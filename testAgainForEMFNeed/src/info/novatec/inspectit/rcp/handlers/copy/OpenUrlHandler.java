package info.novatec.inspectit.rcp.handlers.copy;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.documentation.DocumentationService;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.inject.Named;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents.Context;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler that opens that InspectIT Documentation page on Confluence.
 * 
 * @author Ivan Senic
 * 
 */
public class OpenUrlHandler{

	@Inject ECommandService commandService;
	@Inject EHandlerService handlerService;
	
	/**
	 * {@inheritDoc}
	 */
	@Execute
	public Object execute(ExecutionEvent event) throws ExecutionException {
	//	@Inject IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				
		String urlString = getUrlString(event);
	
		if (null == urlString) {
			return null;
		}

		try {
//			Browser browser = new Browser(parent,0);
			URL url = new URL(urlString);
//		browser.setUrl(url.toString());
			
			Program.launch(url.toString());
		} catch (MalformedURLException e) {
			throw new ExecutionException("Error opening the URL ('" + urlString + "') in the system browser.", e);
		}
		return null;
	}

	
	
	/**
	 * Implementing classes should return the correct URL string to open.
	 * 
	 * @param event
	 *            {@link ExecutionEvent} that activated the handler.
	 * @return URL as a string or <code>null</code> to signal the abort of the action due to the not
	 *         regular behavior.
	 */
	protected String getUrlString(ExecutionEvent event){
		
		return "http://www.google.de";
	};

	/**
	 * Handler for opening the Confluence documentation.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	public class OpenDocumentationHandler extends OpenUrlHandler {

		/**
		 * Documentation Service.
		 */
		protected DocumentationService documentationService = InspectIT.getService(DocumentationService.class);

		/**
		 * {@inheritDoc}
		 */	
		protected String getUrlString(ExecutionEvent event) {
			return documentationService.getDocumentationUrl();
		}
		
		@Execute
		public Object execute(ExecutionEvent event) throws ExecutionException {
		//	@Inject IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
			
			
		//	String urlString = getUrlString(event);
			String urlString = "http://www.google.de";
			if (null == urlString) {
				return null;
			}

			try {
//				Browser browser = new Browser(parent,0);
				URL url = new URL(urlString);
//			browser.setUrl(url.toString());
				
				Program.launch(url.toString());
			} catch (MalformedURLException e) {
				throw new ExecutionException("Error opening the URL ('" + urlString + "') in the system browser.", e);
			}
			return null;
		}	
	}

	/**
	 * Handler for staring the feedback email.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	public static class GiveFeedbackHandler extends OpenUrlHandler {

		@Execute
		public Object execute(ExecutionEvent event) throws ExecutionException {
		//	@Inject IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					
			String urlString = getUrlString(event);
		
			if (null == urlString) {
				return null;
			}

			try {
//				Browser browser = new Browser(parent,0);
				URL url = new URL(urlString);
//			browser.setUrl(url.toString());
				
				Program.launch(url.toString());
			} catch (MalformedURLException e) {
				throw new ExecutionException("Error opening the URL ('" + urlString + "') in the system browser.", e);
			}
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getUrlString(ExecutionEvent event) {
			return "mailto:info.inspectit@novatec-gmbh.de&subject=Feedback";
		}
	}

	/**
	 * Handler for staring the support email.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	public static class RequestSupportHandler extends OpenUrlHandler {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected String getUrlString(ExecutionEvent event) {
			return "mailto:support.inspectit@novatec-gmbh.de&subject=Support%20needed";
		}
	}

	/**
	 * Handler for sending the exception to the support mail.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	public static class ExceptionSupportHandler extends OpenUrlHandler {

		/**
		 * ID of the command.
		 */
		public static final String COMMAND = "info.novatec.inspectit.rcp.sendErrorReport";

		/**
		 * ID of the input which should be exception.
		 */
		public static final String INPUT = "info.novatec.inspectit.rcp.sendErrorReport.throwable";

		
		//Das wieder durch die alte ersetzen, soweit wie möglich
		@Override
		protected String getUrlString(ExecutionEvent event) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
	/*	@Override
		protected String getUrlString(ExecutionEvent event) {
			// Get the exception out of the context
			IEvaluationContext context = (IEvaluationContext) event.getApplicationContext();
			Throwable throwable = (Throwable) context.getVariable(INPUT);
			if (null == throwable) {
				return null;
			}

			// create body
			StringBuilder body = new StringBuilder("I would like to report the following exception that occurred in inspectIT:\n\n");
			body.append("inspectIT Version: ");
			body.append(InspectIT.getDefault().getBundle().getVersion());
			body.append("\nOperating system: ");
			body.append(SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " (" + SystemUtils.OS_ARCH + ")"); // NOPMD
			body.append("\nJava version: ");
			body.append(SystemUtils.JAVA_VENDOR + " " + SystemUtils.JAVA_VERSION); // NOPMD
			body.append("\nException type: ");
			body.append(throwable.getClass().getName());
			body.append("\nException message: ");
			body.append(throwable.getMessage());
			body.append("\nStack trace (limited): ");
			String stackTrace = ExceptionUtils.getStackTrace(throwable);
			int limit = stackTrace.length() > 1000 ? 1000 : stackTrace.length();
			body.append(stackTrace.subSequence(0, limit));
			body.append("\n\nBest regards,\nYour Name");

			String result = "mailto:support.inspectit@novatec-gmbh.de&subject=Support%20needed&body=";
			try {
				// encode body
				result += URLEncoder.encode(body.toString(), "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")")
						.replaceAll("\\%7E", "~");
			} catch (UnsupportedEncodingException exception) {
				return result;
			}
			return result;
		}*/
	}

	/**
	 * Handler for searching the documentation.
	 * 
	 * @author Ivan Senic
	 * 
	 */
//	public static class SearchDocumentationHandler extends OpenDocumentationHandler {
//
//		/**
//		 * Parameter for the SearchDocumentationHandler.
//		 */
//		public static final String SEARCH_DOCUMENTATION_PARAMETER = "info.novatec.inspectit.rcp.commands.searchDocumentation.searchString";
//
//		/**
//		 * {@inheritDoc}
//		 */
//		@Override
//		protected String getUrlString(ExecutionEvent event) {
//			String param = event.getParameter(SEARCH_DOCUMENTATION_PARAMETER);
//			return documentationService.getSearchUrlFor(param);
//		}
//	}
}

