package org.ripple.power.ui;

import java.awt.Component;

import javax.swing.JScrollPane;

import org.bootstrap.ui.ScrollBarUI;

public class RPScrollPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RPScrollPane() {
		this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public RPScrollPane(Component view) {
		this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public RPScrollPane(int vsbPolicy, int hsbPolicy) {
		this(null, vsbPolicy, hsbPolicy);
	}

	public RPScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);

		this.getVerticalScrollBar().setUI(new ScrollBarUI());
		this.getHorizontalScrollBar().setUI(new ScrollBarUI());

		this.getVerticalScrollBar().setUnitIncrement(16);
	}

}
