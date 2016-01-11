package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITConstants;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryAndAgentProvider;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.storage.recording.RecordingState;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.progress.IProgressConstants;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
/**
 * Stops recording.
 * 
 * @author Ivan Senic
 * 
 */
public class StopRecordingHandler  {

	
	boolean visible = false;
	
	 /**
	  * 	Sets the enabled/disabled-Attribute via the @CanExecute-Annotation. 	 
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
			//is null if there isn´t any selected
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();			
			if (selectedObject instanceof ICmrRepositoryProvider || selectedObject instanceof ICmrRepositoryAndAgentProvider) 
			{
				cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				if(cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE && cmrRepositoryDefinition.getStorageService().getRecordingState() != RecordingState.OFF)	
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
	public void execute(IEventBroker eventBroker, EPartService ePartService, 
			ESelectionService eSelectionService) throws ExecutionException {
		CmrRepositoryDefinition cmrRepositoryDefinition = null;
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		if (selection.getSelection() instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
			} else if (((StructuredSelection) selection.getSelection()).getFirstElement() instanceof ICmrRepositoryAndAgentProvider) {
				cmrRepositoryDefinition = ((ICmrRepositoryAndAgentProvider) selectedObject).getCmrRepositoryDefinition();
			}
		}
		if (null != cmrRepositoryDefinition) {
			if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
				boolean canStop = cmrRepositoryDefinition.getStorageService().getRecordingState() != RecordingState.OFF;
				if (canStop) {
					final CmrRepositoryDefinition finalCmrRepositoryDefinition = cmrRepositoryDefinition;
					Job stopRecordingJob = new Job("Stop Recording") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								finalCmrRepositoryDefinition.getStorageService().stopRecording();
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {										
										MPart repositoryManagerView = ePartService.findPart(RepositoryManagerView.VIEW_ID);
										if (repositoryManagerView instanceof RepositoryManagerView) {
											((RepositoryManagerView) repositoryManagerView).refresh();
										}
										MPart storageManagerView = ePartService.findPart(StorageManagerView.VIEW_ID);
										if (storageManagerView instanceof StorageManagerView) {
											((StorageManagerView) storageManagerView).refresh(finalCmrRepositoryDefinition);
										}
									}
								});
							} catch (final BusinessException e) {
								return new Status(IStatus.ERROR, InspectIT.ID, "Stopping the recording failed", e);
							}
							return Status.OK_STATUS;
						}
					};
					stopRecordingJob.setUser(true);
					stopRecordingJob.setProperty(IProgressConstants.ICON_PROPERTY, InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_RECORD_STOP));
					stopRecordingJob.schedule();
				}
			} else {
				throw new ExecutionException("Recording can not be stopped, because the repository is currently offline.");
			}
		}		
		//Post Event to signal the StartRecordingHandler		
				if(eventBroker != null)
					eventBroker.post(InspectITConstants.RECORING_ACTIVE, UIEvents.ALL_ELEMENT_ID);		
					//eventBroker.post(InspectITConstants.RECORING_ACTIVE, "false");
	}

}
