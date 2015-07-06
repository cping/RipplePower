package org.ripple.power.ui.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bootstrap.ui.CalloutBorder;
import org.ripple.power.ui.UIConfig;

import net.miginfocom.swing.MigLayout;

public class Callout extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int INFO = 0;
	public static final int DANGER = 1;
	public static final int WARNING = 2;

	private static final Color INFO_FG = Color.decode("#F4F8FA");
	private static final Color INFO_BG = Color.decode("#5BC0DE");
	private static final Color WARNING_FG = Color.decode("#D9534F");
	private static final Color WARNING_BG = Color.decode("#FDF7F7");
	private static final Color DANGER_FG = Color.decode("#F0AD4E");
	private static final Color DANGER_BG = Color.decode("#FCF8F2");

	private int style = INFO;
	private String title = null;
	private String text = null;

	private JLabel titleLabel;
	private JLabel textLabel;

	private Font titleFont = null;
	private Font textFont = null;

	private CalloutBorder border;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;

		Color fg = INFO_FG;
		Color bg = INFO_BG;

		switch (style) {
		case INFO: {
			fg = INFO_FG;
			bg = INFO_BG;
		}
		case DANGER: {
			fg = DANGER_FG;
			bg = DANGER_BG;
		}
		case WARNING: {
			fg = WARNING_FG;
			bg = WARNING_BG;
		}
		default:
			break;
		}

		border.setBorderColor(fg);
		titleLabel.setForeground(fg);
		setBackground(bg);
	}

	public Callout(String title, String text, int style) {
		super();
		setLayout(new MigLayout("gap 0, ins 0", "20[fill]20", "20[80]5[fill]20"));

		this.title = title;
		this.text = text;

		this.border = new CalloutBorder(INFO_FG, 4);
		setBorder(border);

		titleFont = UIConfig.getBasicFont().deriveFont(20f);
		textFont = UIConfig.getBasicFont().deriveFont(14f);

		this.titleLabel = new JLabel(title);
		this.titleLabel.setFont(titleFont);
		this.titleLabel.setForeground(border.getBorderColor());
		add(this.titleLabel, "cell 0 0 1 1");

		this.textLabel = new JLabel(text);
		this.textLabel.setFont(textFont);
		this.textLabel.setForeground(getForeground());
		add(this.textLabel, "cell 0 1 1 1");

		setStyle(style);

	}
}
