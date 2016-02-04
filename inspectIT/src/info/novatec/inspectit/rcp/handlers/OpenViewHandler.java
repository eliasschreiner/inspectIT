package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.List;

import com.esotericsoftware.kryo.io.Input;

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
if (null != inputDefinition) {
	try {
		boolean existingPart = false;	
		PlatformIdent newPlatformIdent = inputDefinition.getRepositoryDefinition().getCachedDataService().getPlatformIdentForId(inputDefinition.getIdDefinition().getPlatformId());
		for(MPart part :  ePartService.getParts())
		{					
			if(part.getObject() instanceof FormRootEditor)
			{
				AbstractRootEditor rootEditor = (AbstractRootEditor) part.getObject();
				String id = rootEditor.getInputDefinition().getId().getFqn() ;
				if(id == inputDefinition.getId().getFqn())					
				{
					InputDefinition foreignPartsInput  = rootEditor.getInputDefinition();	
					PlatformIdent existingPlatformIdent = foreignPartsInput.getRepositoryDefinition().getCachedDataService().getPlatformIdentForId(foreignPartsInput.getIdDefinition().getPlatformId());
					if(newPlatformIdent.getId() == existingPlatformIdent.getId())
					{
						existingPart = true;
						ePartService.activate(part);
						break;
					}								
				}						
				else
					existingPart = false;	
			}
		}
		if(!(existingPart))
		{					
			RootEditorInput input = new RootEditorInput(inputDefinition);
			MPart newEditorPart = ePartService.createPart(FormRootEditor.ID);
			MPartStack editorPartStack = (MPartStack) eModelService.find("info.novatec.inspectit.rcp.editor", mApplication);
			editorPartStack.getChildren().add(newEditorPart);
			newEditorPart.getTransientData().put("RootEditorInput", input);
			ePartService.showPart(newEditorPart, PartState.ACTIVATE);					
		}
	} catch (Exception e) {
		throw new ExecutionException("Exception occurred trying to open the editor.", e);
	}
			
		}
	}
}
