package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.provider.IStorageDataProvider;
import info.novatec.inspectit.rcp.wizard.DownloadStorageWizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
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
 * Handler for downloading the complete storage.
 * 
 * @author Ivan Senic
 * 
 */
public class DownloadStorageHandler {

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, @Named(IServiceConstants.ACTIVE_SELECTION) ISelection selection)throws ExecutionException {
		if (selection instanceof StructuredSelection) {
			List<IStorageDataProvider> storageDataProviders = new ArrayList<>();
			for (Iterator<?> it = ((StructuredSelection) selection).iterator(); it.hasNext();) {
				IStorageDataProvider storageDataProvider = (IStorageDataProvider) it.next();
				storageDataProviders.add(storageDataProvider);
			}

			if (CollectionUtils.isNotEmpty(storageDataProviders)) {
				WizardDialog wizardDialog = new WizardDialog(shell, new DownloadStorageWizard(storageDataProviders));
				wizardDialog.open();
			}
		}
	}
}
