package info.novatec.inspectit.rcp.dialog;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.util.ObjectUtils;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for editing a storage or CMR repository.
 * 
 * @author Ivan Senic
 * 
 */
public class EditRepositoryDataDialog extends TitleAreaDialog {

	/**
	 * Name box.
	 */
	private Text nameBox;

	/**
	 * Description box.
	 */
	private Text descriptionBox;

	/**
	 * OK button.
	 */
	private Control okButton;

	/**
	 * Old name.
	 */
	private String oldName;

	/**
	 * Old description.
	 */
	private String oldDescription;

	/**
	 * New name.
	 */
	private String newDescription;

	/**
	 * New description.
	 */
	private String newName;

	/**
	 * Default constructor.
	 * 
	 * @param parentShell
	 *            Parent shell.
	 * @param oldName
	 *            Old name.
	 * @param oldDescription
	 *            Old description.
	 */
	public EditRepositoryDataDialog(Shell parentShell, String oldName, String oldDescription) {
		super(parentShell);
		this.oldName = oldName;
		this.oldDescription = oldDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		this.setTitle("Edit Data");
		this.setMessage("Enter new name and/or description", IMessageProvider.INFORMATION);
		this.setTitleImage(InspectIT.getDefault().getImage(InspectITImages.IMG_WIZBAN_EDIT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Edit Data");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		gd.heightHint = 200;
		main.setLayoutData(gd);

		Label nameLabel = new Label(main, SWT.LEFT);
		nameLabel.setText("New name:");
		nameBox = new Text(main, SWT.BORDER);
		nameBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameBox.setText(oldName);
		nameBox.selectAll();

		Label descLabel = new Label(main, SWT.LEFT);
		descLabel.setText("New description:");
		descLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		descriptionBox = new Text(main, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		descriptionBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (null != oldDescription) {
			descriptionBox.setText(oldDescription);
		}

		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (isInputValid()) {
					okButton.setEnabled(true);
				} else {
					okButton.setEnabled(false);
				}
			}
		};

		nameBox.addModifyListener(modifyListener);
		descriptionBox.addModifyListener(modifyListener);
		return main;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			newName = nameBox.getText().trim();
			if (!descriptionBox.getText().trim().isEmpty()) {
				newDescription = descriptionBox.getText().trim();
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * @return Returns the submitted name.
	 */
	public String getName() {
		return newName;
	}

	/**
	 * @return Returns the submitted description.
	 */
	public String getDescription() {
		return newDescription;
	}

	/**
	 * Is input in textual boxes valid.
	 * 
	 * @return Is input in textual boxes valid.
	 */
	private boolean isInputValid() {
		if (ObjectUtils.equals(oldName, nameBox.getText().trim())) {
			if (ObjectUtils.equals(oldDescription, descriptionBox.getText().trim())) {
				return false;
			}
		}
		if (nameBox.getText().isEmpty()) {
			return false;
		}
		return true;
	}
}
