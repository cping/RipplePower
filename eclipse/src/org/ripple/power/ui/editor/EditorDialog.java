package org.ripple.power.ui.editor;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.address.collection.ArrayList;
import org.ripple.power.command.IScriptLog;
import org.ripple.power.command.ROCScript;
import org.ripple.power.command.ROCScript.ScriptException;
import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.RippleMacros;
import org.ripple.power.ui.JConsole;
import org.ripple.power.ui.RPCButton;
import org.ripple.power.ui.RPList;
import org.ripple.power.utils.GraphicsUtils;

public class EditorDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ROCScript _script;

	class Console implements IScriptLog {

		@Override
		public void show(boolean flag) {

		}

		@Override
		public void err(Object mes) {
			println(mes.toString());

		}

		@Override
		public void info(Object mes) {
			println(mes.toString());

		}

		@Override
		public void line(Object mes) {
			print(mes.toString());

		}

		@Override
		public void newline() {
			println();

		}

		@Override
		public void err(String mes, Object... o) {
			println(String.format(mes, o));

		}

		@Override
		public void info(String mes, Object... o) {
			println(String.format(mes, o));

		}

	}
	public void println(String line) {
		if (_log != null) {
			_log.uiprint(line+"\n");
		}
	}
	
	public void print(String line) {
		if (_log != null) {
			_log.uiprint(line);
		}
	}

	public void println() {
		if (_log != null) {
			_log.uiprint("\n");
		}
	}

	private final static ArrayList samples = new ArrayList(10);
	static {
		samples.add("hello");
		samples.add("ping");
		samples.add("server_info");
		samples.add("server_state");
		samples.add("account_info");
		samples.add("account_lines");
		samples.add("account_offers");
		samples.add("account_tx");
		samples.add("transaction_entry");
		samples.add("tx");
		samples.add("tx_history");
		samples.add("send");
		samples.add("offer_create");
		samples.add("offer_cancel");
		samples.add("offer_price");
		samples.add("convert_price");
	}
	private RPCButton _exitButton;
	private RPCButton _callButton;
	private RPList jList1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private ROCScriptEditor _editorText;
	private JConsole _log;

	private String getContext(String name) {
		StringBuffer sbr = new StringBuffer();
		if ("hello".equals(name)) {
			sbr.append("#ROC Script");
			sbr.append("\n");
			sbr.append("function hello() begin");
			sbr.append("\n");
			sbr.append(" return \"Hello World!\"");
			sbr.append("\n");
			sbr.append("end");
			sbr.append("\n");
			sbr.append("println hello()");
		} else if ("ping".equals(name)) {
			sbr.append("#ping");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("ping");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
			sbr.append("print \"Hello World!\"+ripple.ping.id");
		}
		return sbr.toString();
	}

	public EditorDialog(Window parent) {
		super(parent, "ROC Script Editor(Developing)", Dialog.ModalityType.MODELESS);
		addWindowListener(HelperWindow.get());
		setResizable(false);
		Dimension dim = new Dimension(818, 595);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	public static void showDialog(Window parent) {
		try {
			EditorDialog dialog = new EditorDialog(parent);
			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			dialog.setVisible(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new RPList();
		jScrollPane2 = new javax.swing.JScrollPane();
		_editorText = new ROCScriptEditor();
		jScrollPane3 = new javax.swing.JScrollPane();
		_log = new JConsole();
		_exitButton = new RPCButton();
		_callButton = new RPCButton();

		setLayout(null);

		jList1.setModel(new javax.swing.AbstractListModel<Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return samples.size();
			}

			public Object getElementAt(int i) {
				return samples.get(i);
			}
		});
		jScrollPane1.setViewportView(jList1);
		jList1.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int idx = jList1.getSelectedIndex();
				if (idx > -1) {
					_editorText.setText(getContext((String) jList1
							.getSelectedValue()));
				}
			}
		});

		add(jScrollPane1);
		jScrollPane1.setBounds(10, 10, 108, 545);

		jScrollPane2.setViewportView(_editorText);

		add(jScrollPane2);
		jScrollPane2.setBounds(128, 10, 670, 320);

		jScrollPane3.setViewportView(_log);

		add(jScrollPane3);
		jScrollPane3.setBounds(130, 340, 670, 160);
		
		Font font = GraphicsUtils.getFont(14);

		_exitButton.setText(LangConfig.get(this, "exit", "Exit"));
		_exitButton.setFont(font);
		add(_exitButton);
		_exitButton.setBounds(710, 510, 90, 40);

		_callButton.setText(LangConfig.get(this, "run", "Run"));
		_callButton.setFont(font);
		_callButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_log.clear();
				if (_script != null) {
					_script.stop();
					_script =null;
				}
				try {
					_script = new ROCScript(new Console(),_editorText.getText() , false);
				} catch (ScriptException ex) {
					print(ex.getMessage());
				}
				_script.setMacrosListener(new RippleMacros());
				_script.call(false);
			}
		});
		add(_callButton);
		_callButton.setBounds(600, 510, 90, 40);
		getContentPane().setBackground(LSystem.dialogbackground);
	}
}
