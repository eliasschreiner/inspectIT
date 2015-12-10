package info.novatec.inspectit.rcp;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView;

public class LifecycleHook {
//
//	@Inject IEventBroker eventBroker;
	
	@ProcessAdditions
	public void processAdditions(IEclipseContext context) {
			
		context.set(RepositoryManagerView.KEY, Boolean.TRUE);
		context.declareModifiable(RepositoryManagerView.KEY);
	//	
//		
//		eventBroker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC,
//						UIEvents.ALL_ELEMENT_ID);						
//		
		
	}
}
