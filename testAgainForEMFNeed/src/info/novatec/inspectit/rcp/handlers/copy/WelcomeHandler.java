package info.novatec.inspectit.rcp.handlers.copy;

import javax.inject.Named;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Klasse WelcomeHandler. Behandelt den Befehl "Willkommen", indem sie
 * eine Willkommensseite aufruft. Bitte beachten Sie, dass die Seite
 * nur dann angezeigt werden kann, wenn eine Internetverbindung zu
 * steppan.net aufgebaut werden kann, wo sich die HTLM-Dateien befinden.
 * 
 * @author Bernhard Steppan
 * (c) 2008 - 2015 Bernhard Steppan
 * Veroeffentlichung und Weitergabe nur mit Genehmigung des Autoren.
 * @version 1.0
 *
 */
public class WelcomeHandler {

  @Execute
  public void execute(MApplication application, EPartService partService, EModelService modelService,
      @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

    MPerspective introPerspective =
        (MPerspective) modelService.find(
            "info.novatec.inspectit.rcp.parts.SamplePart", application); //$NON-NLS-1$
    try {
      partService.switchPerspective(introPerspective);
    } catch (AssertionFailedException e) {
      MessageDialog.open(
          MessageDialog.ERROR, shell,
          "Vocat", 
          "Ein interner Fehler ist aufgetreten.",
          SWT.ICON_ERROR);
    } catch (Exception e) {
      MessageDialog.open(
          MessageDialog.ERROR, shell,
          "Vocat",
          "Ein Datenbankfehler ist aufgetreten.", //$NON-NLS-1$
          SWT.ICON_ERROR);
    }
  }
}