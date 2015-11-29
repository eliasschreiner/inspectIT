package testagainforemfneed.parts;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Secondsample {
	
	
		   @Inject
		   public Secondsample(Composite parent) {
		      Label label = new Label(parent, SWT.NONE);
		      label.setText("Hello World!");
		   }
		}
	
