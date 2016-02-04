package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.cmr.service.IGlobalDataAccessService;
import info.novatec.inspectit.communication.data.cmr.AgentStatusData;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.model.AgentLeaf;
import info.novatec.inspectit.rcp.model.Component;
import info.novatec.inspectit.rcp.model.DeferredAgentsComposite;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler for deleting the agent from the CMR.
 * 
 * @author Ivan Senic
 * 
 */
public class DeleteAgentHandler{

	@CanExecute
	public boolean isVisible(@Optional @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, ESelectionService eSelectionService)
	{
		if(eSelectionService == null) return false;
		if(shell == null) return false;
		if(eSelectionService.getSelection() == null) return false;
		{
			TreeViewer treeViewer= (TreeViewer) eSelectionService.getSelection();
			if(!(treeViewer.getSelection() instanceof StructuredSelection))
				{
					return false;
				}
		}
			
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(@Optional @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, ESelectionService eSelectionService) throws ExecutionException {
		TreeViewer treeViewer = (TreeViewer) eSelectionService.getSelection();
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		boolean confirmed = MessageDialog.openConfirm(shell, "Confirm Delete",
				"Are you sure you want to permanently delete the selected Agent(s)? Note that all monitoring data related to the Agent(s) will be deleted from the repository database."); 
		
		if (confirmed) {
			for (Iterator<?> it = selection.iterator(); it.hasNext();) {
				Object selected = (Object) it.next();
				
				//#TODO delete this DeferredAgent-Thing
				if (selected instanceof DeferredAgentsComposite)
				{
					DeferredAgentsComposite defAgentsLeaf = (DeferredAgentsComposite) selected;
					CmrRepositoryDefinition cmrRepositoryDefinition = defAgentsLeaf.getCmrRepositoryDefinition();
					Map<PlatformIdent, AgentStatusData> map = (Map<PlatformIdent, AgentStatusData>) cmrRepositoryDefinition.getGlobalDataAccessService().getAgentsOverview();
					AgentStatusData leaf = map.get(1);
					//cmrRepositoryDefinition.getGlobalDataAccessService().deleteAgent(leaf.getId());
					System.out.println("asf");
					
					//defAgentsLeaf.getChildren().clear();
					
//					PlatformIdent platformIdent
//					try {
//						cmrRepositoryDefinition.getGlobalDataAccessService().deleteAgent(platformIdent.getId());
//						InspectIT.getDefault().getCmrRepositoryManager().repositoryAgentDeleted(cmrRepositoryDefinition, platformIdent);
//					} catch (BusinessException e) {
//						throw new ExecutionException("Exception occurred trying to delete the Agent from the CMR.", e);
//					}
					
				}
				
				
				if (selected instanceof AgentLeaf) {
					AgentLeaf agentLeaf = (AgentLeaf) selected;
					PlatformIdent platformIdent = agentLeaf.getPlatformIdent();
					CmrRepositoryDefinition cmrRepositoryDefinition = agentLeaf.getCmrRepositoryDefinition();

					try {
						cmrRepositoryDefinition.getGlobalDataAccessService().deleteAgent(platformIdent.getId());
						InspectIT.getDefault().getCmrRepositoryManager().repositoryAgentDeleted(cmrRepositoryDefinition, platformIdent);
					} catch (BusinessException e) {
						throw new ExecutionException("Exception occurred trying to delete the Agent from the CMR.", e);
					}
				}
			}
		}
	}
}
