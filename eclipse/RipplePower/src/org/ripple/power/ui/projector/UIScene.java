package org.ripple.power.ui.projector;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JPanel;

import org.ripple.power.config.LSystem;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.RPDialogTool;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.core.LHandler;
import org.ripple.power.ui.projector.core.graphics.Screen;
import org.ripple.power.ui.view.WaitDialog;
import org.ripple.power.wallet.WalletItem;

public class UIScene extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static UIScene lock = null;

	private RPDialogTool tool;
	private LHandler handler;
	private Updateable exit;

	public static UIScene showDialog(String text, int width, int height,
			Screen screen, Window parent, WalletItem item, boolean show) {
		if (show) {
			synchronized (UIScene.class) {
				if (lock == null) {
					return (lock = new UIScene(text, width, height, screen,
							parent, item));
				} else {
					if (lock != null) {
						lock.closeDialog();
						lock = new UIScene(text, width, height, screen, parent,
								item);
					}
					return lock;
				}
			}
		}
		return null;
	}

	public static UIScene showDialog(String text, int width, int height,
			Screen screen, Window parent, WalletItem item) {
		return showDialog(text, width, height, screen, parent, item, true);
	}

	public RPDialogTool get() {
		return tool;
	}

	private void close() {
		if (handler != null) {
			if (handler.getView() != null) {
				handler.getView().endPaint();
				handler.getView().setRunning(false);
				LSystem.destroy();
			}
		}
		if (exit != null) {
			exit.action(this);
		}
	}

	public void closeDialog() {
		synchronized (WaitDialog.class) {
			close();
			tool.close();
			lock = null;
		}
	}

	public UIScene(String text, final int width, final int height,
			final Screen screen, Window parent, WalletItem item) {
		LSystem.screenRect = new RectBox(0, 0, width, height);
		Dimension dim = new Dimension(width, height);
		setPreferredSize(dim);
		setSize(dim);
		setLayout(null);
		setBackground(UIConfig.dialogbackground);
		this.tool = RPDialogTool.show(parent, text, this, -1, -1, false,
				LSystem.MINUTE);
		Updateable update = new Updateable() {

			@Override
			public void action(Object o) {
				tool.setClose(new Updateable() {

					@Override
					public void action(Object o) {
						close();
					}
				});
				UIScene.this.handler = new LHandler(tool.getDialog(), width,
						height);
				UIScene.this.handler.setScreen(screen);
				UIView view = new UIView(handler);
				add(view);
				view.setFPS(LSystem.DEFAULT_MAX_FPS);
				view.createScreen();
				view.setShowFPS(false);
				view.mainLoop();
				view.startPaint();

			}
		};
		LSystem.postThread(update);
	}

	public Updateable getExit() {
		return exit;
	}

	public void setExit(Updateable exit) {
		this.exit = exit;
	}

}
