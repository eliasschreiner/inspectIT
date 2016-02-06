package info.novatec.inspectit.rcp.view.impl.toolitemhandlers;

import javax.inject.Inject;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;


/**
 * Handler for show hide agents which have not sent any data.
 * 
 * This shall be the toggle-button to show and hide the inactive agents.
 *
 * #TODO this has to be fixed due to its not working properly. 
 * Therefore some methods of the RepositoryManager should be inspected
 * 
 * @author Patrice Bouillet
 * 
 */
public class ShowAgentsHandler {

	/**
	 * E4 style tool item 
	 */
	private MHandledToolItem mHandledToolItem;
	
	/**
	 * the Repository manager 
	 */
	private RepositoryManagerView repositoryManagerView;


	/**@Execute marks the method as the executive method of this handler. 
	 * 
	 * {@inheritDoc}
	 */
	@Execute
	public void run(MHandledToolItem mHandledToolItem, EPartService ePartService) throws ExecutionException {
		this.mHandledToolItem = mHandledToolItem;
		repositoryManagerView = (RepositoryManagerView) ePartService.findPart(RepositoryManagerView.VIEW_ID).getObject();
		mHandledToolItem.setSelected(repositoryManagerView.showOldAgents);
		updateToolTipText();
		
		repositoryManagerView.showOldAgents = !(mHandledToolItem.isSelected());
		repositoryManagerView.createInputList();
		repositoryManagerView.updateFormBody();
	}

	/**
	 * Updates tool-tip text based on the current state.
	 */
	private void updateToolTipText() {
		if (mHandledToolItem.isSelected()) {
			mHandledToolItem.setTooltip("Hide Agents which have not sent any data yet.");
		} else {
			mHandledToolItem.setTooltip("Show Agents which have not sent any data yet.");
		}
	}
}
