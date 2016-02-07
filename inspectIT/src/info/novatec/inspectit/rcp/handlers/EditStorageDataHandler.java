package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.dialog.EditRepositoryDataDialog;
import info.novatec.inspectit.rcp.provider.IStorageDataProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.storage.StorageData;
import info.novatec.inspectit.storage.serializer.SerializationException;

import java.io.IOException;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Edit storage name and description handler.
 * 
 * @author Ivan Senic
 * 
 */
public class EditStorageDataHandler {

	/**@Execute tags the method as the executable of the class
	 * 
	 * @param shell
	 * 			active shell
	 * @param eSelectionService
	 * 			Service to get and set selections
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, ESelectionService eSelectionService) throws ExecutionException {
		TreeViewer selection;
		//Proof whether selection is null or  not
		if(eSelectionService.getSelection()!=null)
		{	
		selection = (TreeViewer) eSelectionService.getSelection();
		IStorageDataProvider storageDataProvider = null;
		Object selectedElement = ((StructuredSelection) selection.getSelection()).getFirstElement();
		if (selectedElement instanceof IStorageDataProvider) {
			storageDataProvider = (IStorageDataProvider) selectedElement;
		} else {
		}

		StorageData storageData = storageDataProvider.getStorageData();
		EditRepositoryDataDialog editStorageDataDialog = new EditRepositoryDataDialog(shell, storageData.getName(), storageData.getDescription());
		editStorageDataDialog.open();
		if (editStorageDataDialog.getReturnCode() == EditRepositoryDataDialog.OK) {
			CmrRepositoryDefinition cmrRepositoryDefinition = storageDataProvider.getCmrRepositoryDefinition();
			if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
				try {
					storageData.setName(editStorageDataDialog.getName());
					storageData.setDescription(editStorageDataDialog.getDescription());
					cmrRepositoryDefinition.getStorageService().updateStorageData(storageData);
					try {
						InspectIT.getDefault().getInspectITStorageManager().storageRemotelyUpdated(storageData);
					} catch (SerializationException | IOException e) {
						throw new ExecutionException("Storage data update failed.", e);
					}
				} catch (BusinessException e) {
					throw new ExecutionException("Storage data update failed.", e);
				}
			} else {
				InspectIT.getDefault().createInfoDialog("Storage data can not be updated, because the underlying repository is currently offline.", -1);
			}
		}
		}
	}
}
