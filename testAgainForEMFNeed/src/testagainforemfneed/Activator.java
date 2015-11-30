package testagainforemfneed;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	private static Activator plugin;
	
	/**
	 * Preferences store for the plug-in.
	 */
	private volatile static ScopedPreferenceStore  preferenceStore;

	public static final String ID = "testAgainForEMFNeed";
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	    plugin = this;
	}
	

	/**
	 * Returns the shared instance.
	 * 
	 * @return Returns the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static ScopedPreferenceStore getPreferenceStore() {
		 
        if(preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(PreferenceSupplier.SCOPE_CONTEXT, "testAgainForEMFNeed");
        }
        return preferenceStore;
    
}
	

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
