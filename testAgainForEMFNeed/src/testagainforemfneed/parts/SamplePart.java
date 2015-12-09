/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package testagainforemfneed.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import info.novatec.inspectit.rcp.handlers.ShowRepositoryHandler;
import testagainforemfneed.handlers.AboutHandler;

public class SamplePart {

	private TableViewer tableViewer;

	@Inject
	private MDirtyable dirty;
	public String text;
	
//	@Inject
//	public void testPrefs(@Preference(nodePath = "/default/"+ InspectIT.ID)
//	        IEclipsePreferences preferences)  throws BackingStoreException 
//	{
//	    preferences.put("DUMMY","DUMMYVALUE222");    
//	    
//	    preferences.flush();
//	}
	
	
	@PostConstruct
	public void createComposite(EHandlerService eHandlerService, ECommandService eCommandService ,Composite parent) {
			Button btn = new Button(parent, 1);
			btn.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
	
					//Test
					ParameterizedCommand command =
							eCommandService.createCommand("info.novatec.inspectit.rcp.commands.showRepository", null);
					eHandlerService.activateHandler("info.novatec.inspectit.rcp.commands.showRepository", new ShowRepositoryHandler());
					//mApplication.getContext().set(ShowRepositoryHandler.REPOSITORY_DEFINITION, reposito//ryDefinition);
					try{
						if(eHandlerService.canExecute(command)) {
							 eHandlerService.executeHandler(command);						 
						}
					}
					catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

					
					//Test
					ParameterizedCommand command =
							eCommandService.createCommand(ShowRepositoryHandler.COMMAND, null);
					eHandlerService.activateHandler(ShowRepositoryHandler.COMMAND, new ShowRepositoryHandler());
				//	mApplication.getContext().set(ShowRepositoryHandler.REPOSITORY_DEFINITION, repositoryDefinition);
					try{
						if(eHandlerService.canExecute(command)) {
							 eHandlerService.executeHandler(command);						 
						}
					}
					catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
			});
		

	}

	@Focus
	public void setFocus() {
		//tableViewer.getTable().setFocus();
	}

	@Persist
	public void save() {
		//dirty.setDirty(false);
	}
}