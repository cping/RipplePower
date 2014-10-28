package org.ripple.power.ui.graphics.chart;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.LGraphics;
import org.ripple.power.ui.graphics.LImage;

class Canvas {
	private static final String UNKNOWN_STYLE = "unknown style: ";

	private LImage _bufferedImage;
	private LGraphics _graphics2D;

	Canvas() {

	}

	Canvas(LGraphics g) {
		this._graphics2D = g;
		enableAntiAliasing();
		_graphics2D.save();
	}
	
	public void restore(){
		_graphics2D.restore();
	}
	
	public void save(){
		_graphics2D.save();
	}
	
	public void drawBitmap(Bitmap bitmap, int left, int top) {
		this._graphics2D.drawImage(
				JavaSEGraphicFactory.getBufferedImage(bitmap), left, top);
	}

	public void drawBitmap(Bitmap bitmap, Matrix matrix) {
		this._graphics2D.drawRenderedImage(
				JavaSEGraphicFactory.getBufferedImage(bitmap).getBufferedImage(),
				JavaSEGraphicFactory.getAffineTransform(matrix));
	}

	public void drawCircle(int x, int y, int radius, Paint paint) {
		if (paint.isTransparent()) {
			return;
		}

		Paint awtPaint = JavaSEGraphicFactory.getAwtPaint(paint);
		setColorAndStroke(awtPaint);
		int doubleRadius = radius * 2;

		Style style = awtPaint.style;
		switch (style) {
		case FILL:
			this._graphics2D.fillOval(x - radius, y - radius, doubleRadius,
					doubleRadius);
			return;

		case STROKE:
			this._graphics2D.drawOval(x - radius, y - radius, doubleRadius,
					doubleRadius);
			return;
		}

		throw new IllegalArgumentException(UNKNOWN_STYLE + style);
	}

	public void drawLine(int x1, int y1, int x2, int y2, Paint paint) {
		if (paint.isTransparent()) {
			return;
		}

		setColorAndStroke(JavaSEGraphicFactory.getAwtPaint(paint));
		this._graphics2D.drawLine(x1, y1, x2, y2);
	}

	public void drawPath(Path path, Paint paint) {
		if (paint.isTransparent()) {
			return;
		}

		Paint awtPaint = JavaSEGraphicFactory.getAwtPaint(paint);
		Path awtPath = JavaSEGraphicFactory.getAwtPath(path);

		setColorAndStroke(awtPaint);
		this._graphics2D.setPaint(awtPaint.texturePaint);

		Style style = awtPaint.style;
		switch (style) {
		case FILL:
			this._graphics2D.fill(awtPath.path2D);
			return;

		case STROKE:
			this._graphics2D.draw(awtPath.path2D);
			return;
		}

		throw new IllegalArgumentException(UNKNOWN_STYLE + style);
	}

