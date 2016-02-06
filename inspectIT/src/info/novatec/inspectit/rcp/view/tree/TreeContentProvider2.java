package info.novatec.inspectit.rcp.view.tree;

import java.io.File;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.e4.ui.internal.workbench.Util;
import org.eclipse.e4.ui.model.application.ui.advanced.util.AdvancedAdapterFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import info.novatec.inspectit.rcp.model.Composite;
import info.novatec.inspectit.rcp.model.DeferredAgentsComposite;
import info.novatec.inspectit.rcp.model.Leaf;
import info.novatec.inspectit.rcp.model.TreeModelManager;
import info.novatec.inspectit.rcp.util.ListenerList;


/**
 * 
 * 
 * #TODO Look for solutions for the missing components of the progress-Bundle of E4
 * This bundle has to be imported because it is not included in the actual build of Mars
 * Maybe a new model should be applied because the DeferredTreeContentManager, the 
 * IDeferredWorkbenchAdapter and the PendingUpdateAdapter are not included, BUT they are 
 * needed for the actual DeferredTree-Model out of the E3 development
 * 
 * a LOT of methods have a reduced functionality. 
 */
public class TreeContentProvider2 implements ITreeContentProvider{
	
	private ListenerList<IJobChangeListener> updateCompleteListenerList;
	
	    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	    }

		/**
		 * Adds the listener to the update job that updates the elements. The listener will be added to
		 * the {@link DeferredTreeContentManager} if one is initialized. In any case the listener will
		 * be added when the new manager is initialized in the future.
		 * 
		 * @param listener
		 *            {@link IJobChangeListener}
		 */
		public void addUpdateCompleteListener(IJobChangeListener listener) {
			if (null == updateCompleteListenerList) {
				updateCompleteListenerList = new ListenerList<IJobChangeListener>();
			}
			updateCompleteListenerList.add(listener);
		}
	    
	    @Override
	    public void dispose() {
	    }

	    @Override
	    public Object[] getElements(Object inputElement) {
	    	TreeModelManager treeModelManager = (TreeModelManager) inputElement;
			return treeModelManager.getRootElements();
	    }

	    @Override
	    public Object[] getChildren(Object parentElement) {
// parentElement only returns 0 - Children. #TODO fix
//	    	if (manager.isDeferredAdapter(parentElement)) {
//				Object[] children = manager.getChildren(parentElement);
	    	if(parentElement instanceof DeferredAgentsComposite)
	    	{
	    		Object[] children = ((DeferredAgentsComposite) parentElement).getChildren(parentElement);// get manager.getChildren(parentElement);

				return children;
	    	}
			if (parentElement instanceof Composite) {
				// direct access to the children
				Composite composite = (Composite) parentElement;
				return composite.getChildren().toArray();
			}
			return new Object[0];
	    }

	    @Override
	    public Object getParent(Object element) {
	    	if (element instanceof Composite) {
				Composite composite = (Composite) element;
				return composite.getParent();
			}

			return null;
	    }

	    @Override
	    public boolean hasChildren(Object element) {
	    	if (null == element) {
				return false;
			}
	    	
	    	if (element instanceof DeferredAgentsComposite) {
				return true;
			}
	    	
	    	if (element instanceof Leaf) {
				return false;
			}
	    	
			if (element instanceof Composite) {
				Composite composite = (Composite) element;
				return composite.hasChildren();
			}	    	
			
	      File file = (File) element;
	      
	      
	      if (file.isDirectory()) {
	        return true;
	      }
	      return false;
	    }
	    
//		public boolean mayHaveChildren(Object element) {
//			element.
//			Assert.isNotNull(element);
//			IAdapterFactory factory = new AdvancedAdapterFactory();
//			factory.getAdapter(element, adapterType)
//			
//			Assert.isNotNull(element,
//					ProgressMessages.DeferredTreeContentManager_NotDeferred);
//			IDeferredWorkbenchAdapter adapter = getAdapter(element);
//			return adapter != null && adapter.isContainer();
//		}
	    
//		public Object[] getChildren(final Object parent) {
//			IDeferredWorkbenchAdapter element = getAdapter(parent);
//			if (element == null) {
//				return null;
//			}
//			PendingUpdateAdapter placeholder = createPendingUpdateAdapter();
//			startFetchingDeferredChildren(parent, element, placeholder);
//			return new Object[] { placeholder };
//		}
}
