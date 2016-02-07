package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.cmr.property.update.configuration.ConfigurationUpdate;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.property.CmrConfigurationDialog;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler for starting the CMR configuration.
 * 
 * @author Ivan Senic
 * 
 * #TODO this need to be implemented, it is just into the code for satisfying dependencies to other classes. 
 */
public class CmrConfigurationHandler {

	/**
	 * {@inheritDoc}
	 */
//	@Execute
//	public void execute(ESelectionService eSelectionService, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) throws ExecutionException {
//		StructuredSelection selection = (StructuredSelection) eSelectionService.getSelection();
//		if (selection.getFirstElement() instanceof ICmrRepositoryProvider) {
//			CmrRepositoryDefinition cmrRepositoryDefinition = ((ICmrRepositoryProvider) selection.getFirstElement()).getCmrRepositoryDefinition();
//			CmrConfigurationDialog preferenceDialog = new CmrConfigurationDialog(shell, cmrRepositoryDefinition);
//			preferenceDialog.open();
//			if (Dialog.OK == preferenceDialog.getReturnCode()) {
//				ConfigurationUpdate configurationUpdate = preferenceDialog.getConfigurationUpdate();
//				boolean restartRequired = preferenceDialog.isServerRestartRequired();
//				if (null != configurationUpdate) {
//					boolean executeRestart = false;
//					if (restartRequired) {
//						String msg = "Selected updates need server restart to be effective. Do you want to restart the CMR?";
//						executeRestart = MessageDialog.openQuestion(shell, "CMR Restart Required", msg);
//					}
//					new ConfigurationUpdateJob(cmrRepositoryDefinition, configurationUpdate, executeRestart).schedule();
//				}
//			}
//		}
//	}
//
//	/**
//	 * Job for updating the configuration.
//	 * 
//	 * @author Ivan Senic
//	 * 
//	 */
//	private static final class ConfigurationUpdateJob extends Job {
//
//		/**
//		 * CMR to update.
//		 */
//		private CmrRepositoryDefinition cmrRepositoryDefinition;
//
//		/**
//		 * {@link ConfigurationUpdate}.
//		 */
//		private ConfigurationUpdate configurationUpdate;
//
//		/**
//		 * If user has selected that restart should be automatically executed.
//		 */
//		private boolean executeRestart;
//
//		/**
//		 * Default constructor.
//		 * 
//		 * @param cmrRepositoryDefinition
//		 *            CMR to update.
//		 * @param configurationUpdate
//		 *            {@link ConfigurationUpdate}
//		 * @param executeRestart
//		 *            If user has selected that restart should be automatically executed.
//		 */
//		public ConfigurationUpdateJob(CmrRepositoryDefinition cmrRepositoryDefinition, ConfigurationUpdate configurationUpdate, boolean executeRestart) {
//			super("Update CMR Configuration Job");
//			this.cmrRepositoryDefinition = cmrRepositoryDefinition;
//			this.configurationUpdate = configurationUpdate;
//			this.executeRestart = executeRestart;
//			setUser(true);
//		}
//
//		/**
//		 * {@inheritDoc}
//		 */
//		@Override
//		protected IStatus run(IProgressMonitor monitor) {
//			monitor.beginTask("Updating the CMR configuration", IProgressMonitor.UNKNOWN);
//			if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
//				try {
//					cmrRepositoryDefinition.getCmrManagementService().updateConfiguration(configurationUpdate, executeRestart);
//				} catch (Exception e) {
//					return new Status(Status.ERROR, InspectIT.ID, "Exception occurred trying to update the CMR configuration.", e);
//				}
//				monitor.done();
//				return Status.OK_STATUS;
//			} else {
//				return new Status(Status.ERROR, InspectIT.ID, "Can not update the configuration because selected CMR is offline.");
//			}
//		}
//	}

}
