package org.ripple.power.ui.graphics.chart;

import java.util.ArrayList;

public class XyChartView extends ChartBaseCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<ChartPointSerie> mSeries = new ArrayList<ChartPointSerie>();

	private Paint mPnt = new Paint();

	public XyChartView(int w, int h) {
		super(w, h);
	}

	public void onDraw(Canvas cnv) {

		if ((mBmp == null) || (bRedraw)) {

			getViewSizes();

			getXYminmax();

			if (p_xscale_auto)
				calcXgridRange();
			if (p_yscale_auto)
				calcYgridRange();

			calcXYcoefs();

			reset();

			if (p_grid_vis)
				drawGrid();

			if (p_xtext_vis)
				drawXlabel();

			if (p_ytext_vis)
				drawYlabel();

			if (p_border_vis)
				drawBorder();

			if (p_axis_vis)
				drawAxis();

			drawData();

			bRedraw = false;
		}

		cnv.drawBitmap(mBmp, 0, 0);
	}

	public void clearSeries() {
		while (mSeries.size() > 0) {
			mSeries.remove(0);
		}
		bRedraw = true;

	}

	public void addSerie(ChartPointSerie serie) {
		mSeries.add(serie);
		bRedraw = true;

	}

	public ArrayList<ChartPointSerie> getSeries() {
		return mSeries;
	}

	public void setLineVis(int index, boolean show) {
		mSeries.get(index).setVisible(show);
		bRedraw = true;

	}

	public void setLineStyle(int index, int color, float size) {
		mSeries.get(index).setStyle(color, size);
		bRedraw = true;

	}

	public void setLineStyle(int index, int color, float size, boolean usedip) {
		mSeries.get(index).setStyle(color, size, usedip);
		bRedraw = true;

	}

	protected void getXYminmax() {
		ChartPointSerie serie;
		for (int ii = 0; ii < mSeries.size(); ii++) {
			serie = mSeries.get(ii);
			if (ii == 0) {
				mXmin = serie.mXmin;
				mXmax = serie.mXmax;
				mYmin = serie.mYmin;
				mYmax = serie.mYmax;
			} else {
				if (serie.mXmin < mXmin)
					mXmin = serie.mXmin;
				if (serie.mXmax > mXmax)
					mXmax = serie.mXmax;
				if (serie.mYmin < mYmin)
					mYmin = serie.mYmin;
				if (serie.mYmax > mYmax)
					mYmax = serie.mYmax;
			}
		}
	}

	protected void drawData() {
		float pX, pY;
		boolean pValid;
		ChartPoint point;
		for (ChartPointSerie serie : mSeries) {
			if (serie.isVisible()) {
				mPnt.reset();
				mPnt.setStyle(Style.STROKE);
				mPnt.setColor(serie.mColor);
				if (serie.mUseDip)
					mPnt.setStrokeWidth(dipToPixel(serie.mWidth));
				else
					mPnt.setStrokeWidth(serie.mWidth);
				mPnt.setAntiAlias(true);
				pValid = false;
				mPath.reset();
				for (int ii = 0; ii < serie.mPointList.size(); ii++) {
					point = serie.mPointList.get(ii);
					pX = point.x;
					pY = point.y;
					if (Float.isNaN(pX) || Float.isNaN(pY)) {
						pValid = false;
					} else if (!pValid) {
						mPath.moveTo(sX + (pX - bX) * aX, eY - (pY - bY) * aY);
						pValid = true;
					} else {
						mPath.lineTo(sX + (pX - bX) * aX, eY - (pY - bY) * aY);
					}
				}
				mCnv.drawPath(mPath, mPnt);
			}
		}
	}

}
