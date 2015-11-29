package info.novatec.inspectit.rcp;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;

/**
 * This class controls all aspects of the application's execution.
 */
public class Application implements IApplication {
	
    private static Application instance;

	public static Application getInstance() {
	    return instance;
	}	
	
    private Integer exitRet = IApplication.EXIT_OK;
	private  E4Application e4app;

	/**
	 * {@inheritDoc}
	 */
    
    @Override
	public Object start(IApplicationContext context) throws Exception {	      	
    	instance = this; 
    	e4app = new E4Application();
    	Display display = e4app.getApplicationDisplay();

        try{               	
        	e4app.createE4Workbench(context, display);
        	e4app.start(context);
        } finally {
        	display.dispose();
        }

    
        
		return exitRet;
	}
	
    public void setRestart() {
        exitRet = IApplication.EXIT_RESTART;
    }

	/**
	 * {@inheritDoc}
	 */
    
	public void stop() {
		e4app.stop();
	}

}