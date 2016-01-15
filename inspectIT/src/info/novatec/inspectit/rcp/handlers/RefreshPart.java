package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.editor.ISubView;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.view.IRefreshableView;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

/**
 * Refresh view handler that refresh the current active sub-view.
 * 
 * @author Ivan Senic
 * 
 */
public class RefreshPart {

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart mPart) throws ExecutionException {
		//if it is an Eclipse3-Editor Part
		if (mPart.getObject() instanceof AbstractRootEditor) {
			ISubView subView = ((AbstractRootEditor) mPart).getSubView();
			subView.doRefresh();
		}
		//if it is an Eclipse3-View Part
		if (mPart.getObject() instanceof IRefreshableView) {
			((IRefreshableView) mPart.getObject()).refresh();
		}
	}

}
