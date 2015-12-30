package info.novatec.inspectit.rcp.editor.preferences;

import info.novatec.inspectit.communication.data.ExceptionSensorData;
import info.novatec.inspectit.communication.data.HttpTimerData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.communication.data.SqlStatementData;
import info.novatec.inspectit.communication.data.TimerData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.action.MenuAction;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceEventCallback.PreferenceEvent;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId.LiveMode;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId.TimeResolution;
import info.novatec.inspectit.rcp.editor.preferences.control.IPreferenceControl;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.editor.root.FormRootEditor;
import info.novatec.inspectit.rcp.handlers.MaximizeActiveViewHandler;
import info.novatec.inspectit.rcp.preferences.PreferencesConstants;
import info.novatec.inspectit.rcp.preferences.PreferencesUtils;
import info.novatec.inspectit.util.ObjectUtils;

import java.awt.MenuItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.Assert;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuFactoryImpl;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.ui.action.ActionMenuItem;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.e4.ui.model.application.ui.menu.MDynamicMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;

/**
 * This is the class where the preference panel is created.
 * 
 * @author Eduard Tudenhoefner
 * @author Patrice Bouillet
 * @author Stefan Siegl
 */
@SuppressWarnings("restriction")
public class FormPreferencePanel implements IPreferencePanel {

	/**
	 * ID of the preference panel.
	 */
	private String id;

	/**
	 * The used toolkit.
	 */
	private final FormToolkit toolkit;

	/**
	 * Callbacks which are containing the fire method which is executed whenever something is
	 * changed and updated.
	 */
	private List<PreferenceEventCallback> callbacks = new ArrayList<PreferenceEventCallback>();

	/**
	 * The button for live mode switching.
	 */
	private Action switchLiveMode;

	/**
	 * The button for switching the preferences.
	 */
	private Action switchPreferences;

	/**
	 * THe button for switching the stepping control.
	 */
	private Action switchSteppingControl;

	/**
	 * The list of created preference controls.
	 */
	private List<IPreferenceControl> preferenceControlList = new ArrayList<IPreferenceControl>();

	/**
	 * The created section.
	 */
	private Section section;

	@Inject MWindow mWindow;
	@Inject EPartService ePartService;
	@Inject EBindingService eBindingService;
	@Inject EModelService eModelService;
	@Inject MApplication mApplication;
	@Inject EMenuService eMenuService;
	@Inject MMenuFactory mMenuFactory;
	@Inject MPart mPart;
	@Inject ECommandService eCommandService;
	
