package info.novatec.inspectit.rcp.handlers.copy;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.documentation.DocumentationService;

public class OpenDocumentationHandler {
	
	String url = "http://www.google.de";
	Browser browser;	
	
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
	public void execute(Shell shell) {
		/*if (Desktop.isDesktopSupported()) {
            // Windows
            try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            // Ubuntu
            Runtime runtime = Runtime.getRuntime();
            try {
				runtime.exec("/usr/bin/firefox -new-window " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }*/
		Program.launch(url);

}
}