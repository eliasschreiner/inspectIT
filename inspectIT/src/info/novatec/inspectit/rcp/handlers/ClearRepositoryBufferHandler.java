package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITConstants;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.editor.root.IRootEditor;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryAndAgentProvider;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.provider.IInputDefinitionProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.StorageRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;
import info.novatec.inspectit.storage.recording.RecordingState;
import info.novatec.inspectit.util.ObjectUtils;

import java.util.Collections;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.progress.IProgressConstants;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler for clearing the repository buffer.
 * 
 * @author Ivan Senic
 * 
 */
public class ClearRepositoryBufferHandler {

	boolean visible = false;
	
	 /**
	  * 	Sets the enabled/disabled-Attribute via the @CanExecute-Annotation. 
	  *  	
	  */
	@CanExecute
	public boolean isVisible(@Optional @UIEventTopic(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC) String test, 
				ESelectionService eSelectionService, EPartService ePartService) {
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		CmrRepositoryDefinition cmrRepositoryDefinition;
		if (selection.getSelection() instanceof StructuredSelection) {
			//is null if there isn´t any selected
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();			
			if (selectedObject instanceof ICmrRepositoryProvider) 
			{
				cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				// TODO delete sollowing row, only for test cases... 
				RecordingState status = cmrRepositoryDefinition.getStorageService().getRecordingState();
				if(cmrRepositoryDefinition.getOnlineStatus() == OnlineStatus.ONLINE)	
				{			
					//#TODO weg
					MPart test2 = ePartService.getActivePart();
					if(ePartService.getActivePart().getObject() instanceof CmrRepositoryDefinition)
					{
						visible = true;
					}else if (ePartService.getActivePart().getObject() instanceof StorageRepositoryDefinition)
					{
						visible = true;
					}
						
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
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart mPart, EPartService ePartService, ESelectionService eSelectionService, final ExecutionEvent event) throws ExecutionException {
		CmrRepositoryDefinition availableCmr = null;

		ISelection selection = (ISelection) eSelectionService.getSelection();
		if (selection instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				ICmrRepositoryProvider cmrRepositoryProvider = (ICmrRepositoryProvider) selectedObject;
				availableCmr = cmrRepositoryProvider.getCmrRepositoryDefinition();
			}
		}
		if (null == availableCmr) {
			MPart editor = mPart;
			if (editor instanceof IInputDefinitionProvider) {
				IInputDefinitionProvider inputDefinitionProvider = (IInputDefinitionProvider) editor;
				if (inputDefinitionProvider.getInputDefinition().getRepositoryDefinition() instanceof CmrRepositoryDefinition) {
					availableCmr = (CmrRepositoryDefinition) inputDefinitionProvider.getInputDefinition().getRepositoryDefinition();
				}
			}
		}

		final CmrRepositoryDefinition cmrRepositoryDefinition = availableCmr;
		if (null != cmrRepositoryDefinition && cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
			boolean isSure = MessageDialog.openConfirm(null, "Empty buffer",
					"Are you sure that you want to completely delete all the data in the buffer on repository " + cmrRepositoryDefinition.getName() + " (" + cmrRepositoryDefinition.getIp() + ":"
							+ cmrRepositoryDefinition.getPort() + ")?");
			if (isSure) {
				Job clearBufferJob = new Job("Clear Respoitory Buffer") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						cmrRepositoryDefinition.getCmrManagementService().clearBuffer();
						Display.getDefault().asyncExec(new Runnable() {
							@SuppressWarnings("null")
							@Override
							public void run() {
								//Instantiate all the Parts that are editors
								MPart[] editors = null;
								int editorCount=0;
								for(MPart part : ePartService.getParts())
								{
									if(part instanceof AbstractRootEditor)
									{
										editors[editorCount] = part;
										editorCount++;
									}
								}
								editorCount=0;
								for (MPart editor : editors) {
									IRootEditor rootEditor = (IRootEditor) editor;
									if (null != rootEditor.getPreferencePanel()) {
										if (rootEditor.getSubView().getPreferenceIds().contains(PreferenceId.CLEAR_BUFFER)) {
											InputDefinition inputDefinition = rootEditor.getInputDefinition();
											if (ObjectUtils.equals(inputDefinition.getRepositoryDefinition(), cmrRepositoryDefinition)) {
												rootEditor.getSubView().setDataInput(Collections.<DefaultData> emptyList());
											}
										}
									}
								}
								MPart viewPart = ePartService.findPart(RepositoryManagerView.VIEW_ID);
								if (viewPart instanceof RepositoryManagerView) {
									((RepositoryManagerView) viewPart).refresh();
								}

							}
						});
						return Status.OK_STATUS;
					}
				};
				clearBufferJob.setUser(true);
				clearBufferJob.setProperty(IProgressConstants.ICON_PROPERTY, InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_BUFFER_CLEAR));
				clearBufferJob.schedule();
			}
		}
	}

}
