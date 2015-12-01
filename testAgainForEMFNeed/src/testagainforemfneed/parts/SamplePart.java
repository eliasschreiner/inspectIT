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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.preferences.PreferenceSupplier;

public class SamplePart {

	private Text txtInput;
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
	public void createComposite(Composite parent) {
//		text = Activator.getDefault().getPreferenceStore().getDefaultString("DUMMY");
//		
		IEclipsePreferences preferences = PreferenceSupplier.getPreferences();
		int theAnswerToTheQuestionOfAllQuestions = preferences.getInt(PreferenceSupplier.P_INT, PreferenceSupplier.DEF_INT);
		
		String t2 = InspectIT.getDefault().getPreferenceStore().getDefaultString("DUMMY");
		parent.setLayout(new GridLayout(1, false));

		txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter text to mark part as dirty");
		txtInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dirty.setDirty(true);
			}
		});
		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = new TableViewer(parent);

		tableViewer.add("Sample item 1");
		tableViewer.add("Sample item 2");
		tableViewer.add("Sample item 3");
		tableViewer.add("Sample item 4");
		tableViewer.add("Sample item 5");
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Focus
	public void setFocus() {
		tableViewer.getTable().setFocus();
	}

	@Persist
	public void save() {
		dirty.setDirty(false);
	}
}