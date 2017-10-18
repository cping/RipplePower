package org.ripple.power.print;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.ripple.power.utils.GraphicsUtils;

public class BufferedImagePrintable implements Printable, Pageable {
	private PageFormat pf;

	private BufferedImage pImage;

	public BufferedImagePrintable() {
	}

	public BufferedImagePrintable(PageFormat pf) {
		this.pf = pf;
	}

	public BufferedImagePrintable(String path) {
		pImage = GraphicsUtils.loadBufferedImage(path);
	}

	public BufferedImagePrintable(BufferedImage img) {
		pImage = img;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		if (pImage != null) {
			GraphicsUtils.setAntialiasAll(g, true);
			g.drawImage(pImage, 0, 0, (int) pf.getWidth(), (int) pf.getHeight(), null);
			return Printable.PAGE_EXISTS;
		} else {
			return Printable.NO_SUCH_PAGE;
		}
	}

	@Override
	public int getNumberOfPages() {
		return 1;
	}

	@Override
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
		return pf;
	}

	@Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
		return this;
	}

}