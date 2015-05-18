package org.ripple.power.ui.editor;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.ripple.power.ui.UIRes;
import org.ripple.power.ui.graphics.LColor;

public class SourceDocument extends DefaultStyledDocument {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SimpleAttributeSet keywordStyle = new SimpleAttributeSet();

	private SimpleAttributeSet commentStyle = new SimpleAttributeSet();

	private SimpleAttributeSet stringStyle = new SimpleAttributeSet();

	private SimpleAttributeSet defaultStyle = new SimpleAttributeSet();

	private Vector<String> keywords = new Vector<String>();

	private Pattern keyReg = Pattern.compile("");

	private Pattern doubleStringReg = Pattern.compile("\"[^\n\"]*\"");

	private Pattern singleStringReg = Pattern.compile("'[^\n']*'");

	private Pattern stringComments = Pattern
			.compile("(#[^\n]*|\"([^\n\"\\x5c]|(\\x5c\")|(\\x5c))*\"|'([^\n'\\x5c]|(\\x5c')|(\\x5c))*')");

	public SourceDocument() {
		super();

		SimpleAttributeSet commentStyle = new SimpleAttributeSet();
		commentStyle.addAttribute(StyleConstants.Foreground,
				LColor.cornFlowerBlue);
		commentStyle.addAttribute(StyleConstants.Italic, Boolean.TRUE);
		this.setCommentStyle(commentStyle);

		SimpleAttributeSet keywordStyle = new SimpleAttributeSet();
		keywordStyle.addAttribute(StyleConstants.Bold, Boolean.TRUE);
		keywordStyle.addAttribute(StyleConstants.Foreground, LColor.orange);
		this.setKeywordStyle(keywordStyle);

		SimpleAttributeSet stringStyle = new SimpleAttributeSet();
		stringStyle.addAttribute(StyleConstants.Foreground,
				LColor.mediumAquamarine);
		this.setStringStyle(stringStyle);

		Vector<String> rocKeywords = new Vector<String>();
		Scanner scaner = null;
		try {
			scaner = new Scanner(UIRes.getStream("config/roc"));
			while (scaner.hasNextLine()) {
				String line = scaner.nextLine().trim();
				if (line.length() > 0) {
					rocKeywords.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] keys = new String[rocKeywords.size()];
		rocKeywords.copyInto(keys);
		this.setKeywords(keys);
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		super.insertString(offs, str, a);
		updateHighlightingInRange(offs, str.length());
	}

	@Override
	protected void fireRemoveUpdate(DocumentEvent e) {
		int offset = e.getOffset();
		updateHighlightingInRange(offset - 1, 0);
		super.fireRemoveUpdate(e);
	}

	public void updateHighlightingInRange(int offset, int length) {
		try {

			Element defaultElement = getDefaultRootElement();
			int line = defaultElement.getElementIndex(offset);
			int lineend = defaultElement.getElementIndex(offset + length);
			int start = defaultElement.getElement(line).getStartOffset();
			int end = defaultElement.getElement(lineend).getEndOffset();

			String text = getText(start, end - start);
			setCharacterAttributes(start, end - start, defaultStyle, true);

			Matcher m = keyReg.matcher(text);
			while (m.find()) {
				setCharacterAttributes(start + m.start(), m.end() - m.start(),
						keywordStyle, true);
			}

			m = stringComments.matcher(text);
			while (m.find()) {
				if (text.charAt(m.start()) == '#') {
					setCharacterAttributes(start + m.start(),
							m.end() - m.start(), commentStyle, true);
				}
			}

			m = doubleStringReg.matcher(text);
			while (m.find()) {
				if (text.charAt(m.start()) == '\''
						|| text.charAt(m.start()) == '"') {
					setCharacterAttributes(start + m.start(),
							m.end() - m.start(), stringStyle, true);
				}
			}

			m = singleStringReg.matcher(text);
			while (m.find()) {
				if (text.charAt(m.start()) == '\''
						|| text.charAt(m.start()) == '"') {
					setCharacterAttributes(start + m.start(),
							m.end() - m.start(), stringStyle, true);
				}
			}
		} catch (Exception e) {
		}
	}

	public void setKeywords(String[] words) {
		keywords.clear();
		for (int i = 0; i < words.length; i++) {
			keywords.add(words[i]);
		}
		compileKeywords();
	}

	public void addKeyword(String word) {
		keywords.add(word);
		compileKeywords();
	}

	public void setKeywordStyle(SimpleAttributeSet style) {
		keywordStyle = style;
	}

	public void setCommentStyle(SimpleAttributeSet style) {
		commentStyle = style;
	}

	public void setStringStyle(SimpleAttributeSet style) {
		stringStyle = style;
	}

	public void setDefaultStyle(SimpleAttributeSet style) {
		defaultStyle = style;
	}

	private void compileKeywords() {
		String exp = new String();
		exp = "\\b(";
		for (int i = 0; i < keywords.size(); i++) {
			if (i == 0) {
				exp = exp + (keywords.elementAt(i)).trim();
			}
			exp = exp + "|" + (keywords.elementAt(i)).trim();
		}
		exp = exp + ")\\b";
		keyReg = Pattern.compile(exp);
	}

}
