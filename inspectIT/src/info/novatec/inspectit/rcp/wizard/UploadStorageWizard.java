package info.novatec.inspectit.rcp.wizard;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.provider.ILocalStorageDataProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.storage.InspectITStorageManager;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.rcp.wizard.page.UploadStorageWizardPage;
import info.novatec.inspectit.storage.LocalStorageData;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.progress.IProgressConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

/**
 * Wizard for uploading a storage.
 * 
 * @author Ivan Senic
 * 
 * For E4: Proof whether there will be a MWizard in later wizards to make it more independent of JFAce
 */
public class UploadStorageWizard extends Wizard{

	/**
	 * Storage to be uploaded.
	 */
	private LocalStorageData localStorageData;

	/**
	 * Wizard page.
	 */
	private UploadStorageWizardPage uploadStorageWizardPage;

	/**
	 * E4 Service for part management
	 */
	@Inject EPartService ePartService;
	
	/**
	 * Default constructor.
	 * 
	 * @param localStorageDataProvider
	 *            {@link ILocalStorageDataProvider} pointing to the storage to upload.
	 */
	public UploadStorageWizard(ILocalStorageDataProvider localStorageDataProvider) {
		this.localStorageData = localStorageDataProvider.getLocalStorageData();
		this.setWindowTitle("Upload Storage to CMR (Central Management Repository)");
		this.setDefaultPageImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_WIZBAN_UPLOAD));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		uploadStorageWizardPage = new UploadStorageWizardPage(localStorageData);
		addPage(uploadStorageWizardPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		final CmrRepositoryDefinition cmrRepositoryDefinition = uploadStorageWizardPage.getCmrRepositoryDefinition();
		Job uploadStorageJob = new Job("Upload storage") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor);
				InspectITStorageManager storageManager = InspectIT.getDefault().getInspectITStorageManager();
				try {
					storageManager.uploadCompleteStorage(localStorageData, cmrRepositoryDefinition, subMonitor);
					cmrRepositoryDefinition.getStorageService().createStorageFromUploadedDir(localStorageData);
				} catch (final Exception e) {
					return new Status(Status.ERROR, InspectIT.ID, "Exception occurred during storage upload", e);
				}

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						InspectIT.getDefault().createInfoDialog("Selected storage successfully uploaded.", -1);
						//Searches for the StorageManagerView and gets it into the MPart 
						MPart storageManagerView = ePartService.findPart(StorageManagerView.VIEW_ID) ;//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(StorageManagerView.VIEW_ID);
						if (storageManagerView instanceof StorageManagerView) {
							((StorageManagerView) storageManagerView).refresh(cmrRepositoryDefinition);
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		uploadStorageJob.setUser(true);
		uploadStorageJob.setProperty(IProgressConstants.ICON_PROPERTY, InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_STORAGE_UPLOAD));
		uploadStorageJob.schedule();
		return true;
	}

}
