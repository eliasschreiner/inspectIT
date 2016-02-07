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
import org.eclipse.core.expressions.IEvaluationContext;
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

	/**@Execute marks the method as the executable
	 *
	 *@param ePartService
	 *			manages Parts
	 *@param mApplication
	 *			current application
	 *@param mPart
	 *			active part
	 */
	@Execute
	public void execute(EPartService ePartService, MApplication mApplication, @Named(IServiceConstants.ACTIVE_PART) MPart mPart) throws ExecutionException {
		
		// Get the repository definition and agent out of the context
		//IEvaluationContext evacontext = (IEvaluationContext) event.getApplicationContext();
		IEclipseContext context = (IEclipseContext) mApplication.getContext();
		RepositoryDefinition repositoryDefinition = (RepositoryDefinition) context.get(REPOSITORY_DEFINITION);
		PlatformIdent platformIdent = (PlatformIdent) context.get(AGENT);

		if (null != repositoryDefinition) {
			// find view		
			MPart viewPart = ePartService.findPart(DataExplorerView.VIEW_ID); 
			DataExplorerView dev = (DataExplorerView) viewPart.getObject();
			if (dev == null) {
				try {
					viewPart = ePartService.showPart(viewPart, PartState.VISIBLE);
					dev = (DataExplorerView) viewPart.getObject();
				} catch (Exception e) {
				}
			}
			if (dev instanceof DataExplorerView) {
				//ePartService.showPart(viewPart, PartState.VISIBLE);
				ePartService.activate(viewPart);
				dev.showRepository(repositoryDefinition, platformIdent);
			}
		}
	}
}
