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
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import testagainforemfneed.parts.Secondsample;

import java.awt.Desktop;
import javax.inject.Named;


public class OpenHandler {

	
	
	@Execute
	public void execute(MApplication application, @Optional @Named(IServiceConstants.ACTIVE_SHELL) Shell shelly) throws InterruptedException {
		   MWindow mWindow = MBasicFactory.INSTANCE.createTrimmedWindow();
		    mWindow.setHeight(200);
		    mWindow.setWidth(400);
		    mWindow.getChildren().add(MBasicFactory.INSTANCE.createPart());
		    application.getChildren().add(mWindow);
	    
	    
	}
	
}
