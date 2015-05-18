package org.ripple.power.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.Gradation;
import org.ripple.power.sound.MP4Player;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;
import org.ripple.power.wallet.WalletCache;

public class RPCScrollPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Gradation _gradation;

	private LColor _colorStart, _colorEnd;

	private int _alpha;

	private boolean _isRunning;

	private int _address_limit = 1;

	private LImage _image;

	private LGraphics _graphics;

	private MP4Player _player;

	public RPCScrollPane(Component view, LColor start, LColor end, int alpha) {
		super(view);
		_colorStart = start;
		_colorEnd = end;
		_alpha = alpha;
		_isRunning = WalletCache.get().size() < _address_limit;
		setOpaque(false);
		setBackground(new LColor(0f, 0F, 0F, 0F));
		setBorder(new EmptyBorder(1, 1, 1, 1));
		getViewport().setOpaque(false);

	}

	private Thread _thread;

	private void loop() {
		_isRunning = true;
		final long timer = 120;
		Updateable update = new Updateable() {
			public void action(Object o) {
				for (; _isRunning && WalletCache.get().size() < _address_limit;) {
					if (_player != null) {
						_player.update(timer);
					}
					repaint();
					try {
						Thread.sleep(timer);
					} catch (InterruptedException e) {
					}
				}
				repaint();
				_image = null;
				_isRunning = false;
			}
		};
		if (_thread == null) {
			_thread = LSystem.postThread(update);
		} else {
			try {
				_isRunning = false;
				_thread.interrupt();
				_thread = null;
			} catch (Exception ex) {
			}
			_isRunning = true;
			_thread = LSystem.postThread(update);
		}
	}

	protected synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (WalletCache.get().size() < _address_limit) {
			if (_image == null) {
				loop();
				_image = new LImage(getWidth(), getHeight());
				_graphics = _image.getLGraphics();
				_player = new MP4Player(getWidth(), getHeight());
				_player.play("ripple");
			}
			_graphics.drawClear();
			_player.drawUI(_graphics, 0, 0, getWidth(), getHeight());
			g.drawImage(_image.getBufferedImage(), 0, 0, this);
		} else {
			if (_gradation == null) {
				_gradation = Gradation.getInstance(_colorStart, _colorEnd,
						getWidth(), getHeight(), _alpha);
			}
			_gradation.drawHeight(g, 0, 0);
			g.setColor(LColor.white.brighter());
			g.drawRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
			g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 0, 0);
		}
	}
}