package info.novatec.inspectit.rcp.handlers;
////import info.novatec.inspectit.rcp.InspectIT;
////import info.novatec.inspectit.rcp.documentation.DocumentationService;
//
////import java.io.UnsupportedEncodingException;
////import java.net.MalformedURLException;
////import java.net.URL;
////import java.net.URLEncoder;
////
////import javax.inject.Inject;
//import javax.inject.Named;
//
//
////import org.eclipse.core.commands.AbstractHandler;
////import org.eclipse.core.commands.ExecutionEvent;
////import org.eclipse.core.commands.ExecutionException;
////import org.eclipse.e4.core.commands.ECommandService;
////import org.eclipse.e4.core.commands.EHandlerService;
//import org.eclipse.e4.core.di.annotations.Execute;
//import org.eclipse.e4.core.di.annotations.Optional;
//import org.eclipse.e4.ui.model.application.MApplication;
//import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
//import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
//import org.eclipse.e4.ui.services.IServiceConstants;
////import org.eclipse.e4.ui.workbench.UIEvents.Context;
////import org.eclipse.swt.browser.Browser;
////import org.eclipse.swt.program.Program;
////import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Shell;
//
//import java.awt.Desktop;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import org.eclipse.e4.core.di.annotations.Execute;
//import org.eclipse.e4.ui.model.application.MApplication;
//import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.SWTError;
//import org.eclipse.swt.browser.Browser;
//import org.eclipse.swt.program.Program;
//import org.eclipse.swt.widgets.FileDialog;
//import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.Shell;


import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.CanExecute;
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

/**
 * Handler that opens that InspectIT Documentation page on Confluence.
 * 
 * @author Ivan Senic
 * 
 */
public class TestHandler2{
	/**
	 * Documentation Service.
	 */
	protected DocumentationService documentationService = InspectIT.getService(DocumentationService.class);

	/**
	 * {@inheritDoc}
	 */	
	protected String getUrlString() {
		return documentationService.getDocumentationUrl();
	}
	
	@CanExecute
	public boolean canExecute()
	{
		return true;}
	
	@Execute
	public void execute(Shell shell) throws Exception {
	//	@Inject IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				
		String urlString = getUrlString();
		
		
		try {
//			Browser browser = new Browser(parent,0);
			URL url = new URL(urlString);
//		browser.setUrl(url.toString());
			
			Program.launch(url.toString());
		} catch (Exception e) {
			throw new Exception("Error opening the URL ('" + urlString + "') in the system browser.", e);
		}
	
	}	
}
	


