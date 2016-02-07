package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.repository.CmrRepositoryManager.UpdateRepositoryJob;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.progress.IProgressService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler that performs shutdown and restart of the CMR.
 * 
 * @author Ivan Senic
 * 
 */
public class ShutdownCmrHandler{

	/**
	 * Parameter that defines if restart should be executed after along shutdown of CMR.
	 */
	public static final String SHOULD_RESTART_PARAMETER = "shouldRestart";

	/**@Execute marks the method as the executable
	 *
	 *@param cmd
	 *			the executing command
	 *@param progressServiceInject
	 *			progress Service for executing stuff and threads
	 *@param shell 
	 *			active shell
	 *@param eSelectionService 
	 *			selection service for getting and setting selection
	 */
	@Execute
	public void execute(ParameterizedCommand cmd,IProgressService progressServiceInject, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, ESelectionService eSelectionService) throws ExecutionException {
		
		System.out.println("sdfg");
		String param;
		IParameter param2 = null;
		try {
			param2 = cmd.getCommand().getParameter(SHOULD_RESTART_PARAMETER);
		} catch (NotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		param = param2.toString();
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		if (StringUtils.isNotEmpty(param) && selection.getSelection() instanceof StructuredSelection) {
			final boolean shouldRestart = Boolean.parseBoolean(param);
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				final CmrRepositoryDefinition cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				String cmrName = "'" + cmrRepositoryDefinition.getName() + "' (" + cmrRepositoryDefinition.getIp() + ":" + cmrRepositoryDefinition.getPort() + ")";
				boolean confirm;
				if (shouldRestart) {
					confirm = MessageDialog.openConfirm(shell, "Restart CMR", "Are you sure you want to restart the CMR " + cmrName + "?");
				} else {
					confirm = MessageDialog.openConfirm(shell, "Shutdown CMR", "Are you sure you want to shutdown the CMR " + cmrName + "?");
				}
				if (confirm) {
					IProgressService progressService = progressServiceInject;
					try {
						progressService.busyCursorWhile(new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								if (shouldRestart) {
									monitor.beginTask("Restarting the CMR", IProgressMonitor.UNKNOWN);
									cmrRepositoryDefinition.getCmrManagementService().restart();
								} else {
									monitor.beginTask("Shutting down the CMR", IProgressMonitor.UNKNOWN);
									cmrRepositoryDefinition.getCmrManagementService().shutdown();
								}

								cmrRepositoryDefinition.changeOnlineStatus(OnlineStatus.CHECKING);
								cmrRepositoryDefinition.changeOnlineStatus(OnlineStatus.OFFLINE);

								// we first sleep so that CMR can shutdown
								// this will ensure that this method does not return to fast and CMR
								// is still online
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									Thread.interrupted();
								}

								// then if we are restarting wait until we are up
								if (shouldRestart) {
									monitor.beginTask("Waiting for the CMR to be online again", IProgressMonitor.UNKNOWN);
									while (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.ONLINE) {
										// first let the CMR restart, we can not invoke the service
										// right away cause it can end up in endless service request
										try {
											Thread.sleep(2000);
										} catch (InterruptedException e) {
											Thread.interrupted();
										}

										// we force status update and wait until job has finished
										UpdateRepositoryJob updateRepositoryJob = InspectIT.getDefault().getCmrRepositoryManager().forceCmrRepositoryOnlineStatusUpdate(cmrRepositoryDefinition);
										updateRepositoryJob.join();

										if (monitor.isCanceled()) {
											return;
										}
									}
								}
								monitor.done();
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {
						throw new ExecutionException("Exception occurred during execution of shutdown/restart handler", e);
					}
				}
			}
		}
	}
}
