package info.novatec.inspectit.rcp.handlers;


import info.novatec.inspectit.rcp.handlers.CloseStorageHandler.FinalizeStorageJob;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.provider.IStorageDataProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;
import info.novatec.inspectit.rcp.storage.InspectITStorageManager;
import info.novatec.inspectit.rcp.view.impl.DataExplorerView;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.storage.StorageData;
import info.novatec.inspectit.storage.serializer.SerializationException;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
/**
 * This handler starts the storage finalization job, and when it is done opens the finalized storage
 * in the {@link DataExplorerView}.
 * 
 * @author Ivan Senic
 * 
 */
public class CloseAndShowStorageHandler {

	/**
	 * Command id.
	 */
	public static final String COMMAND = "info.novatec.inspectit.rcp.commands.closeAndShowStorage";

	/**
	 * Parameter id.
	 */
	public static final String STORAGE_DATA_PROVIDER = "info.novatec.inspectit.rcp.commands.closeAndShowStorage.param";

	@Inject EPartService ePartService;
	@Inject EHandlerService eHandlerService;
	@Inject ECommandService eCommandService;
	@Inject MApplication mApplication;
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public Object execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, final ExecutionEvent event) throws ExecutionException {
		IStorageDataProvider storageDataProvider = (IStorageDataProvider) mApplication.getContext().get(STORAGE_DATA_PROVIDER);// HandlerUtil.getVariable(event, STORAGE_DATA_PROVIDER);

		final StorageData storageData = storageDataProvider.getStorageData();
		final CmrRepositoryDefinition cmrRepositoryDefinition = storageDataProvider.getCmrRepositoryDefinition();

		if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
			FinalizeStorageJob finalizeStorageJob = new FinalizeStorageJob(storageData, cmrRepositoryDefinition);
			finalizeStorageJob.schedule();
			finalizeStorageJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent jobEvent) {
					Job mountStorageJob = new Job("Mounting Storage") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							SubMonitor subMonitor = SubMonitor.convert(monitor);
							InspectITStorageManager storageManager = InspectIT.getDefault().getInspectITStorageManager();
							RepositoryDefinition repositoryDefinition = null;
							try {
								storageManager.mountStorage(storageData, cmrRepositoryDefinition, subMonitor);
								repositoryDefinition = storageManager.getStorageRepositoryDefinition(storageManager.getLocalDataForStorage(storageData));
							} catch (BusinessException | SerializationException | IOException exception) {
								return new Status(IStatus.ERROR, InspectIT.ID, "There was an exception trying to open the storage.", exception);
							}

							// find views
							final MPart page = ePartService.getActivePart(); //HandlerUtil.getActiveSite(event).getPage();
							final RepositoryDefinition finalRepositoryDefinition = repositoryDefinition;
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									MPart dataExplorerView = ePartService.findPart(DataExplorerView.VIEW_ID); // page.findView();
									MPart storageManagerView = ePartService.findPart(StorageManagerView.VIEW_ID); //page.findView(StorageManagerView.VIEW_ID);
									if (dataExplorerView == null) {
										try {
											dataExplorerView = ePartService.showPart(DataExplorerView.VIEW_ID, PartState.ACTIVATE); // page.showView(DataExplorerView.VIEW_ID);
										} catch (Exception e) {
											return;
										}
									}
									if (dataExplorerView instanceof DataExplorerView) {
										ePartService.activate(dataExplorerView); //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(dataExplorerView);
										((DataExplorerView) dataExplorerView).showRepository(finalRepositoryDefinition, null);
									}

									if (storageManagerView instanceof StorageManagerView) {
										((StorageManagerView) storageManagerView).refresh(cmrRepositoryDefinition);
									}
								}
							});
							monitor.done();
							return Status.OK_STATUS;

						}
					};
					mountStorageJob.setUser(true);
					mountStorageJob.schedule();
				}
			});

		} else {
			InspectIT.getDefault().createInfoDialog("Can not finalize storage, CMR repository is offline.", -1);
		}
		return null;
	}
}