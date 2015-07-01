package org.ripple.power.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Timer;

import org.ripple.power.utils.GraphicsUtils;

public class AnimationIcon implements Icon {

	private boolean fix;
	private final Icon delegateIcon;
	private final int width;
	private final int height;

	private final int[][] rotationOffsets = new int[][] { { 0, 0 }, { 0, 0 },
			{ 0, 0 }, { 0, 1 }, { 0, 1 }, { 0, 1 }, { 0, 1 }, { -1, 1 },
			{ -1, 1 }, { -1, 1 }, { -1, 1 }, { -1, 0 }, { -1, 0 }, { -1, 0 },
			{ -1, 0 }, { 0, 0 }, };

	private final static int maxStepCount = 16;
	private double theta = 0;
	private double delta = 2 * Math.PI / maxStepCount;
	private int stepCount = 0;
	private final Timer timer;

	public AnimationIcon(Icon icon, final JComponent component,
			final boolean repeat) {

		delegateIcon = icon;

		width = delegateIcon.getIconWidth();
		height = delegateIcon.getIconHeight();

		timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				incrementRotation(component);
			}
		});

		component.repaint();

		timer.setRepeats(repeat);
		timer.start();

	}

	public void incrementRotation(final JComponent component) {
		theta += delta;
		stepCount++;
		if (stepCount >= maxStepCount) {
			theta = 0;
			stepCount = 0;

		}
		component.repaint();
	}

	public void decrementRotation(JComponent component) {
		theta -= delta;
		if (stepCount == 0) {
			theta = 2 * Math.PI - delta;
			stepCount = maxStepCount - 1;
		} else {
			stepCount--;
		}
		component.repaint();
	}

	public void fixRotation() {
		this.fix = true;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
		GraphicsUtils.setExcellentRenderingHints(g2d);

		int xCenteringOffset = 0;
		int yCenteringOffset = 0;

		int xRotationOffset = fix ? rotationOffsets[stepCount][0] : 0;
		int yRotationOffset = fix ? rotationOffsets[stepCount][1] : 0;

		double centerX = xRotationOffset + xCenteringOffset + x + (width / 2);
		double centerY = yRotationOffset + yCenteringOffset + y + (height / 2);

		AffineTransform tx = new AffineTransform();
		tx.rotate(theta, centerX, centerY);

		g2d.setTransform(tx);
		delegateIcon.paintIcon(c, g2d, xRotationOffset + xCenteringOffset + x,
				yRotationOffset + yCenteringOffset + y);
		g2d.dispose();
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}
}