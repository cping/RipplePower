package org.ripple.power.ui.graphics.chart;

import java.util.ArrayList;

import org.ripple.power.ui.graphics.geom.RectF;

public class StackedBarChartCanvas extends ChartBaseCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ChartValueSerie> mSeries = new ArrayList<ChartValueSerie>();
	private ChartValueSerie mStacked = new ChartValueSerie();
	private int mXnum = 0;
	private int mLabelMaxNum = 10;

	private Paint mPnt = new Paint();
	private Paint mPntFill = new Paint();

	public StackedBarChartCanvas(int w, int h) {
		super(w, h);

	}

	public void onDraw(Canvas cnv) {

		if ((mBmp == null) || (bRedraw)) {

			getViewSizes();

			getXYminmax();

			if (p_yscale_auto) {
				calcYgridRange();
			}

			calcXYcoefs();

			reset();

			drawData();

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

			bRedraw = false;
		}

		cnv.drawBitmap(mBmp, 0, 0, null);
	}

	public void clearSeries() {
		while (mSeries.size() > 0) {
			mSeries.remove(0);
		}
		bRedraw = true;

	}

	public void addSerie(ChartValueSerie serie) {
		mSeries.add(serie);
		bRedraw = true;

	}

	public ArrayList<ChartValueSerie> getSeries() {
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

	public void setLineStyle(int index, int color, int fillcolor, float size, boolean usedip) {
		mSeries.get(index).setStyle(color, fillcolor, size, usedip);
		bRedraw = true;

	}

	public void setLabelMaxNum(int maxnum) {
		if (maxnum <= 0)
			return;
		mLabelMaxNum = maxnum;
		bRedraw = true;

	}

	protected void getXYminmax() {
		calcStackedSerie();
		mXnum = mStacked.getSize();
		mYmax = mStacked.mYmax;
		ChartValueSerie serie;
		for (int ii = 0; ii < mSeries.size(); ii++) {
			serie = mSeries.get(ii);
			if (ii == 0) {
				mYmin = serie.mYmin;
			} else {
				if (serie.mYmin < mYmin)
					mYmin = serie.mYmin;
			}
		}
	}

	protected void drawData() {

		float pY;
		ChartValueSerie serie;
		ChartValue v;
		for (int ii = 0; ii < mSeries.size(); ii++) {
			serie = mSeries.get(ii);
			if (serie.isVisible()) {
				mPnt.reset();
				mPnt.setStyle(Style.STROKE);
				mPnt.setColor(serie.mColor);
				mPntFill.reset();
				mPntFill.setStyle(Style.FILL);
				mPntFill.setColor(serie.mFillColor);
				if (serie.mUseDip)
					mPnt.setStrokeWidth(4);
				else
					mPnt.setStrokeWidth(serie.mWidth);
				mPnt.setAntiAlias(true);
				mPntFill.setAntiAlias(false);

				for (int jj = 0; jj < mStacked.mPointList.size(); jj++) {
					v = mStacked.mPointList.get(jj);
					pY = v.y;

					if (!Float.isNaN(pY)) {
						RectF rect = new RectF(sX + aX / 4 + jj * aX + 1, eY, sX + aX / 4 + aX / 2 + jj * aX,
								eY - (pY - bY) * aY);
						mCnv.drawRect(rect.left + offsetX, rect.top + offsetY, rect.right + offsetX,
								rect.bottom + offsetY, mPntFill);
						mCnv.drawRect(rect.left + offsetX, rect.top + offsetY, rect.right + offsetX,
								rect.bottom + offsetY, mPnt);
					}
					mStacked.updatePoint(jj, v.y - serie.getPoint(jj).y);
				}
			}
		}
	}

	protected void calcXYcoefs() {
		aX = (float) dX / mXnum;
		bX = (float) aX / 2;
		aY = (float) dY / Math.abs(mYmaxGrid - mYminGrid);
		bY = (float) mYminGrid;
	}

	protected void drawXlabel() {
		mPntText.setTextAlign(Align.CENTER);
		mPath.reset();
		ChartValueSerie mLabel = mSeries.get(0);
		String label;
		int numlab = mLabel.getSize();
		int numdiv = 1 + (numlab - 1) / mLabelMaxNum;
		if (p_xtext_bottom) {
			for (int ii = 0; ii < mLabel.getSize(); ii++) {
				mPath.moveTo(sX + bX + ii * aX, eY - 3);
				mPath.lineTo(sX + bX + ii * aX, eY + 3);
				label = mLabel.mPointList.get(ii).t;
				if ((label != null) && (ii < numlab) && ((ii % numdiv) == 0))
					mCnv.drawText(label, sX + bX + ii * aX, eY + p_text_size + 2, mPntText);
			}
		} else {
			for (int ii = 0; ii < mLabel.getSize(); ii++) {
				mPath.moveTo(sX + bX + ii * aX, sY - 3);
				mPath.lineTo(sX + bX + ii * aX, sY + 3);
				label = mLabel.mPointList.get(ii).t;
				if ((label != null) && (ii < numlab) && ((ii % numdiv) == 0))
					mCnv.drawText(label, sX + bX + ii * aX, sY - p_text_size + 3, mPntText);
			}
		}
		mCnv.drawPath(mPath, mPntAxis);
	}

	protected void calcStackedSerie() {
		if (mSeries.size() == 0)
			return;
		mStacked.clearPointList();
		ChartValueSerie f = mSeries.get(0);
		float acc = 0;
		for (int ii = 0; ii < f.getSize(); ii++) {
			if (f.isVisible())
				acc = f.getPoint(ii).y;
			else
				acc = 0;
			for (int jj = 1; jj < mSeries.size(); jj++) {
				if ((mSeries.get(jj).isVisible()) && (ii < mSeries.get(jj).getSize()))
					acc += mSeries.get(jj).getPoint(ii).y;
			}
			mStacked.addPoint(new ChartValue(null, acc));
		}
	}

}
