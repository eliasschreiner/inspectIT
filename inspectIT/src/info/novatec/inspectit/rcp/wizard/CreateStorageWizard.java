package info.novatec.inspectit.rcp.wizard;

import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.impl.StorageManagerView;
import info.novatec.inspectit.rcp.wizard.page.DefineNewStorageWizzardPage;
import info.novatec.inspectit.storage.StorageData;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

/**
 * Wizard for creating and opening the storage.
 * 
 * @author Ivan Senic
 * 
 * For E4: Proof whether there will be a MWizard in later wizards to make it more independent of JFAce
 */
public class CreateStorageWizard extends Wizard {

	/**
	 * New storage page.
	 */
	private DefineNewStorageWizzardPage defineNewStoragePage;

	/**
	 * Selected CMR repository.
	 */
	private CmrRepositoryDefinition cmrRepositoryDefinition;

	/**
	 * E4 service for Part Management
	 */
	@Inject EPartService ePartService;
	
	/**
	 * Default constructor.
	 */
	public CreateStorageWizard() {
		super();
		this.setWindowTitle("Create Storage Wizard");
		this.setDefaultPageImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_WIZBAN_STORAGE));
	}

	/**
	 * This constructor will set provided {@link CmrRepositoryDefinition} as the initially selected
	 * repository to create storage to. Force open, means that the option if the storage will be
	 * opened or not, will not be available for the user.
	 * 
	 * @param cmrRepositoryDefinition
	 *            {@link CmrRepositoryDefinition} to create storage on.
	 */
	public CreateStorageWizard(CmrRepositoryDefinition cmrRepositoryDefinition) {
		this();
		this.cmrRepositoryDefinition = cmrRepositoryDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		defineNewStoragePage = new DefineNewStorageWizzardPage(cmrRepositoryDefinition, false);
		addPage(defineNewStoragePage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		CmrRepositoryDefinition cmrRepositoryDefinition = defineNewStoragePage.getSelectedRepository();
		if (cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
			StorageData storageData = defineNewStoragePage.getStorageData();
			try {
				cmrRepositoryDefinition.getStorageService().createAndOpenStorage(storageData);
				MPart viewPart = ePartService.findPart(StorageManagerView.VIEW_ID);
				if (viewPart instanceof StorageManagerView) {
					((StorageManagerView) viewPart).refresh();
				}
			} catch (BusinessException e) {
				InspectIT.getDefault().createErrorDialog("Storage can not be created.", e, -1);
				return false;
			}
		} else {
			InspectIT.getDefault().createErrorDialog("Storage can not be created. Selected CMR repository is currently not available.", -1);
			return false;
		}
		return true;
	}

}
