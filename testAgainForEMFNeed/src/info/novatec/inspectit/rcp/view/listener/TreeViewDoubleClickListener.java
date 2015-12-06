package info.novatec.inspectit.rcp.view.listener;

import info.novatec.inspectit.rcp.handlers.ShowRepositoryHandler;
import info.novatec.inspectit.rcp.model.Component;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.UIEvents.Context;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
/**
 * Double click listener for the explorers trees.
 * 
 * @author Ivan Senic
 * 
 */
public class TreeViewDoubleClickListener implements IDoubleClickListener {

	@Inject ECommandService eCommandService;
	@Inject EHandlerService eHandlerService;
	@Inject MApplication mApplication;
	@Inject	private IEventBroker eventBroker; 
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
//		TreeSelection selection = (TreeSelection) event.getSelection();
//		Object element = selection.getFirstElement();
//		if (null != element) {
//			if (((Component) element).getInputDefinition() == null) {
//				TreeViewer treeViewer = (TreeViewer) event.getViewer();
//				TreePath path = selection.getPaths()[0];
//				if (null != path) {
//					boolean expanded = treeViewer.getExpandedState(path);
//					if (expanded) {
//						treeViewer.collapseToLevel(path, 1);
//					} else {
//						treeViewer.expandToLevel(path, 1);
//					}
//				}
//			} else {				
//				Command command = eCommandService.getCommand(OpenViewHandler.COMMAND);
//				if ( eventBroker != null ) //Sends async. for sending sync. use send()
//				     eventBroker.post(OpenViewHandler.COMMAND, new Event());
//				
//				eHandlerService.activateHandler(OpenViewHandler.COMMAND, new Event());
//				
//				ParameterizedCommand parameterCommand = eCommandService.createCommand(OpenViewHandler.COMMAND, null);
//											
//				mApplication.getContext().set(OpenViewHandler.INPUT, ((Component) element).getInputDefinition()); 
//				
//			
//				try {
//					if(eHandlerService.canExecute(parameterCommand)) eHandlerService.executeHandler(parameterCommand);
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}			
//			}
//		}
	}

}
