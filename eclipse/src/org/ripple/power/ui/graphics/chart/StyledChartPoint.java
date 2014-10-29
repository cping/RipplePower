package org.ripple.power.ui.graphics.chart;

public class StyledChartPoint {
	 public float x = 0;
	   public float y = 0;
	   public int lineColor = 0xff000000;
	   public int fillColor = 0x00000000;
	   public int markColor = 0x00000000;
	   public float markSize = 0;
	   
	   public StyledChartPoint() {   
	   }
	   
	   public StyledChartPoint(float x,float y) {
	      this.x = x;
	      this.y = y;
	   }
	   
	   public StyledChartPoint(float x,float y,int lineColor) {
	      this.x = x;
	      this.y = y;
	      this.lineColor = lineColor;
	   }
	   
	   public StyledChartPoint(float x,float y,int lineColor,int fillColor) {
	      this.x = x;
	      this.y = y;
	      this.lineColor = lineColor;
	      this.fillColor = fillColor;
	   }

	   public StyledChartPoint(float x,float y,int lineColor,int fillColor,int markColor,float markSize) {
	      this.x = x;
	      this.y = y;
	      this.lineColor = lineColor;
	      this.fillColor = fillColor;
	      this.markColor = markColor;
	      this.markSize = markSize;
	   }
	   
}