	/**
	 * The constructor which needs a {@link ViewController} reference.
	 * 
	 * @param toolkit
	 *            The Form toolkit which defines the used colors.
	 */
	public FormPreferencePanel(FormToolkit toolkit, EModelService eModelService, MApplication mApplication, MPart mPart) {
		Assert.isNotNull(toolkit);

		this.toolkit = toolkit;
		this.id = UUID.randomUUID().toString();
		this.eModelService = eModelService;
		this.mApplication = mApplication;
		this.mPart = mPart;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerCallback(PreferenceEventCallback callback) {
		Assert.isNotNull(callback);

		callbacks.add(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeCallback(PreferenceEventCallback callback) {
		Assert.isNotNull(callback);

		callbacks.remove(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	public void fireEvent(PreferenceEvent event) {
		for (PreferenceEventCallback callback : callbacks) {
			callback.eventFired(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void createPartControl(Composite parent, Set<PreferenceId> preferenceSet, InputDefinition inputDefinition, IToolBarManager toolBarManager) {
		section = toolkit.createSection(parent, Section.NO_TITLE);
		section.setText("Preferences");
		section.setLayout(new GridLayout(1, false));
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		section.setVisible(false);

		Composite innerComposite = toolkit.createComposite(section);
		innerComposite.setLayout(new GridLayout(1, false));
		innerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// only add buttons and some controls if the set is not empty
		if (null != preferenceSet && !preferenceSet.isEmpty()) {
			if (null != toolBarManager) {
				createButtons(preferenceSet, toolBarManager);
			}
			createPreferenceControls(innerComposite, preferenceSet);
		}

		section.setClient(innerComposite);
		section.setExpanded(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setVisible(boolean visible) {
		section.setVisible(visible);
		section.setExpanded(visible);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disableLiveMode() {
		if (switchLiveMode.isChecked()) {
			switchLiveMode.setChecked(false);
			// switchPreferences.setEnabled(!switchPreferences.isEnabled());

			createLiveModeEvent();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void update() {
		if (switchPreferences.isChecked()) {
			for (IPreferenceControl preferenceControl : preferenceControlList) {
				PreferenceEvent event = new PreferenceEvent(preferenceControl.getControlGroupId());
				event.setPreferenceMap(preferenceControl.eventFired());
				fireEvent(event);
			}
		}

		fireEvent(new PreferenceEvent(PreferenceId.UPDATE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bufferCleared() {
		fireEvent(new PreferenceEvent(PreferenceId.CLEAR_BUFFER));
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSteppingControlChecked(boolean checked) {
		if (null != switchSteppingControl) {
			switchSteppingControl.setChecked(checked);
		}
	}

	/**
	 * Creates the preference controls in the preference control panel.
	 * 
	 * @param parent
	 *            The parent {@link Composite} to which the controls will be added.
	 * @param preferenceSet
	 *            The set containing the preference IDs.
	 */
	private void createPreferenceControls(Composite parent, Set<PreferenceId> preferenceSet) {
		for (PreferenceId preferenceIdEnum : preferenceSet) {
			IPreferenceControl preferenceControl = PreferenceControlFactory.createPreferenceControls(parent, toolkit, preferenceIdEnum, this);
			if (null != preferenceControl) {
				preferenceControlList.add(preferenceControl);
			}
		}
	}

	/**
	 * Creates the buttons for this panel.
	 * 
	 * @param preferenceSet
	 *            the list containing the preference ids.
	 * @param toolBarManager
	 *            The tool bar manager.
	 */
	@Inject
	private void createButtons(Set<PreferenceId> preferenceSet, IToolBarManager toolBarManager) {
		switchLiveMode = new SwitchLiveMode("Live");
		switchPreferences = new SwitchPreferences("Additional options"); // NOPMD
		
		//Test fora more e4 wayofthe toolbar. 
		MToolBar toolbar =  mPart.getToolbar();
		
		MenuAction menuAction = new MenuAction();
		menuAction.setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_TOOL));
		menuAction.setToolTipText("Preferences");
		
//		ToolBarContributionItem item1 = new ToolBarContributionItem(toolBarManager); 

		
		// add the maximize to all forms, let eclipse hide it as declared in plugin.xml
//		Map<Object, Object> params = new HashMap<Object, Object>();
//		params.put(MaximizeActiveViewHandler.PREFERENCE_PANEL_ID_PARAMETER, id);
		
//		MToolBarContribution maximizeCommandContribution = (MToolBarContribution) ePartService.findPart("info.novatec.inspectit.rcp.contributions.maximizeActiveView");
//		MHandledToolItem maximizeCommandContribution = (MHandledToolItem) eModelService.find("info.novatec.inspectit.rcp.contributions.maximizeActiveView", mApplication);

//		CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(workbenchWindow, null, MaximizeActiveViewHandler.COMMAND_ID, params, InspectIT.getDefault()
//				.getImageDescriptor(InspectITImages.IMG_WINDOW), null, null, null, null, getTooltipTextForMaximizeContributionItem(), SWT.CHECK, null, true);
//		CommandContributionItem maximizeCommandContribution = new CommandContributionItem(contributionParameters);
//		toolBarManager.add((IAction) item1);

		toolBarManager.add(new MaximizeActiveViewAction("Maximize active View"));
		if (preferenceSet.contains(PreferenceId.HTTP_AGGREGATION_REQUESTMETHOD)) {
//			MToolBarElement switchHttpCategorizationRequestMethod = mMenuFactory.createDirectToolItem();
//			toolbar.getChildren().add(switchHttpCategorizationRequestMethod);
			
			toolBarManager.add(new SwitchHttpCategorizationRequestMethod("Include Request Method in Categorization"));
		}

		if (preferenceSet.contains(PreferenceId.HTTP_URI_TRANSFORMING)) {
			toolBarManager.add(new SwitchHttpUriTransformation("Apply sensor regular expression on URI"));
		}

		if (preferenceSet.contains(PreferenceId.INVOCATION_SUBVIEW_MODE)) {
			toolBarManager.add(new SwitchInvocationSubviewMode("Switch the tabbed views mode from/to aggregated/raw"));
		}

		toolBarManager.add(new Separator());

		if (preferenceSet.contains(PreferenceId.SAMPLINGRATE) || preferenceSet.contains(PreferenceId.TIMELINE)) {
			toolBarManager.add(switchPreferences);
		}

		if (preferenceSet.contains(PreferenceId.STEPPABLE_CONTROL)) {
			switchSteppingControl = new SwitchSteppingControl("Stepping control");
			toolBarManager.add(switchSteppingControl);
		}

		if (preferenceSet.contains(PreferenceId.LIVEMODE)) {
			toolBarManager.add(switchLiveMode);

			// Refresh rate
			MenuManager refreshMenuManager = new MenuManager("Refresh rate");
			long currentRefreshRate = PreferencesUtils.getLongValue(PreferencesConstants.REFRESH_RATE);
			refreshMenuManager.add(new SetRefreshRateAction("5 (s)", 5000, currentRefreshRate));
			refreshMenuManager.add(new SetRefreshRateAction("10 (s)", 10000, currentRefreshRate));
			refreshMenuManager.add(new SetRefreshRateAction("30 (s)", 30000, currentRefreshRate));
			refreshMenuManager.add(new SetRefreshRateAction("60 (s)", 60000, currentRefreshRate));
			menuAction.addContributionItem(refreshMenuManager);
		}
		if (preferenceSet.contains(PreferenceId.UPDATE)) {
			toolBarManager.add(new UpdateAction("Update")); // NOPMD
		}

		if (preferenceSet.contains(PreferenceId.ITEMCOUNT)) {
			int currentItemsToShow = PreferencesUtils.getIntValue(PreferencesConstants.ITEMS_COUNT_TO_SHOW);
			MenuManager countMenuManager = new MenuManager("Item count to show");
			countMenuManager.add(new SetItemCountAction("10", 10, currentItemsToShow));
			countMenuManager.add(new SetItemCountAction("20", 20, currentItemsToShow));
			countMenuManager.add(new SetItemCountAction("50", 50, currentItemsToShow));
			countMenuManager.add(new SetItemCountAction("100", 100, currentItemsToShow));
			countMenuManager.add(new SetItemCountAction("200", 200, currentItemsToShow));
			countMenuManager.add(new SetItemCountAction("500", 500, currentItemsToShow));
			countMenuManager.add(new SetItemCountAction("All...", -1, currentItemsToShow));
			menuAction.addContributionItem(countMenuManager);
		}
		if (preferenceSet.contains(PreferenceId.FILTERDATATYPE)) {
			Set<Class<?>> activeDataTypes = PreferencesUtils.getObject(PreferencesConstants.INVOCATION_FILTER_DATA_TYPES);
			MenuManager dataTypeMenuManager = new MenuManager("Filter by DataType");
			dataTypeMenuManager.add(new FilterByDataTypeAction("Invocation Sequence Data", InvocationSequenceData.class, activeDataTypes));
			dataTypeMenuManager.add(new FilterByDataTypeAction("Timer Data", TimerData.class, activeDataTypes));
			dataTypeMenuManager.add(new FilterByDataTypeAction("Sql Statement Data", SqlStatementData.class, activeDataTypes));
			dataTypeMenuManager.add(new FilterByDataTypeAction("Http Timer Data", HttpTimerData.class, activeDataTypes));
			dataTypeMenuManager.add(new FilterByDataTypeAction("Exception Sensor Data", ExceptionSensorData.class, activeDataTypes));
			menuAction.addContributionItem(dataTypeMenuManager);
		}
		if (preferenceSet.contains(PreferenceId.INVOCFILTEREXCLUSIVETIME)) {
			double currentInvocFilterExclusive = PreferencesUtils.getDoubleValue(PreferencesConstants.INVOCATION_FILTER_EXCLUSIVE_TIME);
			MenuManager timeMenuManager = new MenuManager("Filter Details by Exclusive Time");
			timeMenuManager.add(new FilterByExclusiveTimeAction("No filter", Double.NaN, currentInvocFilterExclusive));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByExclusiveTimeAction("0.1 ms", 0.1, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("0.2 ms", 0.2, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("0.5 ms", 0.5, currentInvocFilterExclusive));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByExclusiveTimeAction("1 ms", 1.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("2 ms", 2.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("5 ms", 5.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("10 ms", 10.0, currentInvocFilterExclusive));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByExclusiveTimeAction("50 ms", 50.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("100 ms", 100.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("200 ms", 200.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("500 ms", 500.0, currentInvocFilterExclusive));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByExclusiveTimeAction("1 s", 1000.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("1.5 s", 1500.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("2 s", 2000.0, currentInvocFilterExclusive));
			timeMenuManager.add(new FilterByExclusiveTimeAction("5 s", 5000.0, currentInvocFilterExclusive));
			menuAction.addContributionItem(timeMenuManager);
		}
		if (preferenceSet.contains(PreferenceId.INVOCFILTERTOTALTIME)) {
			double currentInvocFilterTotal = PreferencesUtils.getDoubleValue(PreferencesConstants.INVOCATION_FILTER_TOTAL_TIME);
			MenuManager timeMenuManager = new MenuManager("Filter Details by Total Time");
			timeMenuManager.add(new FilterByTotalTimeAction("No filter", Double.NaN, currentInvocFilterTotal));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByTotalTimeAction("0.1 ms", 0.1, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("0.2 ms", 0.2, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("0.5 ms", 0.5, currentInvocFilterTotal));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByTotalTimeAction("1 ms", 1.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("2 ms", 2.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("5 ms", 5.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("10 ms", 10.0, currentInvocFilterTotal));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByTotalTimeAction("50 ms", 50.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("100 ms", 100.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("200 ms", 200.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("500 ms", 500.0, currentInvocFilterTotal));
			// timeMenuManager.add(new Separator());
			timeMenuManager.add(new FilterByTotalTimeAction("1 s", 1000.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("1.5 s", 1500.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("2 s", 2000.0, currentInvocFilterTotal));
			timeMenuManager.add(new FilterByTotalTimeAction("5 s", 5000.0, currentInvocFilterTotal));
			menuAction.addContributionItem(timeMenuManager);
		}

		if (preferenceSet.contains(PreferenceId.TIME_RESOLUTION)) {
			MenuManager timeMenuManager = new MenuManager("Time Decimal Places");
			int currentDecimalPlaces = PreferencesUtils.getIntValue(PreferencesConstants.DECIMAL_PLACES);
			timeMenuManager.add(new SetTimeDecimalPlaces("0", 0, currentDecimalPlaces));
			timeMenuManager.add(new SetTimeDecimalPlaces("1", 1, currentDecimalPlaces));
			timeMenuManager.add(new SetTimeDecimalPlaces("2", 2, currentDecimalPlaces));
			timeMenuManager.add(new SetTimeDecimalPlaces("3", 3, currentDecimalPlaces));
			menuAction.addContributionItem(timeMenuManager);
		}

		// only add if there is really something in the menu
		if (menuAction.getSize() > 0) {
			toolBarManager.add(menuAction);
		}

		toolBarManager.update(true);
	}

	/**
	 * Due to the Eclipse bug this method will return the correct tool-tip text with correct binding
	 * sequence for the maximize active sub-view command.
	 * <p>
	 * <i>This method should be removed when Eclipse fixes the bug.</i>
	 * 
	 * @return Returns tool-tip text with key binding sequence.
	 */
	private String getTooltipTextForMaximizeContributionItem() {
		String tooltipText = "Maximize Active Sub-View";
		TriggerSequence activeBinding = null;
		Collection<Binding> allBindings = eBindingService.getActiveBindings();// getBindings();
		for (Binding b : allBindings) {
			ParameterizedCommand pCommand = b.getParameterizedCommand();
			if (null != pCommand) {
				String commandId = pCommand.getId();
				if (ObjectUtils.equals(commandId, MaximizeActiveViewHandler.COMMAND_ID)) {
					activeBinding = b.getTriggerSequence();
					break;
				}
			}
		}

		if (activeBinding != null && !activeBinding.isEmpty()) {
			String acceleratorText = activeBinding.format();
			if (acceleratorText != null && acceleratorText.length() != 0) {
				tooltipText = NLS.bind("{0}({1})", tooltipText, acceleratorText);
			}
		}

		return tooltipText;
	}

	/**
	 * Switches the Preference View on/off.
	 * 
	 * @author Patrice Bouillet
	 * 
	 */
	private final class SwitchPreferences extends Action {
		/**
		 * Switches the preferences.
		 * 
		 * @param text
		 *            The text.
		 */
		private SwitchPreferences(String text) {
			super(text, AS_CHECK_BOX);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_PREFERENCES));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			FormPreferencePanel.this.setVisible(isChecked());
		}
	}

	/**
	 * Updates the Preferences.
	 * 
	 * @author Patrice Bouillet
	 * 
	 */
	private final class UpdateAction extends Action {
		/**
		 * Updates an action.
		 * 
		 * @param text
		 *            The text.
		 */
		private UpdateAction(String text) {
			super(text);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_REFRESH));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			FormPreferencePanel.this.update();
		}
	}

	/**
	 * Switches the live mode.
	 * 
	 * @author Eduard Tudenhoefner
	 * 
	 */
	private final class SwitchLiveMode extends Action {

		/**
		 * Switches the Live Mode.
		 * 
		 * @param text
		 *            The text.
		 */
		public SwitchLiveMode(String text) {
			super(text);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_LIVE_MODE));
			setChecked(LiveMode.ACTIVE_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			createLiveModeEvent();
		}
	}

	/**
	 * Filters by the maximum number of elements shown.
	 * 
	 * @author Stefan Siegl
	 */
	private final class SetItemCountAction extends Action {
		/** the maximum number of elements shown. */
		private int limit;

		/**
		 * Constructor.
		 * 
		 * @param text
		 *            the text
		 * @param limit
		 *            the maximum number of elements shown.
		 * @param currentItemsToShow
		 *            current items to show, button will be selected if it matches the passed limit
		 */
		public SetItemCountAction(String text, int limit, int currentItemsToShow) {
			super(text, Action.AS_RADIO_BUTTON);
			this.limit = limit;
			setChecked(currentItemsToShow == limit);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			if (isChecked()) {
				PreferencesUtils.saveIntValue(PreferencesConstants.ITEMS_COUNT_TO_SHOW, limit, false);
				Map<IPreferenceGroup, Object> countPreference = new HashMap<IPreferenceGroup, Object>();
				countPreference.put(PreferenceId.ItemCount.COUNT_SELECTION_ID, limit);
				PreferenceEvent event = new PreferenceEvent(PreferenceId.ITEMCOUNT);
				event.setPreferenceMap(countPreference);
				fireEvent(event);
			}
		}
	}

	/**
	 * Filters by data type.
	 * 
	 * @author Ivan Senic
	 */
	private final class FilterByDataTypeAction extends Action {

		/** The sensor type. */
		private Class<?> dataClass;

		/**
		 * Constructor.
		 * 
		 * @param text
		 *            the text
		 * @param dataClass
		 *            the data type
		 * @param activeDatas
		 *            currently active data types, button will be checked if the given data type is
		 *            contained in the set
		 */
		public FilterByDataTypeAction(String text, Class<?> dataClass, Set<Class<?>> activeDatas) {
			super(text, Action.AS_CHECK_BOX);
			this.dataClass = dataClass;
			setChecked(activeDatas.contains(dataClass));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			Set<Class<?>> activeDatas = PreferencesUtils.getObject(PreferencesConstants.INVOCATION_FILTER_DATA_TYPES);
			if (isChecked() && !activeDatas.contains(dataClass)) {
				activeDatas.add(dataClass);
				PreferencesUtils.saveObject(PreferencesConstants.INVOCATION_FILTER_DATA_TYPES, activeDatas, false);
			} else if (!isChecked() && activeDatas.contains(dataClass)) {
				activeDatas.remove(dataClass);
				PreferencesUtils.saveObject(PreferencesConstants.INVOCATION_FILTER_DATA_TYPES, activeDatas, false);
			}
			Map<IPreferenceGroup, Object> sensorTypePreference = new HashMap<IPreferenceGroup, Object>();
			sensorTypePreference.put(PreferenceId.DataTypeSelection.SENSOR_DATA_SELECTION_ID, dataClass);
			PreferenceEvent event = new PreferenceEvent(PreferenceId.FILTERDATATYPE);
			event.setPreferenceMap(sensorTypePreference);
			fireEvent(event);
		}
	}

	/**
	 * Filters by exclusive time.
	 * 
	 * @author Stefan Siegl
	 */
	private final class FilterByExclusiveTimeAction extends Action {
		/** the time. */
		private double time;

		/**
		 * Constructor.
		 * 
		 * @param text
		 *            the text
		 * @param time
		 *            the time
		 * @param currentInvocFilterExclusive
		 *            current invocation filter exclusive time value, button will be checked if it
		 *            matches passed time
		 */
		public FilterByExclusiveTimeAction(String text, double time, double currentInvocFilterExclusive) {
			super(text, Action.AS_RADIO_BUTTON);
			this.time = time;
			setChecked(currentInvocFilterExclusive == time || (Double.isNaN(time)) && Double.isNaN(currentInvocFilterExclusive));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			if (isChecked()) {
				PreferencesUtils.saveDoubleValue(PreferencesConstants.INVOCATION_FILTER_EXCLUSIVE_TIME, time, false);
				Map<IPreferenceGroup, Object> sensorTypePreference = new HashMap<IPreferenceGroup, Object>();
				sensorTypePreference.put(PreferenceId.InvocExclusiveTimeSelection.TIME_SELECTION_ID, new Double(time));
				PreferenceEvent event = new PreferenceEvent(PreferenceId.INVOCFILTEREXCLUSIVETIME);
				event.setPreferenceMap(sensorTypePreference);
				fireEvent(event);
			}
		}
	}

	/**
	 * Filters by total time.
	 * 
	 * @author Stefan Siegl
	 */
	private final class FilterByTotalTimeAction extends Action {
		/** the time. */
		private double time;

		/**
		 * Constructor.
		 * 
		 * @param text
		 *            the text
		 * @param time
		 *            the time
		 * @param currentInvocFilterTotal
		 *            current invocation filter total time value, button will be checked if it
		 *            matches passed time
		 */
		public FilterByTotalTimeAction(String text, double time, double currentInvocFilterTotal) {
			super(text, Action.AS_RADIO_BUTTON);
			this.time = time;
			setChecked(currentInvocFilterTotal == time || (Double.isNaN(time)) && Double.isNaN(currentInvocFilterTotal));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			if (isChecked()) {
				PreferencesUtils.saveDoubleValue(PreferencesConstants.INVOCATION_FILTER_TOTAL_TIME, time, false);
				Map<IPreferenceGroup, Object> sensorTypePreference = new HashMap<IPreferenceGroup, Object>();
				sensorTypePreference.put(PreferenceId.InvocTotalTimeSelection.TIME_SELECTION_ID, new Double(time));
				PreferenceEvent event = new PreferenceEvent(PreferenceId.INVOCFILTERTOTALTIME);
				event.setPreferenceMap(sensorTypePreference);
				fireEvent(event);
			}
		}
	}

	/**
	 * Sets the automatic refresh rate.
	 * 
	 * @author Stefan Siegl
	 */
	private final class SetRefreshRateAction extends Action {

		/**
		 * Refresh rate in ms.
		 */
		private long rate;

		/**
		 * Constructor.
		 * 
		 * @param text
		 *            the text
		 * @param rate
		 *            the refresh rate
		 * @param currentRate
		 *            current refresh rate, button will be checked if rate and current rate are the
		 *            same
		 */
		public SetRefreshRateAction(String text, long rate, long currentRate) {
			super(text, Action.AS_RADIO_BUTTON);
			this.rate = rate;
			setChecked(rate == currentRate);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			if (isChecked()) {
				PreferencesUtils.saveLongValue(PreferencesConstants.REFRESH_RATE, rate, false);
				Map<IPreferenceGroup, Object> refreshPreference = new HashMap<IPreferenceGroup, Object>();
				refreshPreference.put(LiveMode.REFRESH_RATE, rate);
				PreferenceEvent event = new PreferenceEvent(PreferenceId.LIVEMODE);
				event.setPreferenceMap(refreshPreference);
				fireEvent(event);
			}
		}
	}

	/**
	 * Creates and fires a new live mode event.
	 */
	private void createLiveModeEvent() {
		Map<IPreferenceGroup, Object> livePreference = new HashMap<IPreferenceGroup, Object>();
		livePreference.put(PreferenceId.LiveMode.BUTTON_LIVE_ID, switchLiveMode.isChecked());
		PreferenceEvent event = new PreferenceEvent(PreferenceId.LIVEMODE);
		event.setPreferenceMap(livePreference);
		fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		for (IPreferenceControl preferenceControl : preferenceControlList) {
			preferenceControl.dispose();
		}

		switchLiveMode = null; // NOPMD
	}

	/**
	 * Action for turning the stepping control off and on.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private final class SwitchSteppingControl extends Action {

		/**
		 * Default constructor.
		 * 
		 * @param text
		 *            the action's text, or <code>null</code> if there is no text
		 * @see Action
		 */
		public SwitchSteppingControl(String text) {
			super(text, AS_CHECK_BOX);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_LOCATE_IN_HIERARCHY));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			PreferenceEvent event = new PreferenceEvent(PreferenceId.STEPPABLE_CONTROL);
			Map<IPreferenceGroup, Object> steppablePreference = new HashMap<IPreferenceGroup, Object>();
			steppablePreference.put(PreferenceId.SteppableControl.BUTTON_STEPPABLE_CONTROL_ID, this.isChecked());
			event.setPreferenceMap(steppablePreference);
			fireEvent(event);
		}

	}

	/**
	 * Sets the decimal places.
	 * 
	 * @author Stefan Siegl
	 * 
	 */
	private final class SetTimeDecimalPlaces extends Action {

		/**
		 * The number of decimal places.
		 * */
		private int decimalPlaces;

		/**
		 * Default constructor.
		 * 
		 * @param text
		 *            the action's text, or <code>null</code> if there is no text
		 * @param decimalPlaces
		 *            the number of decimal places
		 * @param currentDecimalPlaces
		 *            current decimal places, button will be checked if decimalPlaces and
		 *            currentDecimalPlaces are same
		 * @see Action
		 */
		public SetTimeDecimalPlaces(String text, int decimalPlaces, int currentDecimalPlaces) {
			super(text, Action.AS_RADIO_BUTTON);
			this.decimalPlaces = decimalPlaces;
			setChecked(decimalPlaces == currentDecimalPlaces);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			if (isChecked()) {
				PreferencesUtils.saveIntValue(PreferencesConstants.DECIMAL_PLACES, decimalPlaces, false);
				Map<IPreferenceGroup, Object> decimalPlacesPreference = new HashMap<IPreferenceGroup, Object>();
				decimalPlacesPreference.put(TimeResolution.TIME_DECIMAL_PLACES_ID, decimalPlaces);
				PreferenceEvent event = new PreferenceEvent(PreferenceId.TIME_RESOLUTION);
				event.setPreferenceMap(decimalPlacesPreference);
				fireEvent(event);
			}
		}
	}

	/**
	 * Option to switch between categorization based on request method or not.
	 * 
	 * @author Stefan Siegl
	 */
	private final class SwitchHttpCategorizationRequestMethod extends Action {

		/**
		 * Default Constructor.
		 * 
		 * @param text
		 *            the action's text, or <code>null</code> if there is no text
		 */
		public SwitchHttpCategorizationRequestMethod(String text) {
			super(text, AS_CHECK_BOX);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_HTTP_AGGREGATION_REQUESTMESSAGE));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			PreferenceEvent event = new PreferenceEvent(PreferenceId.HTTP_AGGREGATION_REQUESTMETHOD);
			Map<IPreferenceGroup, Object> httpCategoriation = new HashMap<IPreferenceGroup, Object>();
			httpCategoriation.put(PreferenceId.HttpAggregationRequestMethod.BUTTON_HTTP_AGGREGATION_REQUESTMETHOD_ID, this.isChecked());
			event.setPreferenceMap(httpCategoriation);
			fireEvent(event);

			// perform a refresh
			update();
		}
	}

	/**
	 * Option to active Http URI transformation with regular expression.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private final class SwitchHttpUriTransformation extends Action {

		/**
		 * Default Constructor.
		 * 
		 * @param text
		 *            the action's text, or <code>null</code> if there is no text
		 */
		public SwitchHttpUriTransformation(String text) {
			super(text, AS_CHECK_BOX);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_TRANSFORM));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			PreferenceEvent event = new PreferenceEvent(PreferenceId.HTTP_URI_TRANSFORMING);
			Map<IPreferenceGroup, Object> preferenceMap = new HashMap<IPreferenceGroup, Object>();
			preferenceMap.put(PreferenceId.HttpUriTransformation.URI_TRANSFORMATION_ACTIVE, this.isChecked());
			event.setPreferenceMap(preferenceMap);
			fireEvent(event);

			// perform a refresh
			update();
		}
	}

	/**
	 * Action for switching the mode of the invocation subviews from/to raw/aggregated.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private final class SwitchInvocationSubviewMode extends Action {

		/**
		 * Default constructor.
		 * 
		 * @param text
		 *            Text on the action.
		 */
		public SwitchInvocationSubviewMode(String text) {
			super(text, AS_CHECK_BOX);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_HTTP_AGGREGATION_REQUESTMESSAGE));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			PreferenceEvent event = new PreferenceEvent(PreferenceId.INVOCATION_SUBVIEW_MODE);
			Map<IPreferenceGroup, Object> httpCategoriation = new HashMap<IPreferenceGroup, Object>();
			httpCategoriation.put(PreferenceId.InvocationSubviewMode.RAW, this.isChecked());
			event.setPreferenceMap(httpCategoriation);
			fireEvent(event);
		}
	}
	
	/**
	 * Action for switching the mode of the invocation subviews from/to raw/aggregated.
	 * 
	 * @author Ivan Senic
	 * 
	 */
	private final class MaximizeActiveViewAction extends Action {


		
		/**
		 * Default constructor.
		 * 
		 * @param text
		 *            Text on the action.
		 */
		public MaximizeActiveViewAction(String text) {
			super(text, AS_CHECK_BOX);
			setImageDescriptor(InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_WINDOW));
		}
		
		@Inject MHandledItem mHandledItem;
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			MPart editorPart = mPart;
			if (editorPart.getObject() instanceof AbstractRootEditor) {
				AbstractRootEditor abstractRootEditor = (AbstractRootEditor) editorPart.getObject();
				if (abstractRootEditor.canMaximizeActiveSubView()) {
					abstractRootEditor.maximizeActiveSubView();
				} else if (abstractRootEditor.canMinimizeActiveSubView()) {
					abstractRootEditor.minimizeActiveSubView();
				}
			}

			// after the maximized/minimized is executed we need to refresh the UI elements bounded to
			// the command, so that checked state of that elements is updated
//			Map<Object, Object> filter = new HashMap<Object, Object>();
//			filter.put(IServiceScopes.WINDOW_SCOPE, mWindow);
//			List<MHandledItem> elements = eModelService.findElements(mWindow, null, MHandledItem.class, null);
//		   // elements.addAll(eModelService.findElements(mWindow, null, MHandledItem.class, null));
//		    for( MHandledItem hi : elements ){
//				hi.setSelected(mHandledItem.isSelected());
//			}
			
			//refreshes local objects (?)
			mApplication.updateLocalization();
		    
		}
	}

}