package org.ripple.power.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.ripple.power.config.LSystem;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.StringUtils;

public class RPExchangeInputDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RPCButton _okButton;
	private RPLabel jLabel1;
	private RPLabel jLabel2;
	private RPLabel jLabel3;
	private RPTextBox _curPriceText;
	private RPTextBox _countText;
	private RPTextBox _countPriceText;

	private RPTextBox _textContext;
	private String _curName;
	private String _dstCurrency = "USD";

	public RPExchangeInputDialog(Window parent, String name) {
		super(parent, name, Dialog.ModalityType.MODELESS);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Dimension dim = new Dimension(380, 200);
		setResizable(false);
		setPreferredSize(dim);
		setSize(dim);
		initComponents();
	}

	public static RPExchangeInputDialog showDialog(Window parent, String name) {
		final RPExchangeInputDialog dialog = new RPExchangeInputDialog(parent,
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
					String cprice = a.multiply(b).toString();
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

		jLabel1 = new RPLabel();
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
		jLabel2 = new RPLabel();
		_countText = new RPTextBox();
		_countText.setText("0");
		jLabel3 = new RPLabel();
		_countPriceText = new RPTextBox();
		_countPriceText.setText("0");
		_countPriceText.setEnabled(false);
		_okButton = new RPCButton();
		_okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_textContext != null) {
					String price = _countText.getText().trim();
					_textContext.setText(price + "/" + _curName);
				}

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

		jLabel1.setFont(font);
		jLabel1.setText(LangConfig.get(this, "price", "Currency Price"));

		jLabel2.setFont(font);
		jLabel2.setText(LangConfig.get(this, "count", "Total"));

		jLabel3.setFont(font);
		jLabel3.setText(LangConfig.get(this, "all", "Total price"));

		_okButton.setText(LangConfig.get(this, "ok", "OK"));
		_okButton.setFont(GraphicsUtils.getFont(14));

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
																		jLabel1,
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
																		jLabel2,
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
																		jLabel3,
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
												.addComponent(jLabel1)
												.addComponent(
														_curPriceText,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jLabel2)
												.addComponent(
														_countText,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel3)
												.addComponent(
														_countPriceText,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addComponent(_okButton,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										41, Short.MAX_VALUE).addContainerGap()));
		getContentPane().setBackground(LSystem.dialogbackground);

		pack();
	}

	public RPTextBox getTextContext() {
		return _textContext;
	}

	public void setTextContext(RPTextBox text, String curName) {
		if (text != null) {
			this._textContext = text;
			this._countPriceText.setText("0");
			this._curPriceText.setText("0");
			this._countText.setText("0");
			this._curName = curName;
			this.setTitle(curName + "/USD(Average Price)");
			final WaitDialog waitDialog = WaitDialog.showDialog(this);
			Updateable update = new Updateable() {

				@Override
				public void action(Object o) {
					String value = OfferPrice.getMoneyConvert("1", _curName,
							_dstCurrency.toLowerCase());
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
