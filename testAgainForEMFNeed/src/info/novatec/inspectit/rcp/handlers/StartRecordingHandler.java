package info.novatec.inspectit.rcp.handlers;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IPropertyTester;
import org.eclipse.core.internal.expressions.PropertyTesterDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MCoreExpression;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.google.common.annotations.VisibleForTesting;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.communication.data.cmr.CmrStatusData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITConstants;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.formatter.NumberFormatter;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryAndAgentProvider;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.IRefreshableView;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.rcp.wizard.StartRecordingWizard;
import info.novatec.inspectit.storage.recording.RecordingProperties;
import info.novatec.inspectit.storage.recording.RecordingState;

/**
 * Starts recording.
 * 
 * @author Ivan Senic
 * 
 */
public class StartRecordingHandler{

	boolean visible = false;
	
	 /**
	  * 	Sets the enabled/disabled-Attribute via the @CanExecute-Annotation. 
	  *  TODO check for RECORDING_ACTIVE (to not send the event to everyone)	 
	  */
	@CanExecute
	public boolean isVisible(@Optional @UIEventTopic(InspectITConstants.RECORING_ACTIVE) String recoringActive, 
				@Optional @UIEventTopic(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC) String test, 
				ESelectionService eSelectionService) {
		//selected element is active when count = 1, und iterate entweder Klasse ICmrRepositoryAndAgentProvider oder ICmrRepositoryProvider ist und nach recording Active und OnlineStatus getestet wurde... 
		//cmrRepositoryDefinition.getOnlineStatus()
		//
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		CmrRepositoryDefinition cmrRepositoryDefinition;
		if (selection.getSelection() instanceof StructuredSelection) {
			//is null if there isn�t any selected
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();			
			if (selectedObject instanceof ICmrRepositoryProvider || selectedObject instanceof ICmrRepositoryAndAgentProvider) 
			{
				cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				// TODO delete sollowing row, only for test cases... 
				RecordingState status = cmrRepositoryDefinition.getStorageService().getRecordingState();
				if(cmrRepositoryDefinition.getOnlineStatus() == OnlineStatus.ONLINE && cmrRepositoryDefinition.getStorageService().getRecordingState() != RecordingState.ON)	
				{			
					visible = true;
				}			
				else
				{
					visible = false;
				}
			}
		}	
		else
		{visible = false;}
		return visible;
	}

		
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(IEventBroker eventBroker, 
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, 
			EPartService ePartService, ESelectionService eSelectionService) throws ExecutionException {
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
		
		//Post Event to signal the StopRecordingHandler		
		if(eventBroker != null)
			eventBroker.post(InspectITConstants.RECORING_ACTIVE, UIEvents.ALL_ELEMENT_ID);		
			//eventBroker.post(InspectITConstants.RECORING_ACTIVE, "true");
		
	}
}
