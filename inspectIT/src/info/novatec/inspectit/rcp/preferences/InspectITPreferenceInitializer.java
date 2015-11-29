package info.novatec.inspectit.rcp.preferences;

import info.novatec.inspectit.communication.data.ExceptionSensorData;
import info.novatec.inspectit.communication.data.HttpTimerData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.communication.data.SqlStatementData;
import info.novatec.inspectit.communication.data.TimerData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.internal.workbench.Activator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Initializes the default preferences.
 * 
 * @author Patrice Bouillet
 * 
 */
public class InspectITPreferenceInitializer extends AbstractPreferenceInitializer {

//	@Preference(nodePath = "/default/" + InspectIT.ID)
//    IEclipsePreferences preferences) throws BackingStoreException 
	
	public InspectITPreferenceInitializer(){

	}
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		 
		Preferences defaults = DefaultScope.INSTANCE.getNode(InspectIT.ID);
		  // Set defaults using things like:
		  defaults.put("DUMMY", "DUMMYCONTENT");
		  try {
			defaults.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		  //And this other approach to make sure that one of them works
		  IPreferenceStore store = InspectIT.getDefault().getPreferenceStore();
		  store.setDefault("DUMMY", "DUMMYCONTENT");		 
		  try {
			((Preferences) store).flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
       // store.setDefault(PreferencesConstants.CMR_REPOSITORY_DEFINITIONS, true); //statt true muss ich glaub ein stirng geben .. 
		// CMR list
//		List<CmrRepositoryDefinition> defaultCmrList = new ArrayList<CmrRepositoryDefinition>(1); // (1) ??
//		CmrRepositoryDefinition defaultCmr = new CmrRepositoryDefinition(CmrRepositoryDefinition.DEFAULT_IP, CmrRepositoryDefinition.DEFAULT_PORT, CmrRepositoryDefinition.DEFAULT_NAME);
//		defaultCmr.setDescription(CmrRepositoryDefinition.DEFAULT_DESCRIPTION);
//		defaultCmrList.add(defaultCmr);
//		PreferencesUtils.saveCmrRepositoryDefinitions(defaultCmrList, true);
//
//
//		
//		// Editor defaults
//		PreferencesUtils.saveIntValue(PreferencesConstants.DECIMAL_PLACES, 0, true);
//		PreferencesUtils.saveLongValue(PreferencesConstants.REFRESH_RATE, 5000L, true);
//		PreferencesUtils.saveIntValue(PreferencesConstants.ITEMS_COUNT_TO_SHOW, 100, true);
//		PreferencesUtils.saveDoubleValue(PreferencesConstants.INVOCATION_FILTER_EXCLUSIVE_TIME, Double.NaN, true);
//		PreferencesUtils.saveDoubleValue(PreferencesConstants.INVOCATION_FILTER_TOTAL_TIME, Double.NaN, true);
//		Set<Class<?>> invocDataTypes = new HashSet<>();
//		invocDataTypes.add(InvocationSequenceData.class);
//		invocDataTypes.add(TimerData.class);
//		invocDataTypes.add(HttpTimerData.class);
//		invocDataTypes.add(SqlStatementData.class);
//		invocDataTypes.add(ExceptionSensorData.class);
//		PreferencesUtils.saveObject(PreferencesConstants.INVOCATION_FILTER_DATA_TYPES, invocDataTypes, true);
	}

}
