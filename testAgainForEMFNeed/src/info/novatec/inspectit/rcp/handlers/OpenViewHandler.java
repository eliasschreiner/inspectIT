package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.root.FormRootEditor;
import info.novatec.inspectit.rcp.editor.root.RootEditorInput;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * The open view handler which takes care of opening a view by retrieving the
 * {@link InputDefinition}.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class OpenViewHandler {

	/**
	 * The corresponding command id.
	 */
	public static final String COMMAND = "info.novatec.inspectit.rcp.commands.openView";

	/**
	 * The input definition id to look up.
	 */
	public static final String INPUT = COMMAND + ".input";

	/**
	 * {@inheritDoc}
	 */
	@Execute
	public void execute(EModelService eModelService, EPartService ePartService, MApplication mApplication) throws ExecutionException {
		// Get the input definition out of the context
		IEclipseContext context = (IEclipseContext) mApplication.getContext();
		InputDefinition inputDefinition = (InputDefinition) context.get(INPUT);

		// open the view if the input definition is set
		if (null != inputDefinition) {
			RootEditorInput input = new RootEditorInput(inputDefinition);
			ePartService.getActivePart().getContext().set("RootEditorInput", input);
			try {
				//ePartService.findPart("testPartDescriptor1").setParent((MElementContainer<MUIElement>) eModelService.find("inspectit.partstack.1", ePartService.getActivePart()));
				ePartService.showPart(ePartService.createPart("testPartDescriptor1"), PartState.ACTIVATE);//page.openEditor(input, FormRootEditor.ID);
			} catch (Exception e) {
				throw new ExecutionException("Exception occurred trying to open the editor.", e);
			}
		}
	}
}
