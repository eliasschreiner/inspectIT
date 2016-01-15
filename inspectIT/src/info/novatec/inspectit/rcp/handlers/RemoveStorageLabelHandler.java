package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.provider.IStorageDataProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.storage.StorageData;
import info.novatec.inspectit.storage.label.AbstractStorageLabel;
import info.novatec.inspectit.storage.serializer.SerializationException;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Handler for removing a list of labels from storage.
 * 
 * @author Ivan Senic
 * 
 */
public class RemoveStorageLabelHandler {

	/**
	 * Command ID.
	 */
	public static final String COMMAND = "info.novatec.inspectit.rcp.commands.removeStorageLabel";

	/**
	 * Input ID.
	 */
	public static final String INPUT = COMMAND + ".input";

	/**
	 * {@inheritDoc}
	 */
	@Execute
	@SuppressWarnings("unchecked")
	public void execute(MApplication mApplication, ESelectionService eSelectionService, EPartService ePartService) throws ExecutionException {
		// Get the input list out of the context
		IEclipseContext context = mApplication.getContext();//(IEvaluationContext) event.getApplicationContext();
		List<AbstractStorageLabel<?>> inputList = (List<AbstractStorageLabel<?>>) mApplication.getContext().get(INPUT); // context.getVariable(INPUT);

		IStorageDataProvider storageProvider = null;

		// try to get it from selection
		TreeViewer selection = (TreeViewer) eSelectionService.getSelection();// HandlerUtil.getCurrentSelection(event);
		if (selection.getSelection() instanceof StructuredSelection) {
			if (((StructuredSelection) selection.getSelection()).getFirstElement() instanceof IStorageDataProvider) {
				storageProvider = (IStorageDataProvider) ((StructuredSelection) selection.getSelection()).getFirstElement();
			}
		}

		if (null != storageProvider && null != inputList) {
			CmrRepositoryDefinition cmrRepositoryDefinition = storageProvider.getCmrRepositoryDefinition();
			if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
				try {
					StorageData updatedStorageData = cmrRepositoryDefinition.getStorageService().removeLabelsFromStorage(storageProvider.getStorageData(), inputList);
					try {
						InspectIT.getDefault().getInspectITStorageManager().storageRemotelyUpdated(updatedStorageData);
					} catch (SerializationException | IOException e) {
						throw new ExecutionException("Error occured trying to save local storage data to disk.", e);
					}
					MPart viewPart = ePartService.findPart(StorageManagerView.VIEW_ID); // PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(StorageManagerView.VIEW_ID);
					if (viewPart instanceof StorageManagerView) {
						((StorageManagerView) viewPart).refresh(cmrRepositoryDefinition);
					}
				} catch (BusinessException e) {
					throw new ExecutionException("Error occured trying to remove labels from storage.", e);
				}
			} else {
				throw new ExecutionException("Labels could not be removed from storage, because the underlying repository is offline.");
			}
		}
	}
}
