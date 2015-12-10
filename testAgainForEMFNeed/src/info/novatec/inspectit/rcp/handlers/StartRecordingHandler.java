package info.novatec.inspectit.rcp.handlers;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.progress.IProgressConstants;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.communication.data.cmr.CmrStatusData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.formatter.NumberFormatter;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryAndAgentProvider;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.rcp.wizard.StartRecordingWizard;
import info.novatec.inspectit.storage.recording.RecordingProperties;

/**
 * Starts recording.
 * 
 * @author Ivan Senic
 * 
 */
public class StartRecordingHandler{


	public static final String KEY = "recordingExpression"; //$NON-NLS-1$
	@Inject MApplication mApplication;
	@Inject	IEventBroker eventBroker;
	
	HandledToolItemImpl toolItem; 
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService ePartService,  ESelectionService eSelectionService) throws ExecutionException {
			
		
		// try to get the CMR where recording should start.
		CmrRepositoryDefinition cmrRepositoryDefinition = null;
		Collection<PlatformIdent> autoSelectedAgents = Collections.emptyList();
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		if (selection.getSelection() instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
			} else if (selectedObject instanceof ICmrRepositoryAndAgentProvider) {
				cmrRepositoryDefinition = ((ICmrRepositoryAndAgentProvider) selectedObject).getCmrRepositoryDefinition();
				autoSelectedAgents = Collections.singletonList(((ICmrRepositoryAndAgentProvider) selectedObject).getPlatformIdent());
			}
		}

		// check if the writing state is OK
		if (null != cmrRepositoryDefinition) {
			try {
				CmrStatusData cmrStatusData = cmrRepositoryDefinition.getCmrManagementService().getCmrStatusData();
				if (cmrStatusData.isWarnSpaceLeftActive()) {
					String leftSpace = NumberFormatter.humanReadableByteCount(cmrStatusData.getStorageDataSpaceLeft());
					if (!MessageDialog.openQuestion(shell, "Confirm", "For selected CMR there is an active warning about insufficient storage space left. Only "
							+ leftSpace + " are left on the target server, are you sure you want to continue?")) {
						//return null;
					}
				}
			} catch (Exception e) { // NOPMD NOCHK
				// ignore because if we can not get the info. we will still respond to user action
			}
		}

		// open wizard
		StartRecordingWizard startRecordingWizard = new StartRecordingWizard(cmrRepositoryDefinition, autoSelectedAgents);
		WizardDialog wizardDialog = new WizardDialog(shell, startRecordingWizard);
		wizardDialog.open();

		// if recording has been started refresh the repository and storage manager view
		if (wizardDialog.getReturnCode() == WizardDialog.OK) {
			MPart repositoryManagerView = ePartService.findPart(RepositoryManagerView.VIEW_ID);
			if (repositoryManagerView instanceof RepositoryManagerView) {
				((RepositoryManagerView) repositoryManagerView).refresh();
			}
			MPart storageManagerView = ePartService.findPart(StorageManagerView.VIEW_ID);
			if (storageManagerView instanceof StorageManagerView) {
				if (null != cmrRepositoryDefinition) {
					((StorageManagerView) storageManagerView).refresh(cmrRepositoryDefinition);
				} else {
					((StorageManagerView) storageManagerView).refresh();
				}
			}

			// auto-refresh on recording stop if there is recording duration specified
			RecordingProperties recordingProperties = startRecordingWizard.getRecordingProperties();
			if (null != recordingProperties && recordingProperties.getRecordDuration() > 0) {
				Job refreshStorageManagerJob = new Job("Recording Auto-Stop Updates") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						MPart storageManagerView = ePartService.findPart(StorageManagerView.VIEW_ID);
						if (storageManagerView instanceof StorageManagerView) {
							((StorageManagerView) storageManagerView).refresh();
						}
						return Status.OK_STATUS;
					}
				};
				refreshStorageManagerJob.setUser(false);
				refreshStorageManagerJob.setProperty(IProgressConstants.ICON_PROPERTY, InspectIT.getDefault().getImage(InspectITImages.IMG_RECORD_STOP));
				// add 5 seconds to be sure all is done
				long delay = 5000 + recordingProperties.getRecordDuration() + recordingProperties.getStartDelay();
				refreshStorageManagerJob.schedule(delay);
			}
		}
		//return null;
	}
	
//	@SuppressWarnings("restriction")
//	@PostConstruct
//	public void postConstruct(MApplication application, EModelService modelService, @UIEventTopic(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC) String test, 
//			@Named(KEY) Boolean recordingExpression) {
//		toolItem = (HandledToolItemImpl) modelService.find("info.novatec.inspectit.rcp.toolitems.startRecording", application);
//		String test1 = test;
//		toolItem.setVisible(recordingExpression);
//		
//	}
	
	


	
//	boolean canExecute(MApplication mApplication, @Named(IServiceConstants.ACTIVE_SELECTION) @Optional ISelection selection) {		
	@CanExecute
	@Inject
	@Optional
	boolean canExecute(	@UIEventTopic(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC) String test, 
			@Named(KEY) Boolean debugEnabled)
		{	
		//		boolean visible = null != selection;
//		if(selection instanceof ICmrRepositoryProvider || selection instanceof ICmrRepositoryAndAgentProvider)
//		{
//			
//			
//			
//		}
//			
//		toolItem.setVisible(visible);
		
		Boolean b =  (Boolean) mApplication.getContext().get(RepositoryManagerView.KEY);
		if(debugEnabled!=null)return debugEnabled;
		return b;		
		
	}
	
}
