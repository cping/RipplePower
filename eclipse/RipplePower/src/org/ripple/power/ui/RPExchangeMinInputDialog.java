package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.WindowConstants;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.RPLabel;
import org.ripple.power.ui.view.RPTextBox;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;

public class RPExchangeMinInputDialog extends ABaseDialog implements WindowListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _okButton;
	private RPLabel _oneLabel;
	private RPLabel _twoLabel;
	private RPLabel _threeLabel;
	private RPTextBox _curPriceText;
	private RPTextBox _countText;
	private RPTextBox _countPriceText;

	private RPTextBox _textAContext, _textBContext;
	private String _curName;
	private String _dstCurrency = "USD";
	private ArrayList<WaitDialog> _waitDialogs = new ArrayList<WaitDialog>(10);

	public RPExchangeMinInputDialog(Window parent, String name) {
		super(parent, name, Dialog.ModalityType.MODELESS);
		addWindowListener(HelperWindow.get());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(UIRes.getIcon());
		Dimension dim = new Dimension(380, 200);
		setResizable(false);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	public static RPExchangeMinInputDialog showDialog(Window parent, String name) {
		final RPExchangeMinInputDialog dialog = new RPExchangeMinInputDialog(parent,
				name);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	private void checkPrice() {
		String price = _curPriceText.getText().trim();
		String count = _countText.getText().trim();
		if (StringUtils.isNumber(price)) {
			if (price.startsWith("0")) {
				price = new BigDecimal(price).toString();
			}

		} else {
			price = "0";
		}
		if (StringUtils.isNumber(count)) {
			if (count.startsWith("0")) {
				count = new BigDecimal(count).toString();
			}
		} else {
			count = "0";
		}
		_countPriceText.setText(price);
		_countText.setText(count);
		if (StringUtils.isNumber(price) && StringUtils.isNumber(count)) {
			if (!"0".equals(price) && !"0".equals(count)) {
				BigDecimal a = new BigDecimal(price);
				BigDecimal b = new BigDecimal(count);
				if (a.compareTo(BigDecimal.ZERO) == 1
						&& b.compareTo(BigDecimal.ZERO) == 1) {
					String cprice = LSystem.getNumber(a.multiply(b));
					_countPriceText.setText(cprice + "/" + _dstCurrency);
				} else {
					_countPriceText.setText("0");
					_countText.setText("0");
					_countPriceText.setText("0/" + _dstCurrency);
				}
			} else {
				_countPriceText.setText("0");
				_countText.setText("0");
				_countPriceText.setText("0/" + _dstCurrency);
			}
		}
	}

	private void initComponents() {

		_oneLabel = new RPLabel();
		_curPriceText = new RPTextBox();
		_curPriceText.setText("0");
		_curPriceText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkPrice();
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		_twoLabel = new RPLabel();
		_countText = new RPTextBox();
		_countText.setText("0");
		_threeLabel = new RPLabel();
		_countPriceText = new RPTextBox();
		_countPriceText.setText("0");
		_countPriceText.setEnabled(false);
		_okButton = new RPCButton();
		_okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_textAContext != null) {
					String price = _countText.getText().trim();
					_textAContext.setText(price + "/" + _curName);
				}
				if (_textBContext != null) {
					String price = _countPriceText.getText().trim();
					_textBContext.setText(price);
				}
				SwingUtils.close(RPExchangeMinInputDialog.this);
			}
		});

		_countText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkPrice();
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		Font font = GraphicsUtils.getFont(12);

		_oneLabel.setFont(font);
		_oneLabel.setText(LangConfig.get(this, "price", "Currency Price"));

		_twoLabel.setFont(font);
		_twoLabel.setText(LangConfig.get(this, "count", "Total"));

		_threeLabel.setFont(font);
		_threeLabel.setText(LangConfig.get(this, "all", "Total price"));

		_okButton.setText(UIMessage.ok);
		_okButton.setFont(UIRes.getFont());

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		_oneLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		88,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		_curPriceText))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		_twoLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		88,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		_countText,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		261,
																		Short.MAX_VALUE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		_threeLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		88,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		_countPriceText,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		261,
																		Short.MAX_VALUE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addGap(0,
																		0,
																		Short.MAX_VALUE)
																.addComponent(
																		_okButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		108,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(_oneLabel)
												.addComponent(
														_curPriceText,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(_twoLabel)
												.addComponent(
														_countText,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(_threeLabel)
												.addComponent(
														_countPriceText,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addComponent(_okButton,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										41, Short.MAX_VALUE).addContainerGap()));
		getContentPane().setBackground(UIConfig.dialogbackground);

		pack();
	}

	public RPTextBox getTextAContext() {
		return _textAContext;
	}

	public void setTextContext(RPTextBox a, RPTextBox b, final String curName,
			final String dstCurName) {
		if (a != null && b != null) {
			this._dstCurrency = dstCurName;
			this._textAContext = a;
			this._textBContext = b;
			this._countPriceText.setText("0");
			this._curPriceText.setText("0");
			this._countText.setText("0");
			this._curName = curName;
			this.setTitle(curName + "/" + dstCurName + "(Average Price)");
			if (!dstCurName.equals(curName)) {
				final WaitDialog waitDialog = WaitDialog.showDialog(this);
				_waitDialogs.add(waitDialog);
				Updateable update = new Updateable() {

					@Override
					public void action(Object o) {
						String value = OfferPrice.getMoneyConvert("1",
								_curName, dstCurName.toLowerCase());
						if (!"unkown".equals(value)) {
							_curPriceText.setText(value);
						}
						waitDialog.closeDialog();
					}
				};
				LSystem.postThread(update);
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (_waitDialogs != null) {
			for (WaitDialog wait : _waitDialogs) {
				if (wait != null) {
					wait.closeDialog();
				}
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
