package org.ripple.power.ui.view;

import java.awt.Window;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.ui.UIMessage;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.SwingUtils;

public abstract class ABaseDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private void init(){
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
	}
	
	public ABaseDialog() throws HeadlessException {
		super();
		init();
	}

	public ABaseDialog(String title) {
		super(LSystem.applicationMain, title);
		init();
	}

	public ABaseDialog(Window parent, String title, ModalityType modal)
			throws HeadlessException {
		super(parent, title, modal);
		init();
	}

	public ABaseDialog(Window parent, String title) throws HeadlessException {
		super(parent, title);
		init();
	}

	public ABaseDialog(Window parent) throws HeadlessException {
		super(parent);
		init();
	}

	public ABaseDialog(Frame parent, boolean modal) throws HeadlessException {
		super(parent, modal);
		init();
	}

	public ABaseDialog(Frame parent, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(parent, title, modal, gc);
		init();
	}

	public ABaseDialog(Frame parent, String title, boolean modal)
			throws HeadlessException {
		super(parent, title, modal);
		init();
	}

	public ABaseDialog(Frame parent, String title) throws HeadlessException {
		super(parent, title);
		init();
	}

	public ABaseDialog(Frame parent) throws HeadlessException {
		super(parent);
		init();
	}

	protected JRootPane createRootPane() {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = super.createRootPane();
		rootPane.registerKeyboardAction(closeDialogActionListener(), keyStroke,
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	private ActionListener closeDialogActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtils.close(ABaseDialog.this);
			}
		};
	}

	public void info(String text) {
		UIMessage.infoMessage(this, text);
	}

	public void alert(String text) {
		UIMessage.alertMessage(this, text);
	}

}
