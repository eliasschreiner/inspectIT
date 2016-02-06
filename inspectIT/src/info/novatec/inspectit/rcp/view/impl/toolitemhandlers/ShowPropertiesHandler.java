package info.novatec.inspectit.rcp.view.impl.toolitemhandlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;


/**
 * Handler for the ToggleButton to show/hide the preference side of the part.
 * 
 * #TODO fix the Handler due to the execution is still buggy
 * 
 */
public class ShowPropertiesHandler {
	
	/**
	 * RepositoryManagerView.go grant access to some methods
	 */
	private RepositoryManagerView repositoryManagerView;

	/**@Execute marks the method as the executable of the class
	 * 
	 * @param mHandledToolItem 
	 * 				the actual E4 tool item 
	 * 
	 * @param ePartService 
	 * 				 service for partmanagement
	 * 
	 * {@inheritDoc}
	 */
	@Execute
	public void run(MHandledToolItem mHandledToolItem, EPartService ePartService) {
		repositoryManagerView = (RepositoryManagerView) ePartService.findPart(RepositoryManagerView.VIEW_ID).getObject();
		
		if (mHandledToolItem.isSelected()) {
			repositoryManagerView.setShowProperties(true);
			mHandledToolItem.setTooltip("Hide Properties");
		} else {
			repositoryManagerView.setShowProperties(false);
			mHandledToolItem.setTooltip("Show Properties");
		}
		//mHandledToolItem.setSelected(!mHandledToolItem.isSelected());
	}
}
