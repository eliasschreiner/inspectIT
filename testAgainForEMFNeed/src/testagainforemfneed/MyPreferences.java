package testagainforemfneed;

import java.util.Map;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;



public class MyPreferences extends AbstractPreferenceInitializer {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void initializeDefaultPreferences() {
			
			Preferences defaults = DefaultScope.INSTANCE.getNode(Activator.ID);
			  // Set defaults using things like:
			  defaults.put("DUMMY", "DUMMYCONTENT");
			  try {
				defaults.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
			
			  //And this other approach to make sure that one of them works
			  IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			  store.setDefault("DUMMY", "DUMMYCONTENT");		 
			  try {
				  ((Preferences) store).flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		        Map<String, String> initializationEntries = PreferenceSupplier.getInitializationEntries();
		        for(Map.Entry<String, String> entry : initializationEntries.entrySet()) {
		            store.setDefault(entry.getKey(), entry.getValue());
		        }
			  
		}

}
