package org.ripple.power.ui.view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.ripple.power.utils.GraphicsUtils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class BackgroundPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int SCALED = 0;

	public static final int TILED = 1;

	public static final int ACTUAL = 2;

	private Optional<Paint> painter = Optional.absent();
	private Optional<Image> image = Optional.absent();

	private int style = ACTUAL;

	private float alignmentX = 0.5f;
	private float alignmentY = 0.55f;

	private float alpha = 1.0f;

	private Composite originalComposite;

	public BackgroundPanel(Image image) {
		this(image, SCALED);
	}

	public BackgroundPanel(Image image, int style) {

		this(image, style, 0.5f, 0.5f);

	}

	public BackgroundPanel(Image image, int style, float alignmentX,
			float alignmentY) {

		setImage(image);
		setStyle(style);

		setImageAlignmentX(alignmentX);
		setImageAlignmentY(alignmentY);

		setLayout(new BorderLayout());

		this.applyComponentOrientation(this.getComponentOrientation());

	}

	public BackgroundPanel(Paint painter) {

		setPaint(painter);
		setLayout(new BorderLayout());

	}

	
	@Override
	public Dimension getPreferredSize() {

		if (image.isPresent()) {
			return new Dimension(image.get().getWidth(null), image.get()
					.getHeight(null));
		} else {
			return super.getPreferredSize();
		}

	}

	private void drawScaled(Graphics g) {

		Preconditions.checkState(image.isPresent(), "'image' must be present");

		Graphics2D g2 = (Graphics2D) g;

		Dimension d = getSize();

		applyAlphaComposite(g2);

		g2.drawImage(image.get(), 0, 0, d.width, d.height, null);

		removeAlphaComposite(g2);
	}

	private void drawTiled(Graphics g) {

		Preconditions.checkState(image.isPresent(), "'image' must be present");

		Graphics2D g2 = (Graphics2D) g;

		Dimension d = getSize();

		int width = image.get().getWidth(null);
		int height = image.get().getHeight(null);

		for (int x = 0; x < d.width; x += width) {
			for (int y = 0; y < d.height; y += height) {

				applyAlphaComposite(g2);

				g2.drawImage(image.get(), x, y, null, null);

				removeAlphaComposite(g2);
			}
		}

	}

	private void drawActual(Graphics g) {

		Preconditions.checkState(image.isPresent(), "'image' must be present");

		Graphics2D g2 = (Graphics2D) g;

		Dimension d = getSize();
		Insets insets = getInsets();

		int width = d.width - insets.left - insets.right;
		int height = d.height - insets.top - insets.left;

		float x;
		if (this.getComponentOrientation().isLeftToRight()) {
			x = (width - image.get().getWidth(null)) * alignmentX;
		} else {
			x = (width - image.get().getWidth(null)) * (1 - alignmentX);
		}
		float y = (height - image.get().getHeight(null)) * alignmentY;

		applyAlphaComposite(g2);

		g2.drawImage(image.get(), (int) x + insets.left, (int) y + insets.top,
				this);

		removeAlphaComposite(g2);
	}

	private void applyAlphaComposite(Graphics2D g2) {

		originalComposite = g2.getComposite();

		int rule = AlphaComposite.SRC_OVER;
		Composite alphaComposite = AlphaComposite.getInstance(rule, alpha);
		g2.setComposite(alphaComposite);

	}

	private void removeAlphaComposite(Graphics2D g2) {
		g2.setComposite(originalComposite);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		if (painter.isPresent()) {

			Dimension d = getSize();
			Graphics2D g2 = (Graphics2D) g;

			GraphicsUtils.setExcellentRenderingHints(g2);

			g2.setPaint(painter.get());
			g2.fill(new Rectangle(0, 0, d.width, d.height));

		}

		if (image == null) {
			return;
		}

		switch (style) {
		case SCALED:
			drawScaled(g);
			break;

		case TILED:
			drawTiled(g);
			break;

		case ACTUAL:
			drawActual(g);
			break;

		default:
			drawActual(g);
		}
	}

	public void setImage(Image image) {
		this.image = Optional.of(image);
		repaint();
	}

	public void setStyle(int style) {

		this.style = style;

		repaint();
	}

	public void setPaint(Paint painter) {

		this.painter = Optional.of(painter);

		repaint();
	}

	public void setImageAlignmentX(float alignmentX) {

		this.alignmentX = alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f
				: alignmentX;

		repaint();
	}

	public void setImageAlignmentY(float alignmentY) {

		this.alignmentY = alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f
				: alignmentY;

		repaint();
	}

	public void setAlpha(float alpha) {

		this.alpha = alpha > 1.0f ? 1.0f : alpha < 0.0f ? 0.0f : alpha;

		repaint();
	}

}