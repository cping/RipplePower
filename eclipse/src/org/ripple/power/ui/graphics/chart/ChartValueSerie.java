package org.ripple.power.ui.graphics.chart;

import java.util.ArrayList;

import org.ripple.power.ui.graphics.LColor;

public class ChartValueSerie {

	public ArrayList<ChartValue> mPointList = new ArrayList<ChartValue>();

	public int mColor = LColor.red.getARGB();
	public int mFillColor = LColor.darkGray.getARGB();
	public float mWidth = 2;
	public boolean mUseDip = true;

	public float mYmin = 0, mYmax = 1;

	private boolean mShow = true;
	private boolean mAutoYminmax = true;

	public ChartValueSerie() {
	}

	public ChartValueSerie(int color) {
		mColor = color;
	}

	public ChartValueSerie(int color, float width) {
		mColor = color;
		mWidth = width;
	}

	public ChartValueSerie(int color, float width, boolean usedip) {
		mColor = color;
		mWidth = width;
		mUseDip = usedip;
	}

	public ArrayList<ChartValue> getPointList() {
		return mPointList;
	}

	public void setPointList(ArrayList<ChartValue> points) {
		this.mPointList = points;
	}

	public void clearPointList() {
		this.mPointList.clear();
	}

	public void addPoint(ChartValue point) {
		if (mAutoYminmax) {
			if (mPointList.size() > 0) {
				if (point.y > mYmax){
					mYmax = point.y;
				}
				else if (point.y < mYmin){
					mYmin = point.y;
				}
			} else
				mYmin = mYmax = point.y;
		}
		mPointList.add(point);
	}

	public void shiftPoint(ChartValue point, int max) {
		addPoint(point);
		while (mPointList.size() > max){
			mPointList.remove(0);
		}
		if (mAutoYminmax){
			calcRanges();
		}
	}

	public void removePoint(ChartValue point) {
		mPointList.remove(point);
		if (mAutoYminmax)
			calcRanges();
	}

	public ChartValue getPoint(int index) {
		return mPointList.get(index);
	}

	public void updatePoint(int index, float y) {
		mPointList.get(index).y = y;
		if (mAutoYminmax)
			calcRanges();
	}

	public int getSize() {
		return mPointList.size();
	}

	private void calcRanges() {
		int i;
		if (mPointList.size() == 0){
			return;
		}
		if (mAutoYminmax) {
			mYmin = mPointList.get(0).y;
			mYmax = mPointList.get(0).y;
			for (i = 1; i < mPointList.size(); i++) {
				if (mPointList.get(i).y > mYmax){
					mYmax = mPointList.get(i).y;
				}
				else if (mPointList.get(i).y < mYmin){
					mYmin = mPointList.get(i).y;
				}
			}
		}
	}

	public void setAutoMinmax(boolean bAutoY) {
		this.mAutoYminmax = bAutoY;
		if (bAutoY){
			calcRanges();
		}
	}

	public void setAutoMinmax(boolean bAutoY, float fYmin, float fYmax) {
		this.mAutoYminmax = bAutoY;
		if (!bAutoY) {
			this.mYmin = fYmin;
			this.mYmax = fYmax;
		}
		if (bAutoY){
			calcRanges();
		}
	}

	public void setVisible(boolean bShow) {
		this.mShow = bShow;
	}

	public boolean isVisible() {
		return this.mShow;
	}

	public void setStyle(int iColor, float fWidth) {
		mColor = iColor;
		mWidth = fWidth;
	}

	public void setStyle(int iColor, float fWidth, boolean bUsedip) {
		mColor = iColor;
		mWidth = fWidth;
		mUseDip = bUsedip;
	}

	public void setStyle(int iColor, int iFillColor, float fWidth,
			boolean bUsedip) {
		mColor = iColor;
		mFillColor = iFillColor;
		mWidth = fWidth;
		mUseDip = bUsedip;
	}

}
