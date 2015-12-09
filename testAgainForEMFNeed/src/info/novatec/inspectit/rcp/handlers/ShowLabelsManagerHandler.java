package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.rcp.wizard.ManageLabelWizard;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Show labels manager handler.
 * 
 * @author Ivan Senic
 * 
 */
public class ShowLabelsManagerHandler  {

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(ESelectionService eSelectionService, EPartService ePartService, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) throws ExecutionException {
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();
		if (selection.getSelection() instanceof StructuredSelection) {
			Object selectedObject = ((StructuredSelection) selection.getSelection()).getFirstElement();
			if (selectedObject instanceof ICmrRepositoryProvider) {
				CmrRepositoryDefinition cmrRepositoryDefinition = ((ICmrRepositoryProvider) selectedObject).getCmrRepositoryDefinition();
				ManageLabelWizard wizard = new ManageLabelWizard(cmrRepositoryDefinition);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();

				if (wizard.isShouldRefreshStorages()) {
					MPart viewPart = ePartService.findPart(StorageManagerView.VIEW_ID);
					if (viewPart instanceof StorageManagerView) {
						((StorageManagerView) viewPart).refresh(cmrRepositoryDefinition);
					}
				}
			}
		}
	}

}
