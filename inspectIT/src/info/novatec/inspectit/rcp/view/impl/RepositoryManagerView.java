package info.novatec.inspectit.rcp.view.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.progress.IProgressConstants;
import org.eclipse.e4.ui.progress.UIJob;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import info.novatec.inspectit.cmr.model.PlatformIdent;
import info.novatec.inspectit.communication.data.cmr.AgentStatusData;
import info.novatec.inspectit.exception.BusinessException;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.editor.tree.DeferredTreeViewer;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;
import info.novatec.inspectit.rcp.form.CmrRepositoryPropertyForm;
import info.novatec.inspectit.rcp.formatter.ImageFormatter;
import info.novatec.inspectit.rcp.formatter.TextFormatter;
import info.novatec.inspectit.rcp.handlers.ShowRepositoryHandler;
import info.novatec.inspectit.rcp.model.AgentLeaf;
import info.novatec.inspectit.rcp.model.Component;
import info.novatec.inspectit.rcp.model.DeferredAgentsComposite;
import info.novatec.inspectit.rcp.provider.ICmrRepositoryProvider;
import info.novatec.inspectit.rcp.repository.CmrRepositoryChangeListener;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition;
import info.novatec.inspectit.rcp.repository.CmrRepositoryDefinition.OnlineStatus;
import info.novatec.inspectit.rcp.repository.CmrRepositoryManager;
import info.novatec.inspectit.rcp.repository.CmrRepositoryManager.UpdateRepositoryJob;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;
import info.novatec.inspectit.rcp.view.IRefreshableView;
import info.novatec.inspectit.rcp.view.tree.TreeContentProvider2;
import info.novatec.inspectit.util.ObjectUtils;  
/**
 * Repository manager view where user can work with repositories, check agents, and give input for
 * the data explorer view.
 * 
 * @author Ivan Senic
 * 
 */
public class RepositoryManagerView implements IRefreshableView, CmrRepositoryChangeListener {

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
	 * Tree Viewer.
	 */
	private DeferredTreeViewer treeViewer;

	/**
	 * Composite for displaying the messages.
	 */
	private Composite messageComposite;

	/**
	 * CMR property form.
	 */
	private CmrRepositoryPropertyForm cmrPropertyForm;

	public static final String KEY = "recordingExpression"; //$NON-NLS-1$
	
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
	public boolean showOldAgents = false;

	/**
	 * {@link AgentStatusUpdateJob}.
	 */
	private AgentStatusUpdateJob agentStatusUpdateJob;

	/**
	 * List of the objects that is expanded in the tree.
	 */
	private List<Object> expandedList;
	
	@Inject ESelectionService eSelectionService;
	@Inject EMenuService eMenuService;	
	@Inject EHandlerService eHandlerService;
	@Inject ECommandService eCommandService;
	@Inject	private IEventBroker eventBroker; 
	@Inject MApplication mApplication;
	@Inject EModelService eModelService;
	@Inject MPart mPart;
	/**
	 * Default constructor.
	 */
	public RepositoryManagerView()  {
		cmrRepositoryManager = InspectIT.getDefault().getCmrRepositoryManager();
		cmrRepositoryManager.addCmrRepositoryChangeListener(this);
		
		createInputList();				
	}	
	
