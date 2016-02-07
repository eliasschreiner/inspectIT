package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * handler for removing CMR repository.
 * 
 * @author Ivan Senic
 * 
 */
public class RemoveCmrRepositoryHandler {

	/**@Execute marks the method as the executable
	 *
	 * @param eSelectionService 
	 * 					Service for getting and setting the selection
	 */
	@Execute
	public void execute(ESelectionService eSelectionService) throws ExecutionException {
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		if (selection.getSelection() instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				CmrRepositoryDefinition cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				if (null != cmrRepositoryDefinition) {
					boolean isSure = MessageDialog.openConfirm(
							null,
							"Remove Central Management Repository (CMR)",
							"Are you sure that you want to remove the repository " + cmrRepositoryDefinition.getName() + " (" + cmrRepositoryDefinition.getIp() + ":"
									+ cmrRepositoryDefinition.getPort() + ")?");

					if (isSure) {
						InspectIT.getDefault().getCmrRepositoryManager().removeCmrRepositoryDefinition(cmrRepositoryDefinition);
					}
				}
			}
		}
	}

}
