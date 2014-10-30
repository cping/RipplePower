package org.ripple.power.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;

public class RPToast extends JDialog {
	private static final long serialVersionUID = -1602907470843951525L;

	public enum Style {
		NORMAL, SUCCESS, ERROR
	};

	public static final int LENGTH_SHORT = 3000;
	public static final int LENGTH_LONG = 6000;
	public static final Color ERROR_RED = new Color(121, 0, 0);
	public static final Color SUCCESS_GREEN = new Color(127, 127, 127);
	public static final Color NORMAL_BLACK = new Color(0, 0, 0);
	private final float MAX_OPACITY = 0.8f;
	private final float OPACITY_INCREMENT = 0.05f;
	private final int FADE_REFRESH_RATE = 20;
	private final int WINDOW_RADIUS = 15;
	private final int CHARACTER_LENGTH_MULTIPLIER = 10;

	private Window mOwner;
	private String mText;
	private int mDuration;
	private Color mBackgroundColor = Color.BLACK;
	private Color mForegroundColor = Color.WHITE;

	public RPToast(Window owner) {
		super(owner, Dialog.ModalityType.MODELESS);
		mOwner = owner;
	}

	private void createGUI() {
		setResizable(false);
		setLayout(new GridBagLayout());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setShape(new RoundRectangle2D.Double(0, 0, getWidth(),
						getHeight(), WINDOW_RADIUS, WINDOW_RADIUS));
			}
		});
		setAlwaysOnTop(true);
		setUndecorated(true);
		setFocusableWindowState(false);
		setModalityType(ModalityType.MODELESS);
		setSize(mText.length() * CHARACTER_LENGTH_MULTIPLIER, 25);
		getContentPane().setBackground(mBackgroundColor);
		JLabel label = new JLabel(mText);
		label.setForeground(mForegroundColor);
		add(label);
		setLocationRelativeTo(mOwner);
	}

	public void fadeIn() {
		final Timer timer = new Timer(FADE_REFRESH_RATE, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			private float opacity = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				opacity += OPACITY_INCREMENT;
				setOpacity(Math.min(opacity, MAX_OPACITY));
				if (opacity >= MAX_OPACITY) {
					timer.stop();
				}
			}
		});
		setOpacity(0);
		timer.start();
		setVisible(true);
	}

	public void fadeOut() {
		final Timer timer = new Timer(FADE_REFRESH_RATE, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			private float opacity = MAX_OPACITY;

			@Override
			public void actionPerformed(ActionEvent e) {
				opacity -= OPACITY_INCREMENT;
				setOpacity(Math.max(opacity, 0));
				if (opacity <= 0) {
					timer.stop();
					setVisible(false);
					dispose();
				}
			}
		});
		setOpacity(MAX_OPACITY);
		timer.start();
	}


	public void setText(String text) {
		mText = text;
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	@Override
	public void setBackground(Color backgroundColor) {
		mBackgroundColor = backgroundColor;
	}

	@Override
	public void setForeground(Color foregroundColor) {
		mForegroundColor = foregroundColor;
	}

	public static RPToast makeText(JFrame owner, String text) {
		return makeText(owner, text, LENGTH_SHORT);
	}

	public static RPToast makeText(JFrame owner, String text, Style style) {
		return makeText(owner, text, LENGTH_SHORT, style);
	}

	public static RPToast makeText(JFrame owner, String text, int duration) {
		return makeText(owner, text, duration, Style.NORMAL);
	}

	public static RPToast makeText(JFrame owner, String text, int duration,
			Style style) {
		RPToast toast = new RPToast(owner);
		toast.mText = text;
		toast.mDuration = duration;
		if (style == Style.SUCCESS) {
			toast.mBackgroundColor = SUCCESS_GREEN;
		}
		if (style == Style.ERROR) {
			toast.mBackgroundColor = ERROR_RED;
		}
		if (style == Style.NORMAL) {
			toast.mBackgroundColor = NORMAL_BLACK;
		}
		return toast;
	}

	public void display() {
		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				try {
					createGUI();
					fadeIn();
					Thread.sleep(mDuration);
					fadeOut();
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		LSystem.postThread(update);
	}

}