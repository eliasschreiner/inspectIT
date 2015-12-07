package info.novatec.inspectit.rcp.handlers;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;
import info.novatec.inspectit.rcp.view.impl.DataExplorerView;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Shell;
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
	public Object execute(@Named(IServiceConstants.ACTIVE_PART) MPart mPart, ExecutionEvent event) throws ExecutionException {
		
		// Get the repository definition and agent out of the context
		IEclipseContext context = (IEclipseContext) mApplication.getContext();
		RepositoryDefinition repositoryDefinition = (RepositoryDefinition) context.get(REPOSITORY_DEFINITION);
		PlatformIdent platformIdent = (PlatformIdent) context.get(AGENT);

		if (null != repositoryDefinition) {
			// find view		

			MPart viewPart = ePartService.findPart(DataExplorerView.VIEW_ID); 
			if (viewPart == null) {
				try {
					viewPart = ePartService.showPart(viewPart, PartState.VISIBLE);
				} catch (Exception e) {
					return null;
				}
			}
			if (viewPart instanceof DataExplorerView) {
				ePartService.activate(viewPart);
				((DataExplorerView) viewPart).showRepository(repositoryDefinition, platformIdent);
			}
		}
		return null;
	}
}
