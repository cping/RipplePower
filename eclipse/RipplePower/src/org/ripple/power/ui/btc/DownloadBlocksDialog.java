package org.ripple.power.ui.btc;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.view.ABaseDialog;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.SwingUtils;

public final class DownloadBlocksDialog extends ABaseDialog implements
		ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final StatusPanel statusPanel;

	public static DownloadBlocksDialog showDialog(String text, Window parent) {
		DownloadBlocksDialog dialog = new DownloadBlocksDialog(text, parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public DownloadBlocksDialog(String text, Window parent) {
		super(LSystem.applicationMain, "Download Bitcoin Blocks",
				Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		Dimension dim = new Dimension(parent.getWidth() - 150,
				parent.getHeight() - 100);
		setPreferredSize(dim);
		setSize(dim);

		statusPanel = new StatusPanel();
		setContentPane(statusPanel);

		addWindowListener(new ApplicationWindowListener());
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			String action = ae.getActionCommand();
			switch (action) {
			case "exit":
				exit();
				break;
			}
		} catch (Exception exc) {
			ErrorLog.get().logException("Exception while processing action event",
					exc);
		}
	}

	private void exit() throws IOException {
		BTCLoader.shutdown();
		SwingUtils.close(this);
	}

	private class ApplicationWindowListener extends WindowAdapter {

		public ApplicationWindowListener() {
		}

		@Override
		public void windowGainedFocus(WindowEvent we) {
		}

		@Override
		public void windowIconified(WindowEvent we) {

		}

		@Override
		public void windowDeiconified(WindowEvent we) {

		}

		@Override
		public void windowClosing(WindowEvent we) {
			try {
				exit();
			} catch (Exception exc) {
				ErrorLog.get().logException(
						"Exception while closing application window", exc);
			}
		}
	}
}
