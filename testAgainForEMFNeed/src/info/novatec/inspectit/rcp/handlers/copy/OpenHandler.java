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
package info.novatec.inspectit.rcp.handlers.copy;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.documentation.DocumentationService;

public class OpenHandler {	
	/**
	 * Documentation Service.
	 */
//	protected DocumentationService documentationService = InspectIT.getService(DocumentationService.class);
//
//	/**
//	 * {@inheritDoc}
//	 */	
//	protected String getUrlString() {
//		return documentationService.getDocumentationUrl();
//	}
	
	protected String getUrlString(ExecutionEvent event) {
		return "mailto:support.inspectit@novatec-gmbh.de&subject=Support%20needed";
	}
	
	String url = "http://www.google.de";
	Browser browser;	
	
	@Execute
	public void execute(ExecutionEvent event) {
		//url=getUrlString(event);		
		
		Program.launch(url);
	}
}
