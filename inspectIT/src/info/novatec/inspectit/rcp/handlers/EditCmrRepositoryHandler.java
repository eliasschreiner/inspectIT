package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;
import info.novatec.inspectit.rcp.wizard.EditCmrRepositoryWizard;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Rename the CMR name and description handler.
 * 
 * @author Ivan Senic
 * 
 */
public class EditCmrRepositoryHandler {

	/**@Execute tags the method as the executable of the class
	 * 
	 * @param shell
	 * 			active shell
	 * @param eSelectionService
	 * 			Service to get and set selections
	 * @param ePartService
	 * 			Manages parts
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService ePartService, ESelectionService eSelectionService) throws ExecutionException {
		CmrRepositoryDefinition cmrRepositoryDefinition = null;
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		Object selectedElement = ((StructuredSelection)selection.getSelection()).getFirstElement();
		if (selectedElement instanceof ICmrRepositoryProvider) {
			cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedElement).getCmrRepositoryDefinition();
		} else {
		}

		EditCmrRepositoryWizard editWizard = new EditCmrRepositoryWizard(cmrRepositoryDefinition);
		WizardDialog wizardDialog = new WizardDialog(shell, editWizard);
		if (WizardDialog.OK == wizardDialog.open()) {
			// update view if we have OK from the wizard
			MPart viewPart =  ePartService.findPart(RepositoryManagerView.VIEW_ID); //PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(RepositoryManagerView.VIEW_ID);
			if (viewPart instanceof RepositoryManagerView) {
				((RepositoryManagerView) viewPart).refresh();
			}
		}
	}
}
