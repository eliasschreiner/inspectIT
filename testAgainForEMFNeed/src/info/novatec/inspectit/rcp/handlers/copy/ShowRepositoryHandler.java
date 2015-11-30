package info.novatec.inspectit.rcp.handlers.copy;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.springframework.expression.EvaluationContext;

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;

/**
 * Opens the {@link RepositoryDefinition} in the {@link DataExplorerView}.
 * 
 * @author Ivan Senic
 * 
 */
public class ShowRepositoryHandler  {

	@Inject EPartService ePartService;
	
	/**
	 * The corresponding command id.
	 */
	public static final String COMMAND = "info.novatec.inspectit.rcp.commands.showRepository";

	/**
	 * The repository to look up.
	 */
	public static final String REPOSITORY_DEFINITION = COMMAND + ".repository";

	/**
	 * The repository to look up.
	 */
	public static final String AGENT = COMMAND + ".agent";
	
	@Inject MApplication mApplication;	

	@Execute
	public Object execute(ExecutionEvent event, @Active MWindow activeWindow) throws ExecutionException {
		return activeWindow;
//		// Get the repository definition and agent out of the context
//		RepositoryDefinition repositoryDefinition = (RepositoryDefinition) mApplication.getContext().get(REPOSITORY_DEFINITION);
//		PlatformIdent platformIdent = (PlatformIdent) mApplication.getContext().get(AGENT);
//
//		if (null != repositoryDefinition) {
//			// find view
//			MWindow workbenchWindow;		
//				workbenchWindow = activeWindow; 
//			
//
//			MPart viewPart = ePartService.findPart(DataExplorerView.VIEW_ID); 
//			if (viewPart == null) {
//				try {
//					viewPart = ePartService.showPart(viewPart, PartState.VISIBLE);
//				} catch (Exception e) {
//					return null;
//				}
//			}
//			if (viewPart instanceof DataExplorerView) {
//				ePartService.activate(viewPart);
//				((DataExplorerView) viewPart).showRepository(repositoryDefinition, platformIdent);
//			}
//		}
//		return null;
	}
}