	public void drawPointTextContainer(PointTextContainer ptc, int maxWidth) {
		if (ptc.paintFront.isTransparent()
				&& (ptc.paintBack == null || ptc.paintBack.isTransparent())) {
			return;
		}
		int textWidth = ptc.paintFront.getTextWidth(ptc.text);
		if (textWidth > maxWidth) {
			AttributedString attrString = new AttributedString(ptc.text);
			Paint awtPaintFront = JavaSEGraphicFactory
					.getAwtPaint(ptc.paintFront);
			attrString.addAttribute(TextAttribute.FOREGROUND,
					awtPaintFront.color);
			attrString.addAttribute(TextAttribute.FONT, awtPaintFront.font);
			AttributedCharacterIterator paragraph = attrString.getIterator();
			int paragraphStart = paragraph.getBeginIndex();
			int paragraphEnd = paragraph.getEndIndex();
			FontRenderContext frc = this._graphics2D.getFontRenderContext();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph,
					frc);
			float layoutHeight = 0;
			lineMeasurer.setPosition(paragraphStart);
			while (lineMeasurer.getPosition() < paragraphEnd) {
				TextLayout layout = lineMeasurer.nextLayout(maxWidth);
				layoutHeight += layout.getAscent() + layout.getDescent()
						+ layout.getLeading();
			}
			float drawPosY = (float) ptc.y;
			lineMeasurer.setPosition(paragraphStart);
			while (lineMeasurer.getPosition() < paragraphEnd) {
				TextLayout layout = lineMeasurer.nextLayout(maxWidth);
				float posX = (float) ptc.x;
				float posY = drawPosY;
				if (Position.CENTER == ptc.position) {
					posX += (maxWidth - layout.getAdvance()) * 0.5f;
					posY += (layout.getAscent() + layout.getDescent()
							+ layout.getLeading() - layoutHeight) * 0.5f;
				} else if (Position.BELOW == ptc.position) {
					posX += (maxWidth - layout.getAdvance()) * 0.5f;
				} else if (Position.ABOVE == ptc.position) {
					posX += (maxWidth - layout.getAdvance()) * 0.5f;
					posY += layout.getAscent() + layout.getDescent()
							+ layout.getLeading() - layoutHeight;
				} else if (Position.LEFT == ptc.position) {
					posX += textWidth * 0.5f - maxWidth * 0.5f + maxWidth
							- layout.getAdvance();
					posY += (layout.getAscent() + layout.getDescent()
							+ layout.getLeading() - layoutHeight) * 0.5f;
				} else if (Position.RIGHT == ptc.position) {
					posX += -textWidth * 0.5f + maxWidth * 0.5f;
					posY += (layout.getAscent() + layout.getDescent()
							+ layout.getLeading() - layoutHeight) * 0.5f;
				} else {
					throw new IllegalArgumentException(
							"No position for drawing PointTextContainer");
				}
				if (ptc.paintBack != null) {
					setColorAndStroke(JavaSEGraphicFactory
							.getAwtPaint(ptc.paintBack));
					AffineTransform affineTransform = new AffineTransform();
					affineTransform.translate(posX, posY);
					this._graphics2D.draw(layout.getOutline(affineTransform));
				}
				layout.draw(this._graphics2D, posX, posY);
				drawPosY += layout.getAscent() + layout.getDescent()
						+ layout.getLeading();
			}
		} else {
			if (ptc.paintBack != null) {
				drawText(ptc.text, (int) ptc.x, (int) ptc.y, ptc.paintBack);
			}
			drawText(ptc.text, (int) ptc.x, (int) ptc.y, ptc.paintFront);
		}
	}

	public void drawText(String text, int x, int y, Paint paint) {
		if (paint.isTransparent()) {
			return;
		}

		Paint awtPaint = JavaSEGraphicFactory.getAwtPaint(paint);

		if (awtPaint.stroke == null) {
			this._graphics2D.setColor(awtPaint.color);
			this._graphics2D.setFont(awtPaint.font);
			this._graphics2D.drawString(text, x, y);
		} else {
			setColorAndStroke(awtPaint);
			TextLayout textLayout = new TextLayout(text, awtPaint.font,
					this._graphics2D.getFontRenderContext());
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.translate(x, y);
			this._graphics2D.draw(textLayout.getOutline(affineTransform));
		}
	}

	public void drawTextRotated(String text, int x1, int y1, int x2, int y2,
			Paint paint) {
		if (paint.isTransparent()) {
			return;
		}

		AffineTransform affineTransform = this._graphics2D.getTransform();

		double theta = Math.atan2(y2 - y1, x2 - x1);
		this._graphics2D.rotate(theta, x1, y1);

		double lineLength = Math.hypot(x2 - x1, y2 - y1);
		int textWidth = paint.getTextWidth(text);
		int dx = (int) (lineLength - textWidth) / 2;
		int xy = paint.getTextHeight(text) / 3;
		drawText(text, x1 + dx, y1 + xy, paint);

		this._graphics2D.setTransform(affineTransform);
	}

	public void fillColor(LColor color) {
		fillColor(color.getARGB());
	}

	public void fillColor(int color) {
		fillColor(new java.awt.Color(color));
	}

	public Dimension getDimension() {
		return new Dimension(getWidth(), getHeight());
	}

	public int getHeight() {
		return this._bufferedImage != null ? this._bufferedImage.getHeight() : 0;
	}

	public int getWidth() {
		return this._bufferedImage != null ? this._bufferedImage.getWidth() : 0;
	}

	public void resetClip() {
		this._graphics2D.setClip(null);
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			this._bufferedImage = null;
			this._graphics2D = null;
		} else {
			this._bufferedImage = JavaSEGraphicFactory.getBufferedImage(bitmap);
			this._graphics2D = this._bufferedImage.getLGraphics();
			enableAntiAliasing();
			this._graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			this._graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
		}
	}

	public void setClip(int left, int top, int width, int height) {
		this._graphics2D.setClip(left, top, width, height);
	}

	private void enableAntiAliasing() {
		this._graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private void fillColor(java.awt.Color color) {
		this._graphics2D.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC));
		this._graphics2D.setColor(color);
		this._graphics2D.fillRect(0, 0, getWidth(), getHeight());
	}

	private void setColorAndStroke(Paint awtPaint) {
		this._graphics2D.setColor(awtPaint.color);
		if (awtPaint.stroke != null) {
			this._graphics2D.setStroke(awtPaint.stroke);
		}
	}
}
