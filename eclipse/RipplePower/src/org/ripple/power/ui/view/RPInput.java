package org.ripple.power.ui.view;

import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ripple.power.config.LSystem;
import org.ripple.power.ui.graphics.LColor;

public class RPInput {

	static public interface TextInputListener {

		public void input(String text);

		public void canceled();
	}

	public void getTextInput(final TextInputListener listener, final String title, final String text) {
		LSystem.invokeLater(new Runnable() {
			public void run() {
				final String output = JOptionPane.showInputDialog(null, title, text);
				if (output != null) {
					listener.input(output);
				} else {
					listener.canceled();
				}
			}
		});
	}

	public void getBigTextInput(final TextInputListener listener, final String title, final String placeholder,
			final Object[] objs) {
		LSystem.invokeLater(new Runnable() {
			@Override
			public void run() {
				JPanel panel = new JPanel(new FlowLayout());

				JPanel textPanel = new JPanel() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public boolean isOptimizedDrawingEnabled() {
						return false;
					};
				};

				textPanel.setLayout(new OverlayLayout(textPanel));
				panel.add(textPanel);

				final JTextField textField = new JTextField(20);
				textPanel.add(textField);

				final JLabel placeholderLabel = new JLabel(placeholder);
				placeholderLabel.setForeground(LColor.GRAY);
				placeholderLabel.setAlignmentX(0.0f);
				textPanel.add(placeholderLabel, 0);

				textField.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						this.updated();
					}

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						this.updated();
					}

					@Override
					public void changedUpdate(DocumentEvent arg0) {
						this.updated();
					}

					private void updated() {
						if (textField.getText().length() == 0) {
							placeholderLabel.setVisible(true);
						} else {
							placeholderLabel.setVisible(false);
						}
					}
				});

				JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
						null, objs, null);

				pane.setInitialValue(null);
				pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());

				Border border = textField.getBorder();
				placeholderLabel.setBorder(new EmptyBorder(border.getBorderInsets(textField)));

				JDialog dialog = pane.createDialog(null, title);
				pane.selectInitialValue();

				dialog.addWindowFocusListener(new WindowFocusListener() {

					@Override
					public void windowLostFocus(WindowEvent arg0) {
					}

					@Override
					public void windowGainedFocus(WindowEvent arg0) {
						textField.requestFocusInWindow();
					}
				});

				dialog.setVisible(true);
				dialog.dispose();

				Object selectedValue = pane.getValue();

				if (selectedValue != null && (selectedValue instanceof Integer)
						&& ((Integer) selectedValue).intValue() == JOptionPane.OK_OPTION) {
					listener.input(textField.getText());
				} else {
					listener.canceled();
				}
				if (selectedValue != null && selectedValue.equals(objs[0])) {
					listener.input(placeholder);
				} else {
					listener.canceled();
				}
			}
		});
	}
}