	@PostConstruct
	public void createComposite(Composite parent, MApplication mApplication)		
	{	
		this.mApplication = mApplication;	
		toolkit = new FormToolkit(parent.getDisplay());

		mainComposite = new SashForm(parent, SWT.VERTICAL);
		GridLayout mainLayout = new GridLayout(1, true);
		mainLayout.marginWidth = 0;
		mainLayout.marginHeight = 0;
		mainComposite.setLayout(mainLayout);

		mainForm = toolkit.createForm(mainComposite);
		mainForm.getBody().setLayout(new GridLayout(1, true));
		mainForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		toolkit.decorateFormHeading(mainForm);

		Tree tree = toolkit.createTree(mainForm.getBody(), SWT.V_SCROLL | SWT.H_SCROLL);
		treeViewer = new DeferredTreeViewer(tree);

		// create tree content provider, why is there a TreecontentProvider2 ? 
		TreeContentProvider2 treeContentProvider = new TreeContentProvider2() {
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}
				if (inputElement instanceof Collection) {
					return ((Collection<Object>) inputElement).toArray();
				}
				return new Object[0];
			}
		};
		// add the listener that will expand all levels of the agent tree that were expanded before
		// the update
		treeContentProvider.addUpdateCompleteListener(new ExpandFoldersUpdateCompleteListener());
		treeViewer.setContentProvider(treeContentProvider);

		treeViewer.setLabelProvider(new RepositoryTreeLabelProvider());
		treeViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof info.novatec.inspectit.rcp.model.Composite && !(e2 instanceof info.novatec.inspectit.rcp.model.Composite)) {
					return -1;
				} else if (!(e1 instanceof info.novatec.inspectit.rcp.model.Composite) && e2 instanceof info.novatec.inspectit.rcp.model.Composite) {
					return 1;
				} else if (e1 instanceof Component && e2 instanceof Component) {
					return ((Component) e1).getName().compareToIgnoreCase(((Component) e2).getName());
				} else if (e1 instanceof Component) {
					return 1;
				} else if (e2 instanceof Component) {
					return -1;
				} else {
					return 0;
				}

			}
		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection structuredSelection = (StructuredSelection) event.getSelection();
				if (structuredSelection.getFirstElement() instanceof DeferredAgentsComposite) {
					lastSelectedRepository = (DeferredAgentsComposite) structuredSelection.getFirstElement();
					
					//Sends an Event to the EventBus, if the selection has been changed, so ToolItems can actualize their enabled/disabled-Status.
					//UIEvents ensures that it uses the UI-Thread for doing so. 
					//post-Method is making this Thread async. (for sync. use send-Method)
					if(eventBroker != null)
						eventBroker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);		

				}
			}
		});
		treeViewer.addDoubleClickListener(new RepositoryManagerDoubleClickListener());
		ColumnViewerToolTipSupport.enableFor(treeViewer, ToolTip.NO_RECREATE);
		treeViewer.setInput(inputList);

		cmrPropertyForm = new CmrRepositoryPropertyForm(mainComposite);
		cmrPropertyForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		treeViewer.addSelectionChangedListener(cmrPropertyForm);

		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);		
		Control control = treeViewer.getControl();
		Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
		eMenuService.registerContextMenu(control, MENU_ID); //treeViewer brauch ich nicht, da die Selection via eSelectionService läuft.
		
		mainComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				int width = mainComposite.getBounds().width;
				int height = mainComposite.getBounds().height;

				if (width > height && verticaLayout) {
					verticaLayout = false;
					mainComposite.setOrientation(SWT.HORIZONTAL);
				} else if (width < height && !verticaLayout) {
					verticaLayout = true;
					mainComposite.setOrientation(SWT.VERTICAL);
				}

				mainComposite.layout();
			}
		});

		updateFormBody();
		mainComposite.setWeights(new int[] { 2, 3 });

		eSelectionService.setSelection(treeViewer); 
		agentStatusUpdateJob = new AgentStatusUpdateJob();
		
	}

	/**
	 * Updates the repository map.
	 */
	public void createInputList() {
		inputList.clear();
		List<CmrRepositoryDefinition> repositories = cmrRepositoryManager.getCmrRepositoryDefinitions();
		for (CmrRepositoryDefinition cmrRepositoryDefinition : repositories) {
			
			//Injects the application-Context into all camRepositoryDefinitions, so they have access to the EventBroker-Service
			if(mApplication != null)
			{
				ContextInjectionFactory.inject(cmrRepositoryDefinition, mApplication.getContext());
			}			
			//#TODO delete after Implementation of ShowOldAgents Toggle-Button. 
			showOldAgents = true;
			inputList.add(new DeferredAgentsComposite(cmrRepositoryDefinition, showOldAgents));
			OnlineStatus onlineStatus = cmrRepositoryDefinition.getOnlineStatus();
			if (onlineStatus == OnlineStatus.ONLINE || onlineStatus == OnlineStatus.OFFLINE) {
				cachedStatusMap.put(cmrRepositoryDefinition, onlineStatus);
			}
		}
	}

	/**
	 * Updates body.
	 */
	public void updateFormBody() {
		clearFormBody();
		if (!inputList.isEmpty()) {
			treeViewer.getTree().setVisible(true);
			treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			treeViewer.setInput(inputList);
			treeViewer.expandAll();
			if (null != lastSelectedRepository && inputList.contains(lastSelectedRepository)) {
				StructuredSelection ss = new StructuredSelection(lastSelectedRepository);
				treeViewer.setSelection(ss, true);
			}
		} else {
			displayMessage("No CMR repository present. Please add the CMR repository via 'Add CMR repository' action.", Display.getDefault().getSystemImage(SWT.ICON_INFORMATION));
		}
		mainForm.getBody().layout();
	}

	/**
	 * Clears the look of the forms body.
	 */
	private void clearFormBody() {
		if (messageComposite != null && !messageComposite.isDisposed()) {
			messageComposite.dispose();
		}
		treeViewer.setInput(Collections.emptyList());
		treeViewer.getTree().setVisible(false);
		treeViewer.getTree().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	}

	/**
	 * Displays the message on the provided composite.
	 * 
	 * @param text
	 *            Text of message.
	 * @param image
	 *            Image to show.
	 */
	private void displayMessage(String text, Image image) {
		if (null == messageComposite || messageComposite.isDisposed()) {
			messageComposite = toolkit.createComposite(mainForm.getBody());
		} else {
			for (Control c : messageComposite.getChildren()) {
				if (!c.isDisposed()) {
					c.dispose();
				}
			}
		}
		messageComposite.setLayout(new GridLayout(2, false));
		messageComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		toolkit.createLabel(messageComposite, null).setImage(image);
		toolkit.createLabel(messageComposite, text, SWT.WRAP).setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
	}

	/**
	 * {@inheritDoc}
	 */
	@Focus
	public void setFocus() {
		if (treeViewer.getTree().isVisible()) {
			treeViewer.getTree().setFocus();
		} else {
			mainForm.setFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryOnlineStatusUpdated(final CmrRepositoryDefinition repositoryDefinition, OnlineStatus oldStatus, OnlineStatus newStatus) {
		if (newStatus != OnlineStatus.CHECKING) {
			OnlineStatus cachedStatus = cachedStatusMap.get(repositoryDefinition);
			if (null != cachedStatus) {
				if (cachedStatus != newStatus) { // NOPMD
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							mainForm.setBusy(true);
							for (DeferredAgentsComposite composite : inputList) {
								if (ObjectUtils.equals(composite.getRepositoryDefinition(), repositoryDefinition)) {
									treeViewer.refresh(composite, true);
									treeViewer.expandAll();
									if (ObjectUtils.equals(composite, lastSelectedRepository)) {
										if (null != lastSelectedRepository && inputList.contains(lastSelectedRepository)) {
											treeViewer.setSelection(StructuredSelection.EMPTY);
											StructuredSelection ss = new StructuredSelection(lastSelectedRepository);
											treeViewer.setSelection(ss, true);
										}
									}
								}
							}
							mainForm.setBusy(false);
						}
					});
				}
			}
			cachedStatusMap.put(repositoryDefinition, newStatus);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryDataUpdated(final CmrRepositoryDefinition cmrRepositoryDefinition) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				mainForm.setBusy(true);
				for (DeferredAgentsComposite composite : inputList) {
					if (ObjectUtils.equals(composite.getRepositoryDefinition(), cmrRepositoryDefinition)) {
						treeViewer.refresh(composite);
						break;
					}
				}
				mainForm.setBusy(false);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryAdded(CmrRepositoryDefinition cmrRepositoryDefinition) {
		final DeferredAgentsComposite newComposite = new DeferredAgentsComposite(cmrRepositoryDefinition, showOldAgents);
		inputList.add(newComposite);
		OnlineStatus onlineStatus = cmrRepositoryDefinition.getOnlineStatus();
		cachedStatusMap.put(cmrRepositoryDefinition, onlineStatus);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				mainForm.setBusy(true);
				if (inputList.size() > 1) {
					treeViewer.refresh();
					if (null != lastSelectedRepository && inputList.contains(lastSelectedRepository)) {
						StructuredSelection ss = new StructuredSelection(lastSelectedRepository);
						treeViewer.setSelection(ss, true);
					}
				} else {
					updateFormBody();
				}
				mainForm.setBusy(false);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryRemoved(final CmrRepositoryDefinition cmrRepositoryDefinition) {
		DeferredAgentsComposite toRemove = null;
		for (DeferredAgentsComposite composite : inputList) {
			if (ObjectUtils.equals(composite.getRepositoryDefinition(), cmrRepositoryDefinition)) {
				toRemove = composite;
				break;
			}
		}
		if (null != toRemove) {
			inputList.remove(toRemove);
		}
		cachedStatusMap.remove(cmrRepositoryDefinition);

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (ObjectUtils.equals(cmrRepositoryDefinition, lastSelectedRepository.getRepositoryDefinition())) {
					// reset selection if removed repository is selected
					treeViewer.setSelection(StructuredSelection.EMPTY);
				}
				if (inputList.isEmpty()) {
					updateFormBody();
				} else {
					treeViewer.refresh();
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryAgentDeleted(CmrRepositoryDefinition cmrRepositoryDefinition, PlatformIdent agent) {
		DeferredAgentsComposite toUpdate = null;
		for (DeferredAgentsComposite composite : inputList) {
			if (ObjectUtils.equals(composite.getRepositoryDefinition(), cmrRepositoryDefinition)) {
				toUpdate = composite;
				break;
			}
		}

		if (null != toUpdate) {
			final DeferredAgentsComposite finalToUpdate = toUpdate;
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					treeViewer.refresh(finalToUpdate, true);
					if (ObjectUtils.equals(finalToUpdate, lastSelectedRepository)) {
						treeViewer.setSelection(StructuredSelection.EMPTY);
						StructuredSelection ss = new StructuredSelection(finalToUpdate);
						treeViewer.setSelection(ss, true);
						if (null != cmrPropertyForm && !cmrPropertyForm.isDisposed()) {
							cmrPropertyForm.refresh();
						}
					}
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Refreshes all repositories.
	 */
	public synchronized void refresh() {
		// preserve what elements need to be expanded after refresh
		Object[] expandedElements = treeViewer.getExpandedElements();
		Set<Object> parents = new HashSet<Object>();
		for (Object expanded : expandedElements) {
			Object parent = ((ITreeContentProvider) treeViewer.getContentProvider()).getParent(expanded);
			while (parent != null) {
				parents.add(parent);
				parent = ((ITreeContentProvider) treeViewer.getContentProvider()).getParent(parent);
			}
		}
		expandedList = new ArrayList<Object>(Arrays.asList(expandedElements));
		expandedList.removeAll(parents);

		// execute refresh
		Collection<UpdateRepositoryJob> jobs = cmrRepositoryManager.forceAllCmrRepositoriesOnlineStatusUpdate();
		for (final UpdateRepositoryJob updateRepositoryJob : jobs) {
			updateRepositoryJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					CmrRepositoryDefinition cmrRepositoryDefinition = updateRepositoryJob.getCmrRepositoryDefinition();
					DeferredAgentsComposite toUpdate = null;
					for (DeferredAgentsComposite composite : inputList) {
						if (ObjectUtils.equals(composite.getRepositoryDefinition(), cmrRepositoryDefinition)) {
							toUpdate = composite;
							break;
						}
					}
					if (null != toUpdate) {
						final DeferredAgentsComposite finalToUpdate = toUpdate;
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								treeViewer.refresh(finalToUpdate, true);
								if (ObjectUtils.equals(finalToUpdate, lastSelectedRepository)) {
									if (null != cmrPropertyForm && !cmrPropertyForm.isDisposed()) {
										cmrPropertyForm.refresh();
									}
								}
							}
						});
					}
					updateRepositoryJob.removeJobChangeListener(this);
				}
			});
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canRefresh() {
		return !inputList.isEmpty() || !cmrRepositoryManager.getCmrRepositoryDefinitions().isEmpty();
	}

	/**
	 * Show or hides properties.
	 * 
	 * @param show
	 *            Should properties be shown.
	 */
	public void setShowProperties(boolean show) {
		if (show) {
			CmrRepositoryDefinition cmrRepositoryDefinition = null;
			StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
			if (!selection.isEmpty()) {
				if (selection.getFirstElement() instanceof ICmrRepositoryProvider) {
					cmrRepositoryDefinition = ((ICmrRepositoryProvider) selection.getFirstElement()).getCmrRepositoryDefinition();
				}
			}

			cmrPropertyForm = new CmrRepositoryPropertyForm(mainComposite, cmrRepositoryDefinition);
			cmrPropertyForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			treeViewer.addSelectionChangedListener(cmrPropertyForm);
			mainComposite.setWeights(new int[] { 2, 3, 0 });
			mainComposite.layout();
		} else {
			if (null != cmrPropertyForm && !cmrPropertyForm.isDisposed()) {
				treeViewer.removeSelectionChangedListener(cmrPropertyForm);
				cmrPropertyForm.dispose();
				cmrPropertyForm = null; // NOPMD
			}
			
			mainComposite.setWeights(new int[] { 1 , 0, 0});
			mainComposite.layout();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@PreDestroy
	public void dispose() {
		cmrRepositoryManager.removeCmrRepositoryChangeListener(this);
		agentStatusUpdateJob.cancel();
	}
	
	/**
	 * Double click listener for the view.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private class RepositoryManagerDoubleClickListener implements IDoubleClickListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void doubleClick(final DoubleClickEvent event) {
			UIJob openDataExplorerJob = new UIJob("Opening Data Explorer..") {
				
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {					
					process();
					return Status.OK_STATUS;
				}
			};
			openDataExplorerJob.setUser(true);
			openDataExplorerJob.schedule();
		}

		/**
		 * Processes the double-click.
		 * 
		 */
		private void process() {
			RepositoryDefinition repositoryDefinition = null;
			PlatformIdent platformIdent = null;

			StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof DeferredAgentsComposite) {
				repositoryDefinition = ((DeferredAgentsComposite) firstElement).getRepositoryDefinition();
			} else if (firstElement instanceof AgentLeaf) {
				platformIdent = ((AgentLeaf) firstElement).getPlatformIdent();
				Component parent = ((AgentLeaf) firstElement).getParent();
				while (null != parent) {
					if (parent instanceof DeferredAgentsComposite) {
						repositoryDefinition = ((DeferredAgentsComposite) parent).getRepositoryDefinition();
						break;
					}
					parent = parent.getParent();
				}
			}

			if (null != repositoryDefinition) { 				
				
				ParameterizedCommand command =
						eCommandService.createCommand(ShowRepositoryHandler.COMMAND, null);
				eHandlerService.activateHandler(ShowRepositoryHandler.COMMAND, new ShowRepositoryHandler());
				mApplication.getContext().set(ShowRepositoryHandler.REPOSITORY_DEFINITION, repositoryDefinition);
				try{
					if(eHandlerService.canExecute(command)) {
						 eHandlerService.executeHandler(command);						 
					}
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				if (treeViewer.getExpandedState(firstElement)) {
					treeViewer.collapseToLevel(firstElement, 1);
				} else {
					treeViewer.expandToLevel(firstElement, 1);
				}
			}
		}
	}

	/**
	 * Label provider for the tree.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private static class RepositoryTreeLabelProvider extends StyledCellIndexLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Image getColumnImage(Object element, int index) {
			if (element instanceof DeferredAgentsComposite) {
				CmrRepositoryDefinition cmrRepositoryDefinition = (CmrRepositoryDefinition) ((DeferredAgentsComposite) element).getRepositoryDefinition();
				return ImageFormatter.getCmrRepositoryImage(cmrRepositoryDefinition, true);
			} else if (element instanceof AgentLeaf) {
				return ImageFormatter.getAgentImage(((AgentLeaf) element).getAgentStatusData());
			} else if (element instanceof Component) {
				return ((Component) element).getImage();
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected StyledString getStyledText(Object element, int index) {
			if (element instanceof DeferredAgentsComposite) {
				CmrRepositoryDefinition cmrRepositoryDefinition = (CmrRepositoryDefinition) ((DeferredAgentsComposite) element).getRepositoryDefinition();
				return new StyledString(cmrRepositoryDefinition.getName());
			} else if (element instanceof AgentLeaf) {
				return TextFormatter.getStyledAgentLeafString((AgentLeaf) element);
			} else if (element instanceof Component) {
				return new StyledString(((Component) element).getName());
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getToolTipText(Object element) {
			if (element instanceof AgentLeaf) {
				return "Double click to explore Agent in the Data Explorer";
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point getToolTipShift(Object object) {
			int x = 5;
			int y = 5;
			return new Point(x, y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getToolTipDisplayDelayTime(Object object) {
			return 500;
		}
	}

	/**
	 * Job for auto-update of view.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	public final class AgentStatusUpdateJob extends Job {

		/**
		 * Update rate in milliseconds. Currently every 60 seconds.
		 */
		private static final long UPDATE_RATE = 60 * 1000L;

		/**
		 * Default constructor.
		 */
		public AgentStatusUpdateJob() {
			super("Agents status auto-update");
			setUser(false);		
			setProperty(IProgressConstants.ICON_PROPERTY, InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_AGENT));
			schedule(UPDATE_RATE);
		}

		/**
		 * {@inheritDoc}
		 */

		protected IStatus run(IProgressMonitor monitor) {
			updateAgentsAndCmrStatus();
			schedule(UPDATE_RATE);
			return Status.OK_STATUS;
		}

		/**
		 * Updates the agent status for each CMR and updates the displayed CMR repository.
		 */
		private void updateAgentsAndCmrStatus() {
			if (null != cmrPropertyForm && !cmrPropertyForm.isDisposed()) {
				cmrPropertyForm.refresh();
			}
			if (null != inputList) {
				final List<Object> toUpdate = new ArrayList<Object>();
				for (DeferredAgentsComposite agentsComposite : inputList) {
					CmrRepositoryDefinition cmrRepositoryDefinition = agentsComposite.getCmrRepositoryDefinition();
					List<?> leafs = agentsComposite.getChildren();
					if (CollectionUtils.isNotEmpty(leafs) && cmrRepositoryDefinition.getOnlineStatus() != OnlineStatus.OFFLINE) {
						Map<PlatformIdent, AgentStatusData> statusMap = cmrRepositoryDefinition.getGlobalDataAccessService().getAgentsOverview();
						for (Object child : leafs) {
							if (child instanceof AgentLeaf) {
								AgentLeaf agentLeaf = (AgentLeaf) child;
								AgentStatusData agentStatusData = statusMap.get(agentLeaf.getPlatformIdent());
								agentLeaf.setAgentStatusData(agentStatusData);
								toUpdate.add(agentLeaf);
							}
						}
					}
				}
				if (CollectionUtils.isNotEmpty(toUpdate)) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							treeViewer.update(toUpdate.toArray(), null);
						}
					});
				}

			}
		}
	}

	
	
	/**
	 * Listener that is added to the {@link TreeContentProvider} and is invoked when job of updating
	 * the tree elements is done. At this point this listener will re-expand all the before expanded
	 * elements.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private class ExpandFoldersUpdateCompleteListener extends JobChangeAdapter {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void done(IJobChangeEvent event) {
			synchronized (RepositoryManagerView.this) {
				if (CollectionUtils.isNotEmpty(expandedList) && CollectionUtils.isNotEmpty(inputList)) {
					for (Iterator<?> it = expandedList.iterator(); it.hasNext();) {
						Object o = it.next();
						if (treeViewer.isExpandable(o) && !treeViewer.getExpandedState(o)) {
							treeViewer.expandObject(o, 1);
							it.remove();
						}
					}
				}
			}
		}
	}

}
