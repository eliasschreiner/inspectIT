package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.provider.ILocalStorageDataProvider;
import info.novatec.inspectit.rcp.wizard.UploadStorageWizard;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler for starting the {@link UploadStorageWizard} upon the correct selection.
 * 
 * @author Ivan Senic
 * 
 */
public class UploadStorageHandler {

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, @Named(IServiceConstants.ACTIVE_SELECTION) ISelection selection) throws ExecutionException {
		
		if (selection instanceof StructuredSelection) {
			Object selected = ((StructuredSelection) selection).getFirstElement();
			if (selected instanceof ILocalStorageDataProvider) {
				ILocalStorageDataProvider localStorageDataProvider = (ILocalStorageDataProvider) selected;
				WizardDialog wizardDialog = new WizardDialog(shell, new UploadStorageWizard(localStorageDataProvider));
				wizardDialog.open();
			}

		}
	}

}
