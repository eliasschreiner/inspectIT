package info.novatec.inspectit.rcp.wizard;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.model.Composite;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.wizard.page.DefineCmrWizardPage;
import info.novatec.inspectit.rcp.wizard.page.PreviewCmrDataWizardPage;

import java.util.Objects;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * Wizard for adding the {@link info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition}.
 * 
 * @author Ivan Senic
 * 
 * For E4: Proof whether there will be a MWizard in later wizards to make it more independent of JFAce
 */
public class AddCmrRepositoryWizard extends Wizard {

	/**
	 * {@link DefineCmrWizardPage}.
	 */
	private DefineCmrWizardPage defineCmrWizardPage;

	/**
	 * {@link PreviewCmrDataWizardPage}.
	 */
	private PreviewCmrDataWizardPage previewCmrDataWizardPage;

	/** @Execute marks this method as the executable of the wizards. 
	 * Only necessary for wizards that have to be directly executable
	 * #TODO proof if it is allowed to use the execute in wizards  
	 */
	@Execute
	public void execute(Shell parent) throws ExecutionException {
		WizardDialog dialog = new WizardDialog(parent, this);
		dialog.open();		
		}
	
	/**
	 * Default constructor.
	 */
	public AddCmrRepositoryWizard() {
		this.setWindowTitle("Add Central Management Repository (CMR)");
		this.setDefaultPageImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_WIZBAN_SERVER));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		defineCmrWizardPage = new DefineCmrWizardPage("Add New CMR Repository");
		addPage(defineCmrWizardPage);
		previewCmrDataWizardPage = new PreviewCmrDataWizardPage();
		addPage(previewCmrDataWizardPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (Objects.equals(page, defineCmrWizardPage)) {
			previewCmrDataWizardPage.cancel();
			previewCmrDataWizardPage.update(defineCmrWizardPage.getCmrRepositoryDefinition());
		}
		return super.getNextPage(page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		CmrRepositoryDefinition cmrRepositoryDefinition = defineCmrWizardPage.getCmrRepositoryDefinition();
		InspectIT.getDefault().getCmrRepositoryManager().addCmrRepositoryDefinition(cmrRepositoryDefinition);
		return true;
	}

}
