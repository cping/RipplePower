package org.ripple.power.ui.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.JTextPane;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.utils.GraphicsUtils;

public class SourcePaper extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Font _font;

	public SourcePaper() {
		super();
		this._font = GraphicsUtils.getFont(Font.MONOSPACED, 1, 15);
		this.setOpaque(false);
		this.setFont(_font);
		this.setCaretColor(LColor.white);
		this.setBackground(new LColor(70, 70, 70));
		this.setForeground(LColor.white);
		this.setAutoscrolls(true);
	}

	@Override
	public Insets getInsets() {
		return getInsets(new Insets(0, 0, 0, 0));
	}

	@Override
	public Insets getInsets(Insets insets) {
		insets = super.getInsets(insets);
		insets.left += lineNumberWidth() + 20;
		return insets;
	}

	private int lineNumberWidth() {
		int lineCount = getLineCount();
		return getFontMetrics(getFont()).stringWidth(lineCount + " ");
	}

	private LColor color1 = new LColor(230, 230, 230);

	private LColor color2 = new LColor(40, 40, 40);

	@Override
	public void paintComponent(Graphics g) {
		Insets insets = getInsets();
		Rectangle clip = g.getClipBounds();
		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		g.setColor(color1);
		g.fillRect(0, 0, 40, getSize().height);
		g.setColor(color2);

		g.setFont(_font);
		if (clip.x < insets.left) {
			FontMetrics fm = g.getFontMetrics();
			int fontHeight = fm.getHeight();
			int y = fm.getAscent() + insets.top;
			int startingLineNumber = ((clip.y + insets.top) / fontHeight) + 1;
			if (startingLineNumber != 1)
				if (y < clip.y) {
					y = startingLineNumber * fontHeight;
				}
			int yend = y + clip.height + fontHeight;
			g.setColor(LColor.black);
			int length = ("" + getLineCount()).length();
			while (y < yend) {
				String label = padLabel(startingLineNumber, length, true);
				g.drawString(label, insets.left - fm.stringWidth(label) - 10, y);
				y += fontHeight;
				startingLineNumber++;
			}
		}
		super.paintComponent(g);
	}

	private String padLabel(int lineNumber, int length, boolean addSpace) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(lineNumber);
		for (int count = (length - buffer.length()); count > 0; count--) {
			buffer.insert(0, ' ');
		}
		if (addSpace) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

	private int getLineCount() {
		String text = this.getText();
		int charCt = text.length();
		int lineCt = 1;
		for (int i = 0; i < charCt; i++) {
			if (text.charAt(i) == '\n') {
				lineCt++;
			}
		}
		return Math.max(lineCt, 10);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		Component parent = getParent();
		ComponentUI ui = getUI();
		return parent != null ? (ui.getPreferredSize(this).width <= parent
				.getSize().width) : true;
	}

}
