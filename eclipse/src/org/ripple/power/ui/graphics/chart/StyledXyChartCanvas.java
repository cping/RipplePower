package org.ripple.power.ui.graphics.chart;

import java.util.ArrayList;

public class StyledXyChartCanvas extends ChartBaseCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<StyledChartPointSerie> mSeries = new ArrayList<StyledChartPointSerie>();

	private Paint mLinePnt = new Paint();
	private Paint mFillPnt = new Paint();
	private Paint mMarkPnt = new Paint();

	public StyledXyChartCanvas(int w, int h) {
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


		cnv.drawBitmap(mBmp, 0, 0);
	}

	public void clearSeries() {
		while (mSeries.size() > 0) {
			mSeries.remove(0);
		}
		bRedraw = true;
		
	}

	public void addSerie(StyledChartPointSerie serie) {
		mSeries.add(serie);
		bRedraw = true;
		
	}

	public ArrayList<StyledChartPointSerie> getSeries() {
		return mSeries;
	}

	public void setLineVis(int index, boolean show) {
		mSeries.get(index).setVisible(show);
		bRedraw = true;
		
	}

	public void setLineStyle(int index, float size) {
		mSeries.get(index).setStyle(size);
		bRedraw = true;
		
	}

	public void setLineStyle(int index, float size, boolean usedip) {
		mSeries.get(index).setStyle(size, usedip);
		bRedraw = true;
		
	}

	protected void getXYminmax() {
		StyledChartPointSerie serie;
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
		float qX = 0, qY = 0, pX = 0, pY = 0;
		boolean pValid;
		StyledChartPoint point;

		mLinePnt.reset();
		mFillPnt.reset();
		mMarkPnt.reset();
		mLinePnt.setStyle(Style.STROKE);
		mFillPnt.setStyle(Style.FILL);
		mMarkPnt.setStyle(Style.FILL);
		mLinePnt.setAntiAlias(true);
		mMarkPnt.setAntiAlias(false);
		mFillPnt.setAntiAlias(false);
		for (StyledChartPointSerie serie : mSeries) {
			if (serie.isVisible()) {
		
				if (serie.mUseDip)
					mLinePnt.setStrokeWidth(dipToPixel(serie.mWidth));
				else
					mLinePnt.setStrokeWidth(serie.mWidth);
				
				pValid = false;
				for (int ii = 0; ii < serie.mPointList.size(); ii++) {
					point = serie.mPointList.get(ii);
					pX = point.x;
					pY = point.y;
					if (Float.isNaN(pX) || Float.isNaN(pY)) {
						pValid = false;
					} else if (!pValid) {
	
						if (point.markColor != 0) {
							mMarkPnt.setColor(point.markColor);
							mCnv.drawCircle(sX + (pX - bX) * aX, eY - (pY - bY)
									* aY, point.markSize, mMarkPnt);
						}
						pValid = true;
					} else {
				
						if (point.fillColor != 0) {
							mFillPnt.setColor(point.fillColor);
							mPath.reset();
							mPath.moveTo(sX + (qX - bX) * aX, eY);
							mPath.lineTo(sX + (qX - bX) * aX, eY - (qY - bY)
									* aY);
							mPath.lineTo(sX + (pX - bX) * aX, eY - (pY - bY)
									* aY);
							mPath.lineTo(sX + (pX - bX) * aX, eY);
							mPath.close();
							mCnv.drawPath(mPath, mFillPnt);
						}

						mLinePnt.setColor(point.lineColor);
						mCnv.drawLine(sX + (qX - bX) * aX, eY - (qY - bY) * aY,
								sX + (pX - bX) * aX, eY - (pY - bY) * aY,
								mLinePnt);
						if (point.markColor != 0) {
							mMarkPnt.setColor(point.markColor);
							mCnv.drawCircle(sX + (pX - bX) * aX, eY - (pY - bY)
									* aY, point.markSize, mMarkPnt);
						}
					}
					qX = pX;
					qY = pY;
				}
			}
		}
	}

}
