package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.communication.data.cmr.CmrStatusData;
import info.novatec.inspectit.rcp.InspectITConstants;
import info.novatec.inspectit.rcp.formatter.NumberFormatter;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryAndAgentProvider;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.wizard.CopyBufferToStorageWizard;
import info.novatec.inspectit.storage.recording.RecordingState;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Copy buffer to storage handler.
 * 
 * @author Ivan Senic
 * 
 */
public class CopyBufferToStorageHandler {

	
	boolean visible = false;
	
	 /**
	  * 	Sets the enabled/disabled-Attribute via the @CanExecute-Annotation. 	 
	  */
	@CanExecute
	public boolean isVisible(@Optional @UIEventTopic(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC) String test, 
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
				if(cmrRepositoryDefinition.getOnlineStatus() == OnlineStatus.ONLINE)	
				{			
					visible = true;
				}			
				else
				{
					visible = false;
				}
			}
			else
			{visible = false;}
		}
		return visible;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(ESelectionService eSelectionService, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) throws ExecutionException {
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		if (selection.getSelection() instanceof StructuredSelection) {
			CmrRepositoryDefinition suggestedCmrRepositoryDefinition = null;
			Collection<PlatformIdent> autoSelectedAgents = Collections.emptyList();
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				suggestedCmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
			} else if (selectedObject instanceof ICmrRepositoryAndAgentProvider) {
				suggestedCmrRepositoryDefinition = ((ICmrRepositoryAndAgentProvider) selectedObject).getCmrRepositoryDefinition();
				autoSelectedAgents = Collections.singletonList(((ICmrRepositoryAndAgentProvider) selectedObject).getPlatformIdent());
			}
			if (null != suggestedCmrRepositoryDefinition) {
				// check if the writing state is OK
				try {
					CmrStatusData cmrStatusData = suggestedCmrRepositoryDefinition.getCmrManagementService().getCmrStatusData();
					if (cmrStatusData.isWarnSpaceLeftActive()) {
						String leftSpace = NumberFormatter.humanReadableByteCount(cmrStatusData.getStorageDataSpaceLeft());
						if (!MessageDialog.openQuestion(shell, "Confirm", "For selected CMR there is an active warning about insufficient storage space left. Only "
								+ leftSpace + " are left on the target server, are you sure you want to continue?")) {
							
						}
					}
				} catch (Exception e) { // NOPMD NOCHK
					// ignore because if we can not get the info. we will still respond to user
					// action
				}

				CopyBufferToStorageWizard wizard = new CopyBufferToStorageWizard(suggestedCmrRepositoryDefinition, autoSelectedAgents);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			}
		}
	}
	
	

}
