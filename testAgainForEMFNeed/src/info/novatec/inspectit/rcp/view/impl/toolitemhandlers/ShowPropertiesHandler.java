package info.novatec.inspectit.rcp.view.impl.toolitemhandlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;

public class ShowPropertiesHandler {
	
	private RepositoryManagerView repositoryManagerView;

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void run(MHandledToolItem mHandledToolItem, EPartService ePartService) {
		repositoryManagerView = (RepositoryManagerView) ePartService.findPart(RepositoryManagerView.VIEW_ID).getObject();
		mHandledToolItem.setSelected(true);
		
		if (mHandledToolItem.isSelected()) {
			repositoryManagerView.setShowProperties(false);
			mHandledToolItem.setTooltip("Show Properties");
			mHandledToolItem.setSelected(false);
		} else {
			repositoryManagerView.setShowProperties(true);
			mHandledToolItem.setTooltip("Hide Properties");
			mHandledToolItem.setSelected(true);
		}
	}
}
