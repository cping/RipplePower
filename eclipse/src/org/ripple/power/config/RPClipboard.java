package org.ripple.power.config;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class RPClipboard implements ClipboardOwner {

	public String getClipboardContents() {
		String result = "";
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
			}
		}
		return result;
	}

	public void setClipboardContents(String content) {
		StringSelection stringSelection = new StringSelection(content);
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

	}

}
