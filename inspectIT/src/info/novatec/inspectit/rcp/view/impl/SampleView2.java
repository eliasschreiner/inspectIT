package info.novatec.inspectit.rcp.view.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import info.novatec.inspectit.rcp.form.CmrRepositoryPropertyForm;
import info.novatec.inspectit.rcp.model.DeferredAgentsComposite;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryManager;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.view.impl.RepositoryManagerView.AgentStatusUpdateJob;

public class SampleView2 {
	
	/**
	 * ID of this view.
	 */
	public static final String VIEW_ID = "info.novatec.inspectit.rcp.view.repositoryManager";

	/**
	 * ID for tree menu.
	 */
	private static final String MENU_ID = "info.novatec.inspectit.rcp.view.repositoryManager.repositoryTree";

	/**
	 * {@link CmrRepositoryManager}.
	 */
	private CmrRepositoryManager cmrRepositoryManager;

	/**
	 * Input list.
	 */
	private List<DeferredAgentsComposite> inputList = new ArrayList<DeferredAgentsComposite>();

	/**
	 * Online statuses map.
	 */
	private Map<CmrRepositoryDefinition, OnlineStatus> cachedStatusMap = new ConcurrentHashMap<CmrRepositoryDefinition, OnlineStatus>();

	/**
	 * Toolkit.
	 */
	private FormToolkit toolkit;

	/**
	 * Form for the view.
	 */
	private Form mainForm;

	/**
	 * Composite for displaying the messages.
	 */
	private Composite messageComposite;

	/**
	 * CMR property form.
	 */
	private CmrRepositoryPropertyForm cmrPropertyForm;

	/**
	 * Views main composite.
	 */
	private SashForm mainComposite;

	/**
	 * Boolean for layout of view.
	 */
	private boolean verticaLayout = true;

	/**
	 * Last selected repository, so that the selection can be maintained after the view is
	 * refreshed.
	 */
	private DeferredAgentsComposite lastSelectedRepository = null;

	/**
	 * Defines if agents are shown in the tree which have not sent any data since the CMR was
	 * started.
	 */
	private boolean showOldAgents = false;

	/**
	 * {@link AgentStatusUpdateJob}.
	 */
	private AgentStatusUpdateJob agentStatusUpdateJob;

	/**
	 * List of the objects that is expanded in the tree.
	 */
	private List<Object> expandedList;
	
	@Inject 
	EPartService ePartService;

	@Inject
	private MApplication mApplication;
	
	@Inject
	ESelectionService eSelectionService;
	
	@Inject MPart mPart;
	
	@Inject
	EHandlerService eHandlerService;
	
	@Inject MToolBar mToolBar;
	
	@Inject EMenuService eMenuService;
	
	  private Text text;
	  private Browser browser;
	
	  public SampleView2 () {
		System.out.println("Hi");
	}
	  
	  @PostConstruct
	  public void createControls(Composite parent) {
		    parent.setLayout(new GridLayout(2, false));

		    text = new Text(parent, SWT.BORDER);
		    text.setMessage("Enter City");
		    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		    Button button = new Button(parent, SWT.PUSH);
		    button.setText("Search");
		    button.addSelectionListener(new SelectionAdapter() {
		      @Override
		      public void widgetSelected(SelectionEvent e) {
		        String city = text.getText();
		        if (city.isEmpty()) {
		          return;
		        }
		        try {
		          // not supported at the moment by Google
		          // browser.setUrl("http://maps.google.com/maps?q="
		          // + URLEncoder.encode(city, "UTF-8")
		          // + "&output=embed");
		          browser.setUrl("https://www.google.com/maps/place/"
		              + URLEncoder.encode(city, "UTF-8")
		              + "/&output=embed");

		        } catch (UnsupportedEncodingException e1) {
		          e1.printStackTrace();
		        }
		      }
		    });

		    browser = new Browser(parent, SWT.NONE);
		    browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		  }

		  @Focus
		  public void onFocus() {
		    text.setFocus();
		  }


}
