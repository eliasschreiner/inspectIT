package info.novatec.inspectit.rcp.view.tree;

import java.io.File;

import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import info.novatec.inspectit.rcp.util.ListenerList;

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
	      return (File[]) inputElement;
	    }

	    @Override
	    public Object[] getChildren(Object parentElement) {
	      File file = (File) parentElement;
	      return file.listFiles();
	    }

	    @Override
	    public Object getParent(Object element) {
	      File file = (File) element;
	      return file.getParentFile();
	    }

	    @Override
	    public boolean hasChildren(Object element) {
	      File file = (File) element;
	      if (file.isDirectory()) {
	        return true;
	      }
	      return false;
	    }
	    
	    

	  
	
	

}
