package org.ripple.power.ui.btc;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JMenuBar;

import org.ripple.power.config.LSystem;
import org.ripple.power.helper.HelperWindow;
import org.ripple.power.txns.btc.AddressFormatException;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.txns.btc.BlockStoreListener;
import org.ripple.power.txns.btc.ConnectionListener;
import org.ripple.power.txns.btc.DumpedPrivateKey;
import org.ripple.power.txns.btc.ECKey;
import org.ripple.power.txns.btc.InventoryItem;
import org.ripple.power.txns.btc.InventoryMessage;
import org.ripple.power.txns.btc.Message;
import org.ripple.power.txns.btc.Peer;
import org.ripple.power.txns.btc.SendTransaction;
import org.ripple.power.txns.btc.StoredHeader;
import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.view.Menus;
import org.ripple.power.ui.view.log.ErrorLog;
import org.ripple.power.utils.SwingUtils;

public final class BitcoinWalletDialog extends JDialog implements
		ActionListener, ConnectionListener, BlockStoreListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean synchronizingTitle = false;

	private boolean txBroadcastDone = false;

	private boolean rescanChain = false;

	private final TransactionPanel transactionPanel;

	public static BitcoinWalletDialog showDialog(String text, Window parent) {
		BitcoinWalletDialog dialog = new BitcoinWalletDialog(text, parent);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		return dialog;
	}

	public BitcoinWalletDialog(String text, Window parent) {
		super(LSystem.applicationMain, text, Dialog.ModalityType.DOCUMENT_MODAL);
		addWindowListener(HelperWindow.get());
		setIconImage(UIRes.getIcon());
		setResizable(false);
		int frameWidth = 900;
		int frameHeight = 580;
		if (LSystem.applicationMain != null) {
			frameWidth = LSystem.applicationMain.getWidth() - 150;
			frameHeight = LSystem.applicationMain.getHeight() - 150;
		}
		setPreferredSize(new Dimension(frameWidth, frameHeight));
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(new Color(230, 230, 230));
		menuBar.add(new Menus(this, "File", new String[] { "Exit", "exit" }));
		menuBar.add(new Menus(this, "View", new String[] { "Receive Addresses",
				"view receive" },
				new String[] { "Send Addresses", "view send" }));
		menuBar.add(new Menus(this, "Actions", new String[] { "Send Coins",
				"send coins" },
				new String[] { "Sign Message", "sign message" }, new String[] {
						"Verify Message", "verify message" }));
		menuBar.add(new Menus(this, "Tools", new String[] { "Export Keys",
				"export keys" }, new String[] { "Import Keys", "import keys" },
				new String[] { "Rescan Block Chain", "rescan" }));
		
		setJMenuBar(menuBar);
		transactionPanel = new TransactionPanel(this);
		setContentPane(transactionPanel);
		if (BTCLoader.networkChainHeight > BTCLoader.blockStore
				.getChainHeight()) {
			setTitle("Bitcoin Wallet - Synchronizing with network");
			synchronizingTitle = true;
		}
		addWindowListener(new ApplicationWindowListener());
		BTCLoader.networkHandler.addListener(this);
		BTCLoader.databaseHandler.addListener(this);
	}

	@Override
	public void addChainBlock(StoredHeader blockHeader) {
		LSystem.invokeLater(new Runnable() {

			@Override
			public void run() {

				transactionPanel.statusChanged();
				if (synchronizingTitle
						&& !rescanChain
						&& BTCLoader.networkChainHeight <= BTCLoader.blockStore
								.getChainHeight()) {
					synchronizingTitle = false;
					setTitle("Bitcoin Wallet");
				}

			}
		});
	}

	@Override
	public void txUpdated() {
		LSystem.invokeLater(new Runnable() {

			@Override
			public void run() {

				transactionPanel.walletChanged();

			}
		});
	}

	@Override
	public void rescanCompleted() {
		LSystem.invokeLater(new Runnable() {

			@Override
			public void run() {

				rescanChain = false;
				transactionPanel.statusChanged();
				if (synchronizingTitle
						&& BTCLoader.networkChainHeight <= BTCLoader.blockStore
								.getChainHeight()) {
					synchronizingTitle = false;
					setTitle("Bitcoin Wallet");
				}

			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			String action = ae.getActionCommand();
			switch (action) {
			case "exit":
				exit();
				break;
			case "view receive":
				ReceiveAddressDialog.showDialog(this);
				transactionPanel.statusChanged();
				break;
			case "view send":
				SendAddressDialog.showDialog(this);
				transactionPanel.statusChanged();
				break;
			case "send coins":
				SendDialog.showDialog(this);
				break;
			case "sign message":
				if (BTCLoader.keys.isEmpty()) {
					UIRes.showErrorMessage(this,"Error",
							"There are no keys defined");
				} else {
					SignDialog.showDialog(this);
				}
				break;
			case "verify message":
				VerifyDialog.showDialog(this);
				break;
			case "export keys":
				exportDefPrivateKeys();
				break;
			case "import keys":
				importDefPrivateKeys();
				break;
			case "rescan":
				rescan();
				break;
			}
		} catch (IOException exc) {
			ErrorLog.get().logException("Unable to process key file", exc);
		} catch (AddressFormatException exc) {
			ErrorLog.get().logException("Key format is not valid", exc);
		} catch (BlockStoreException exc) {
			ErrorLog.get().logException("Unable to perform database operation", exc);
		} catch (Exception exc) {
			ErrorLog.get().logException("Exception while processing action event",
					exc);
		}
	}

	private void exportDefPrivateKeys() throws IOException {
		StringBuilder keyText = new StringBuilder(256);
		File keyFile = new File(LSystem.getBitcionDirectory() + LSystem.FS
				+ "BTCWallet.keys");
		if (keyFile.exists()) {
			keyFile.delete();
		}
		try (BufferedWriter out = new BufferedWriter(new FileWriter(keyFile))) {
			for (ECKey key : BTCLoader.keys) {
				String address = key.toAddress().toString();
				DumpedPrivateKey dumpedKey = key.getPrivKeyEncoded();
				keyText.append("Label:");
				keyText.append(key.getLabel());
				keyText.append("\nTime:");
				keyText.append(Long.toString(key.getCreationTime()));
				keyText.append("\nAddress:");
				keyText.append(address);
				keyText.append("\nPrivate:");
				keyText.append(dumpedKey.toString());
				keyText.append("\n\n");
				out.write(keyText.toString());
				keyText.delete(0, keyText.length());
			}
		}
		UIRes.showInfoMessage(this,"Keys Exported", "Keys exported to BTCWallet.keys"
				);
	}

	private void importDefPrivateKeys() throws IOException,
			AddressFormatException, BlockStoreException {
		File keyFile = new File(LSystem.getBitcionDirectory() + LSystem.FS
				+ "BTCWallet.keys");
		if (!keyFile.exists()) {
			UIRes.showErrorMessage(this,"Error",
					"BTCWallet.keys does not exist");
			return;
		}

		try (BufferedReader in = new BufferedReader(new FileReader(keyFile))) {
			String line;
			String importedLabel = "";
			String importedTime = "";
			String importedAddress = "";
			String encodedPrivateKey = "";
			boolean foundKey = false;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				int sep = line.indexOf(':');
				if (sep < 1 || line.length() == sep + 1) {
					continue;
				}
				String keyword = line.substring(0, sep);
				String value = line.substring(sep + 1);
				switch (keyword) {
				case "Label":
					importedLabel = value;
					break;
				case "Time":
					importedTime = value;
					break;
				case "Address":
					importedAddress = value;
					break;
				case "Private":
					encodedPrivateKey = value;
					foundKey = true;
					break;
				}
				if (foundKey) {
					DumpedPrivateKey dumpedKey = new DumpedPrivateKey(
							encodedPrivateKey);
					ECKey key = dumpedKey.getKey();
					if (importedAddress.equals(key.toAddress().toString())) {
						key.setLabel(importedLabel);
						key.setCreationTime(Long.parseLong(importedTime));
						if (!BTCLoader.keys.contains(key)) {
							BTCLoader.blockStore.storeKey(key);
							synchronized (BTCLoader.lock) {
								boolean added = false;
								for (int i = 0; i < BTCLoader.keys.size(); i++) {
									if (BTCLoader.keys.get(i).getLabel()
											.compareToIgnoreCase(importedLabel) > 0) {
										BTCLoader.keys.add(i, key);
										added = true;
										break;
									}
								}
								if (!added)
									BTCLoader.keys.add(key);
								BTCLoader.bloomFilter.insert(key.getPubKey());
								BTCLoader.bloomFilter.insert(key
										.getPubKeyHash());
							}
						}
					} else {
						UIRes.showErrorMessage(
										this, "Error",
										String.format(
												"Address %s does not match imported private key",
												importedAddress));
					}
					foundKey = false;
					importedLabel = "";
					importedTime = "";
					importedAddress = "";
					encodedPrivateKey = "";
				}
			}
		}
		UIRes.showInfoMessage(this,"Keys Imported",
				"Keys imported from BTCWallet.keys");
	}

	private void rescan() throws BlockStoreException {
		long creationTime = System.currentTimeMillis() / 1000;
		for (ECKey key : BTCLoader.keys) {
			creationTime = Math.min(creationTime, key.getCreationTime());
		}
		synchronizingTitle = true;
		rescanChain = true;
		setTitle("Bitcoin Wallet - Synchronizing with network");
		BTCLoader.blockStore.deleteTransactions(creationTime);
		transactionPanel.walletChanged();
		BTCLoader.databaseHandler.rescanChain(creationTime);
	}

	private void exit() throws IOException {
		BTCLoader.shutdown();
		SwingUtils.close(this);
	}

	private class ApplicationWindowListener extends WindowAdapter {

		public ApplicationWindowListener() {
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

	@Override
	public void connectionStarted(Peer peer, int count) {
		if (!synchronizingTitle
				&& BTCLoader.networkChainHeight > BTCLoader.blockStore
						.getChainHeight()) {
			synchronizingTitle = true;
			LSystem.invokeLater(new Runnable() {

				@Override
				public void run() {

					setTitle("Bitcoin Wallet - Synchronizing with network");

				}
			});
		}
		if (!txBroadcastDone) {
			txBroadcastDone = true;
			try {
				List<SendTransaction> sendList = BTCLoader.blockStore
						.getSendTxList();
				if (!sendList.isEmpty()) {
					List<InventoryItem> invList = new ArrayList<InventoryItem>(
							sendList.size());
					for (SendTransaction sendTx : sendList) {
						int depth = BTCLoader.blockStore.getTxDepth(sendTx
								.getTxHash());
						if (depth == 0)
							invList.add(new InventoryItem(InventoryItem.INV_TX,
									sendTx.getTxHash()));
					}
					if (!invList.isEmpty()) {
						Message invMsg = InventoryMessage
								.buildInventoryMessage(peer, invList);
						BTCLoader.networkHandler.sendMessage(invMsg);
						BTCLoader.info(String.format(
								"Pending transaction inventory sent to %s",
								peer.getAddress().toString()));
					}
				}
			} catch (BlockStoreException exc) {
				ErrorLog.get().logException("Unable to get send transaction list",
						exc);
			}
		}

	}

	@Override
	public void connectionEnded(Peer peer, int count) {

	}
}
