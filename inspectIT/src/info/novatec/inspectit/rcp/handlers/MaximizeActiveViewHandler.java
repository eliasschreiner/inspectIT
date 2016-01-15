package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.editor.preferences.IPreferencePanel;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.UIEvents.UIElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Handler for the maximize/minimize the active sub-view. At the same time this Handler implements
 * the {@link IElementUpdater} interface so that we can manually update the checked state of the UI
 * elements that are bounded to the {@value #COMMAND_ID} command.
 * 
 * @author Ivan Senic
 * 
 */
public class MaximizeActiveViewHandler{

	@Inject EPartService ePartService;
	@Inject ECommandService eCommandService;
	@Inject EHandlerService eHandlerService;
	
	/**
	 * Command id.
	 */
	public static final String COMMAND_ID = "info.novatec.inspectit.rcp.commands.maximizeActiveView";

	/**
	 * Preference panel id parameter needed for this command.
	 */
	public static final String PREFERENCE_PANEL_ID_PARAMETER = COMMAND_ID + ".preferencePanelId";

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(EModelService eModelService, ExecutionEvent event, @Active MWindow mWindow, MHandledItem mHandledItem) throws ExecutionException {
		MPart editorPart = ePartService.getActivePart();
		if (editorPart instanceof AbstractRootEditor) {
			AbstractRootEditor abstractRootEditor = (AbstractRootEditor) editorPart;
			if (abstractRootEditor.canMaximizeActiveSubView()) {
				abstractRootEditor.maximizeActiveSubView();
			} else if (abstractRootEditor.canMinimizeActiveSubView()) {
				abstractRootEditor.minimizeActiveSubView();
			}
		}

		// after the maximized/minimized is executed we need to refresh the UI elements bounded to
		// the command, so that checked state of that elements is updated
//		Map<Object, Object> filter = new HashMap<Object, Object>();
//		filter.put(IServiceScopes.WINDOW_SCOPE, mWindow);
		List<MHandledItem> elements = eModelService.findElements(mWindow, null, MHandledItem.class, null);
	   // elements.addAll(eModelService.findElements(mWindow, null, MHandledItem.class, null));
	    for( MHandledItem hi : elements ){
			hi.setSelected(mHandledItem.isSelected());
		}
	    
	    
	    //mWindow.updateLocalization(); .refreshElements(event.getCommand().getId(), filter);
	}

	/**
	 * {@inheritDoc}
	 */
//	@SuppressWarnings("rawtypes")
//	@Inject
//	public void updateElement(@Active UIElement element, Map parameters) {
//		// we'll only update the element that is bounded to the preference panel in the active
//		// sub-view	
//		String preferencePanelId = (String) parameters.get(PREFERENCE_PANEL_ID_PARAMETER);
//		if (null != preferencePanelId) {
//			MPart editorPart = ePartService.getActivePart();
//			if (editorPart instanceof AbstractRootEditor) {
//				AbstractRootEditor abstractRootEditor = (AbstractRootEditor) editorPart;
//				IPreferencePanel preferencePanel = abstractRootEditor.getPreferencePanel();
//				if (preferencePanelId.equals(preferencePanel.getId())) {
//					element.(!abstractRootEditor.canMaximizeActiveSubView());
//				}
//			}
//		}
//	}

}
