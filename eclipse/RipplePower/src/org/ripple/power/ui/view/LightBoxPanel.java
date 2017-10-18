package org.ripple.power.ui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.ripple.power.utils.GraphicsUtils;

import com.google.common.base.Preconditions;

public class LightBoxPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel screenPanel;

	public LightBoxPanel(JPanel screenPanel, Integer layer) {

		Preconditions.checkNotNull(screenPanel, "'panel' must be present");
		Preconditions.checkState(screenPanel.getWidth() > 0, "'width' must be greater than zero");
		Preconditions.checkState(screenPanel.getHeight() > 0, "'height' must be greater than zero");

		this.screenPanel = screenPanel;

		setOpaque(false);

		setVisible(true);

		addMouseListener(new ModalMouseListener());

		if (JLayeredPane.MODAL_LAYER.equals(layer)) {
			Panels.getApplication().getLayeredPane().add(this, JLayeredPane.PALETTE_LAYER);
		} else {
			Panels.getApplication().getLayeredPane().add(this, JLayeredPane.POPUP_LAYER);
		}

		calculatePosition();

		Panels.getApplication().getLayeredPane().add(screenPanel, layer);

	}

	private void calculatePosition() {

		int currentFrameWidth = Panels.getApplication().getWidth();
		int currentFrameHeight = Panels.getApplication().getHeight();

		setSize(currentFrameWidth * 2, currentFrameHeight * 2);

		int x = (currentFrameWidth - screenPanel.getWidth()) / 2;
		int y = ((currentFrameHeight - screenPanel.getHeight()) / 2) - 10;

		x = x < 0 ? 0 : x;
		y = y < 0 ? 0 : y;

		screenPanel.setLocation(x, y);
	}

	public void close() {

		Preconditions.checkState(SwingUtilities.isEventDispatchThread(), "Must be on the EDT");
		JLayeredPane layeredPane = Panels.getApplication().getLayeredPane();

		Component[] components = layeredPane.getComponents();

		if (components.length == 4 || components.length == 6) {
			layeredPane.remove(0);
		}

		if (components.length > 2 && components.length < 7) {
			layeredPane.remove(1);
			layeredPane.remove(0);
		}
		Panels.getApplication().validate();
		Panels.getApplication().repaint();

	}

	@Override
	protected void paintComponent(Graphics graphics) {

		calculatePosition();

		if (graphics instanceof Graphics2D) {
			Graphics2D g = (Graphics2D) graphics;

			g.setPaint(Color.BLACK);

			GraphicsUtils.setAlpha(g, 0.5f);

			g.fillRect(0, 0, Panels.getApplication().getWidth(), Panels.getApplication().getHeight());
		}
	}

	static class ModalMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

}
