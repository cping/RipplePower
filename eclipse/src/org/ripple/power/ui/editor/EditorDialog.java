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

import org.ripple.power.collection.ArrayList;
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
			_log.uiprint(line + "\n");
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
		samples.add("if then else");
		samples.add("calculate");
		samples.add("for while");
		samples.add("function");
		samples.add("ping");
		samples.add("server_info");
		samples.add("server_state");
		samples.add("account_info");
		samples.add("account_lines");
		samples.add("account_offers");
		samples.add("account_tx");
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
		sbr.append("#ROC Script\n");
		if ("hello".equals(name)) {
			sbr.append("function hello() begin");
			sbr.append("\n");
			sbr.append("  return \"Hello World!\"");
			sbr.append("\n");
			sbr.append("end");
			sbr.append("\n");
			sbr.append("println hello()");
		} else if ("if then else".equals(name)) {
			sbr.append("x = 1\n");
			sbr.append("if x < 0 then\n");
			sbr.append("  print \"This won't print\"\n");
			sbr.append("else if x == 0 then\n");
			sbr.append("  print \"This won't print either\"\n");
			sbr.append("else\n");
			sbr.append("  print \"This will print\"\n");
			sbr.append("end");
		} else if ("calculate".equals(name)) {
			sbr.append("x = 119\n");
			sbr.append("println 2 + x * -2\n");
			sbr.append("println 2 - x / 2\n");
			sbr.append("println x ^ 3 % 7\n");
			sbr.append("x = 128 + 6 - 714 + 911\n");
			sbr.append("println x");
		} else if ("for while".equals(name)) {
			sbr.append("x = 10\n");
			sbr.append("for i = 0, i < x, i + 1 begin\n");
			sbr.append("   println i\n");
			sbr.append("end\n\n");
			sbr.append("for i = 0, i < 2, i + 1 begin\n");
			sbr.append("	for j = 0, j < 2, j + 1 begin\n");
			sbr.append("		println i == 0 or j == 0\n");
			sbr.append("	end\n");
			sbr.append("end\n\n");
			sbr.append("x = -10\n");
			sbr.append("while x < 0 begin\n");
			sbr.append("    print x\n");
			sbr.append("    x = x + 1\n");
			sbr.append("end\n");
		} else if ("function".equals(name)) {
			sbr.append("function xyz(x , y) begin\n");
			sbr.append("  for i = x, i < y, i + 1 begin\n");
			sbr.append("    println i\n");
			sbr.append("  end\n");
			sbr.append("end\n");
			sbr.append("xyz(5 , 8)\n");
			sbr.append("function getNum(x) begin\n");
			sbr.append("	return x + 1\n");
			sbr.append("end\n");
			sbr.append("print getNum(9) + getNum(6)\n");
		} else if ("ping".equals(name)) {
			sbr.append("#Ping");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("ping");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
			sbr.append("print \"Hello World!\"+ripple.ping.id");
		}else if ("server_info".equals(name)) {
			sbr.append("#Server_info");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("server_info");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
			sbr.append("print ripple.server_info.load_factor");
		}else if ("server_state".equals(name)) {
			sbr.append("#Server_state");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("server_state");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
			sbr.append("print ripple.server_state.load_base");
		}else if ("account_info".equals(name)) {
			sbr.append("#Account_info");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("account_info ~Bitstamp");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
			sbr.append("print roc.xrp_to_val(ripple.account_info.Balance)");
		}else if ("account_lines".equals(name)) {
			sbr.append("#Account_lines");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("account_lines ~Bitstamp");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("account_offers".equals(name)) {
			sbr.append("#Account_offers");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("account_offers ~Bitstamp");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("account_tx".equals(name)) {
			sbr.append("#Account_tx");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("account_tx ~Bitstamp");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("send".equals(name)) {
			sbr.append("#Send");
			sbr.append(LSystem.LS);
			sbr.append("#[secret] location write secret or address (address need to add to \n#RipplePower)\n");
			sbr.append("#not XRP , format: secret/1/btc/rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B \n");
			sbr.append("#secret位置可为私钥或者地址(如果使用地址发币,则地址需要添加到RipplePower)\n");
			sbr.append("#如非XRP,则格式为: secret/1/btc/rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B \n");
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("send secret/1/xrp rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("offer_create".equals(name)) {
			sbr.append("#Offer_create");
			sbr.append(LSystem.LS);
			sbr.append("#[secret] location write secret or address (address need to add to \n#RipplePower)\n");
			sbr.append("#not XRP , format: secret/1/btc/rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B \n");
			sbr.append("#secret位置可为私钥或者地址(如果使用地址发币,则地址需要添加到RipplePower)\n");
			sbr.append("#如非XRP,则格式为: secret/1/btc/rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B \n");
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("offer_create secret/1/xrp secret/1/btc/~Bitstamp");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("offer_cancel".equals(name)) {
			sbr.append("#Offer_cancel");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("offer_cancel 0");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("offer_price".equals(name)) {
			sbr.append("#Offer_price");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("offer_price ~Bitstamp btc usd");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}else if ("convert_price".equals(name)) {
			sbr.append("#Convert_price");
			sbr.append(LSystem.LS);
			sbr.append("{");
			sbr.append(LSystem.LS);
			sbr.append(" ");
			sbr.append("convert_price 16 cny jpy");
			sbr.append(LSystem.LS);
			sbr.append("}");
			sbr.append(LSystem.LS);
		}
		return sbr.toString();
	}

	public EditorDialog(Window parent) {
		super(parent, "ROC Script Editor(Developing)",
				Dialog.ModalityType.MODELESS);
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
					_script = null;
				}
				try {
					_script = new ROCScript(new Console(), _editorText
							.getText(), false);
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
