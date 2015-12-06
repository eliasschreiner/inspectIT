package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.provider.IStorageDataProvider;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.rcp.wizard.AddStorageLabelWizard;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler for adding a label to storage.
 * 
 * @author Ivan Senic
 * 
 */
public class AddStorageLabelHandler {

	/**
	 * The corresponding command id.
	 */
	public static final String COMMAND = "info.novatec.inspectit.rcp.commands.addStorageLabel";

	@Inject ESelectionService eSelectionService;
	@Inject EPartService ePartService;
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public Object execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, ExecutionEvent event) throws ExecutionException {
		IStorageDataProvider storageProvider = null;

		// try to get it from selection
		ISelection selection = (ISelection) eSelectionService.getSelection(); // HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			if (((StructuredSelection) selection).getFirstElement() instanceof IStorageDataProvider) {
				storageProvider = (IStorageDataProvider) ((StructuredSelection) selection).getFirstElement();
			}
		}

		if (null != storageProvider) {
			AddStorageLabelWizard addStorageLabelWizard = new AddStorageLabelWizard(storageProvider);
			WizardDialog wizardDialog = new WizardDialog(shell, addStorageLabelWizard);
			wizardDialog.open();
			if (wizardDialog.getReturnCode() == WizardDialog.OK) {
				MPart viewPart = ePartService.findPart(StorageManagerView.VIEW_ID);// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(StorageManagerView.VIEW_ID);
				if (viewPart instanceof StorageManagerView) {
					((StorageManagerView) viewPart).refresh(storageProvider.getCmrRepositoryDefinition());
				}
			}
		}

		return null;
	}

}
