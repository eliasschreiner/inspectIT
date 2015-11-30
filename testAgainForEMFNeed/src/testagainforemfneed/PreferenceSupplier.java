package testagainforemfneed;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceSupplier {

 public static final IScopeContext SCOPE_CONTEXT = InstanceScope.INSTANCE;
		    public static final String PREFERENCE_NODE = "testAgainForEMFNeed";
		
		    public static final String P_STRING = "string";
		    public static final String DEF_STRING = "Hello World";
		    public static final String P_BOOLEAN = "boolean";
		    public static final boolean DEF_BOOLEAN = true;
		    public static final String P_DOUBLE = "double";
		    public static final double DEF_DOUBLE = 3.14159d;
		    public static final String P_FLOAT = "float";
		    public static final float DEF_FLOAT = 1.618f;
		    public static final String P_INT = "int";
		    public static final int DEF_INT = 42;
		    public static final String P_LONG = "long";
		    public static final long DEF_LONG = 23;
		 
	public static Map<String, String> getInitializationEntries() {
		 
		
	        Map<String, String> entries = new HashMap<String, String>();
	        //
	        entries.put(P_STRING, DEF_STRING);
	        entries.put(P_BOOLEAN, Boolean.toString(DEF_BOOLEAN));
	        entries.put(P_DOUBLE, Double.toString(DEF_DOUBLE));
	        entries.put(P_FLOAT, Float.toString(DEF_FLOAT));
	        entries.put(P_INT, Integer.toString(DEF_INT));
	        entries.put(P_LONG, Long.toString(DEF_LONG));
	        //
	        return entries;
	    }
	
	public static IEclipsePreferences getPreferences() {
		 
        return SCOPE_CONTEXT.getNode(PREFERENCE_NODE);
    }
	
}
