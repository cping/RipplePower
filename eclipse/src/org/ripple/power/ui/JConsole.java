package org.ripple.power.ui;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JConsole extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OutputStream fromConsoleStream;
	private InputStream in;
	private PrintStream out, err;
	private AttributeSet attrOut, attrError;
	private PipePump outPump, errPump;

	public void uiprint(String mes) {
		if (attrOut != null) {
			print(mes, attrOut);
		}
	}

	private int commandPos = 0;

	private List<String> commandHistory = new ArrayList<String>();

	private int commandHistoryIndex = 0;

	private String currentCommand;

	private MyJTextPane text;

	private JPopupMenu contextMenu;
	private final static String CMD_CUT = "Cut";
	private final static String CMD_COPY = "Copy";
	private final static String CMD_PASTE = "Paste";

	public JConsole() {
		super();

		setBackground(new Color(70, 70, 70));
		setForeground(Color.WHITE);
		text = new MyJTextPane();

		text.setAutoscrolls(true);
		final Font lFont = new Font("Monospaced", Font.PLAIN, 12);
		text.setText("");
		text.setFont(lFont);
		text.setMargin(new Insets(5, 3, 5, 3));
		text.addKeyListener(new MyKeyListener());
		setViewportView(text);

		contextMenu = new JPopupMenu();
		final ActionListener lActionListener = new MyActionListener();
		contextMenu.add(new JMenuItem(CMD_CUT)).addActionListener(
				lActionListener);
		contextMenu.add(new JMenuItem(CMD_COPY)).addActionListener(
				lActionListener);
		contextMenu.add(new JMenuItem(CMD_PASTE)).addActionListener(
				lActionListener);
		text.addMouseListener(new MyMouseListener());

		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setForeground(attr, Color.BLACK);

		attr = new SimpleAttributeSet();
		StyleConstants.setForeground(attr, Color.WHITE);
		attrOut = attr;

		attr = new SimpleAttributeSet();
		StyleConstants.setForeground(attr, Color.RED);
		StyleConstants.setItalic(attr, true);
		StyleConstants.setBold(attr, true);
		attrError = attr;

		try {
			fromConsoleStream = new PipedOutputStream();
			in = new PipedInputStream((PipedOutputStream) fromConsoleStream);

			final PipedOutputStream lOutPipe = new PipedOutputStream();
			out = new PrintStream(lOutPipe);

			final PipedOutputStream lErrPipe = new PipedOutputStream();
			err = new PrintStream(lErrPipe);

		} catch (IOException e) {
			e.printStackTrace();
		}
		requestFocus();
	}

	public void setOutAttributes(AttributeSet aAttribs) {
		attrOut = aAttribs;
		if (outPump != null) {
			outPump.setAttr(aAttribs);
		}
	}
	
	public void setErrAttributes(AttributeSet aAttribs) {
		attrError = aAttribs;
		if (errPump != null) {
			errPump.setAttr(aAttribs);
		}
	}

	public InputStream getInputStream() {
		return in;
	}

	/**
	 * Get the input reader containing the text the user enters in the console.
	 * 
	 * @return A text reader from which the user commands can be read.
	 */
	public Reader getIn() {
		return new InputStreamReader(in);
	}

	/**
	 * The output stream to the console.
	 * 
	 * @return An output stream on which the application can write text to the
	 *         console.
	 */
	public PrintStream getOut() {
		return out;
	}

	/**
	 * The error stream to the console.
	 * 
	 * @return An output stream on which the application can write text to the
	 *         console.
	 */
	public PrintStream getErr() {
		return err;
	}

	// Focus handling.
	public void requestFocus() {
		super.requestFocus();
		text.requestFocus();
	}

	// Remember the start of the command line.
	private void initCommandPos() {
		commandPos = textLength();
	}

	// Append text to the end of the text already present in the
	// text component.
	private void appendConsoleText(String aContent) {
		final int lTxtLen = textLength();
		text.select(lTxtLen, lTxtLen);
		text.replaceSelection(aContent);
	}

	// Replace part of the text in the text component.
	private String replaceConsoleText(Object aContent, int aFrom, int aTo) {
		final String aContentRepr = aContent.toString();
		text.select(aFrom, aTo);
		text.replaceSelection(aContentRepr);
		return aContentRepr;
	}

	private void moveCaret() {
		if (text.getCaretPosition() < commandPos) {
			text.setCaretPosition(textLength());
		}
		text.repaint();
	}

	private void processCommand() {
		String lCommandRepr = getCmd();
		if (lCommandRepr.length() != 0)
			commandHistory.add(lCommandRepr);
		lCommandRepr = lCommandRepr + "\n";

		appendConsoleText("\n");
		commandHistoryIndex = 0;
		acceptLine(lCommandRepr);
		text.repaint();
	}

	private String getCmd() {
		try {
			return text.getText(commandPos, textLength() - commandPos);
		} catch (BadLocationException e) {
			return "";
		}
	}

	// Command history manipulation, go to the previous command.
	// Note that the index runs in reverse.
	private void prevHistory() {
		if (commandHistory.size() == 0)
			return;
		if (commandHistoryIndex == 0)
			currentCommand = getCmd();
		if (commandHistoryIndex < commandHistory.size()) {
			commandHistoryIndex++;
			showHistory();
		}
	}

	// Command history manipulation, go to the next command.
	// Note that the index runs in reverse.
	private void nextHistory() {
		if (commandHistoryIndex == 0)
			return;
		commandHistoryIndex--;
		showHistory();
	}

	// Show the command from the command history, pointed to by the index.
	// Note that the index runs in reverse.
	private void showHistory() {
		String lShowLine;
		if (commandHistoryIndex == 0)
			lShowLine = currentCommand;
		else
			lShowLine = commandHistory.get(commandHistory.size()
					- commandHistoryIndex);

		replaceConsoleText(lShowLine, commandPos, textLength());
		text.setCaretPosition(textLength());
		text.repaint();
	}

	/**
	 * The user of the component can write a command in the console as if the
	 * user typed the command himself. The application using the console can
	 * simnulate user actions in this way.
	 * 
	 * @param aCommand
	 */
	public void setCommand(String aCommand) {
		String lCommandRepr = aCommand;

		if (lCommandRepr.length() != 0)
			commandHistory.add(lCommandRepr);
		lCommandRepr = lCommandRepr + "\n";

		appendConsoleText(lCommandRepr);
		commandHistoryIndex = 0;
		acceptLine(lCommandRepr);
		text.repaint();
	}

	// Put the text that the user typed into the pipe, so that
	// interested console clients can read the stuff from the in stream.
	private void acceptLine(String aLine) {
		try {
			fromConsoleStream.write(aLine.getBytes());
			fromConsoleStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear the console.
	 */
	public void clear() {
		text.setText("");
		text.repaint();
	}

	/**
	 * Print output to the console. Note that this will not be interpreted as a
	 * command line. See the setCommand() method for this functionality.
	 * 
	 * @param aContent
	 *            Print it to the console.
	 */
	public void print(final Object aContent) {
		invokeAndWait(new Runnable() {
			public void run() {
				appendConsoleText(String.valueOf(aContent));
				initCommandPos();
				text.setCaretPosition(commandPos);
			}
		});
	}

	/**
	 * Print error to the console. Note that this will not be interpreted as a
	 * command line. See the setCommand() method for this functionality.
	 * 
	 * @param aContent
	 *            Print it to the console.
	 */
	public void error(Object aContent) {
		print(aContent, attrError);
	}

	/**
	 * Print output to the console. Note that this will not be interpreted as a
	 * command line. See the setCommand() method for this functionality.
	 * 
	 * @param aContent
	 *            The message to be written to the console.
	 * @param aAttribs
	 *            The text attributes used for this message.
	 */
	public void print(final Object aContent, final AttributeSet aAttribs) {
		invokeAndWait(new Runnable() {
			public void run() {
				

				appendConsoleText(String.valueOf(aContent));
				initCommandPos();
				text.setCaretPosition(commandPos);

			}
		});
	}

	public void setFont(Font aFont) {
		super.setFont(aFont);
		if (text != null)
			text.setFont(aFont);
	}

	// Utility method to make sure a task is executed on the Swing display
	// thread.
	private void invokeAndWait(Runnable aRunnable) {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(aRunnable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			aRunnable.run();
		}
	}

	private int textLength() {
		return text.getDocument().getLength();
	}

	// The PipePump waits for input on an output stream, and copies the content
	// to the console window.
	// It is used to get the text from the pipes which are provided to the user
	// of the console.
	// It is the connection between the user streams and the console content.
	private class PipePump implements Runnable {
		private InputStream in;
		private AttributeSet attr;

		public void setAttr(AttributeSet aAttr) {
			attr = aAttr;
		}

		public void run() {
			try {
				final byte[] lBuf = new byte[1024];
				int lBytesRead;

				while (in.read(lBuf, 0, 1) != -1) {
					synchronized (JConsole.this) {
						lBytesRead = in.read(lBuf, 1, 1023) + 1;
						print(new String(lBuf, 0, lBytesRead), attr);

						while (in.available() > 0) {
							lBytesRead = in.read(lBuf);
							print(new String(lBuf, 0, lBytesRead), attr);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class MyKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			type(e);
		}

		public void keyTyped(KeyEvent e) {
			type(e);
		}

		public void keyReleased(KeyEvent e) {
			type(e);
		}

		private synchronized void type(KeyEvent e) {
			switch (e.getKeyCode()) {
			case (KeyEvent.VK_ENTER):
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					processCommand();
					initCommandPos();
					text.setCaretPosition(commandPos);
				}
				e.consume();
				text.repaint();
				break;
			case (KeyEvent.VK_UP):
				if (e.getID() == KeyEvent.KEY_PRESSED)
					prevHistory();
				e.consume();
				break;

			case (KeyEvent.VK_DOWN):
				if (e.getID() == KeyEvent.KEY_PRESSED)
					nextHistory();
				e.consume();
				break;
			case (KeyEvent.VK_LEFT):
			case (KeyEvent.VK_BACK_SPACE):
			case (KeyEvent.VK_DELETE):
				if (text.getCaretPosition() <= commandPos)
					e.consume();
				break;
			case (KeyEvent.VK_HOME):
				text.setCaretPosition(commandPos);
				e.consume();
				break;
			case (KeyEvent.VK_U):
				if ((e.getModifiers() & InputEvent.CTRL_MASK) > 0) {
					replaceConsoleText("", commandPos, textLength());
					commandHistoryIndex = 0;
					e.consume();
				}
				break;
			case (KeyEvent.VK_ALT):
			case (KeyEvent.VK_CAPS_LOCK):
			case (KeyEvent.VK_CONTROL):
			case (KeyEvent.VK_META):
			case (KeyEvent.VK_SHIFT):
			case (KeyEvent.VK_PRINTSCREEN):
			case (KeyEvent.VK_SCROLL_LOCK):
			case (KeyEvent.VK_PAUSE):
			case (KeyEvent.VK_INSERT):
			case (KeyEvent.VK_F1):
			case (KeyEvent.VK_F2):
			case (KeyEvent.VK_F3):
			case (KeyEvent.VK_F4):
			case (KeyEvent.VK_F5):
			case (KeyEvent.VK_F6):
			case (KeyEvent.VK_F7):
			case (KeyEvent.VK_F8):
			case (KeyEvent.VK_F9):
			case (KeyEvent.VK_F10):
			case (KeyEvent.VK_F11):
			case (KeyEvent.VK_F12):
			case (KeyEvent.VK_ESCAPE):
			case (KeyEvent.VK_C):
				break;
			default:
				if ((e.getModifiers() & (InputEvent.CTRL_MASK
						| InputEvent.ALT_MASK | InputEvent.META_MASK)) == 0) {
					moveCaret();
				}

				if ((e.paramString().contains("Backspace"))
						&& (text.getCaretPosition() <= commandPos)) {
					e.consume();
				}
				break;
			}
		}
	}

	private class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent aEvent) {
			if (aEvent.isPopupTrigger())
				contextMenu.show((Component) aEvent.getSource(), aEvent.getX(),
						aEvent.getY());
		}

		public void mouseReleased(MouseEvent aEvent) {
			if (aEvent.isPopupTrigger())
				contextMenu.show((Component) aEvent.getSource(), aEvent.getX(),
						aEvent.getY());
		}
	}

	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent aEvent) {
			final String lActionCommand = aEvent.getActionCommand();
			if (lActionCommand.equals(CMD_CUT)) {
				text.cut();
			} else if (lActionCommand.equals(CMD_COPY)) {
				text.copy();
			} else if (lActionCommand.equals(CMD_PASTE)) {
				text.paste();
			}
		}
	}

	private class MyJTextPane extends RPTextArea {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyJTextPane() {
			super();
			setCaretColor(Color.WHITE);
			setBackground(new Color(0, 0, 0));
			setForeground(Color.WHITE);
			setLineWrap(true);
			setColumns(20);
			setRows(5);
		}


		public void cut() {
			if (text.getCaretPosition() < commandPos)
				super.copy();
			else
				super.cut();
		}

		public void paste() {
			moveCaret();
			super.paste();
		}
	}
}