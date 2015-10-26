/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package testagainforemfneed.handlers;

import org.eclipse.e4.core.di.annotations.Execute;


import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.awt.Desktop;
import java.net.URI;


public class OpenHandler {

	
	
	@Execute
	public void execute(Shell shell)throws Exception{
		
		        String url = "http://stackoverflow.com";

		        if (Desktop.isDesktopSupported()) {
		            // Windows
		            Desktop.getDesktop().browse(new URI(url));
		        } else {
		            // Ubuntu
		            Runtime runtime = Runtime.getRuntime();
		            runtime.exec("/usr/bin/firefox -new-window " + url);
		        }
		    }
	}
}
