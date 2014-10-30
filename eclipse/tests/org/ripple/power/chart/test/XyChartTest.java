package org.ripple.power.chart.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.BarChartCanvas;
import org.ripple.power.ui.graphics.chart.ChartPoint;
import org.ripple.power.ui.graphics.chart.ChartPointSerie;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.ui.graphics.chart.StackedBarChartCanvas;
import org.ripple.power.ui.graphics.chart.StackedLineChartCanvas;
import org.ripple.power.ui.graphics.chart.StyledChartPoint;
import org.ripple.power.ui.graphics.chart.StyledChartPointSerie;
import org.ripple.power.ui.graphics.chart.StyledXyChartCanvas;
import org.ripple.power.ui.graphics.chart.XyChartView;

public class XyChartTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Dimension size = new Dimension(400, 400);
		frame.setPreferredSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setPreferredSize(size);
		frame.setSize(size);

	      // create RED dummy serie
	      ChartPointSerie rr = new ChartPointSerie(LColor.red,1);
	      rr.addPoint(new ChartPoint(-98,99));
	      rr.addPoint(new ChartPoint(-49,80));
	      rr.addPoint(new ChartPoint(-5,180));
	      rr.addPoint(new ChartPoint(17,99));
	      rr.addPoint(new ChartPoint(54,80));
	      rr.addPoint(new ChartPoint(125,120));
	      rr.addPoint(new ChartPoint(158,20));
	      rr.addPoint(new ChartPoint(209,50));
	      rr.addPoint(new ChartPoint(317,109));
	      
	      // create GREEN dummy serie
	      ChartPointSerie gg = new ChartPointSerie(LColor.green,2);
	      gg.addPoint(new ChartPoint(17,-10));
	      gg.addPoint(new ChartPoint(54,20));
	      gg.addPoint(new ChartPoint(125,-50));
	      gg.addPoint(new ChartPoint(158,89));
	      gg.addPoint(new ChartPoint(209,20));
	      gg.addPoint(new ChartPoint(317,Float.NaN));
	      gg.addPoint(new ChartPoint(350,99));
	      gg.addPoint(new ChartPoint(461,75));
	      gg.addPoint(new ChartPoint(495,33));
	      
	      // create BLUE dummy serie
	      ChartPointSerie bb = new ChartPointSerie(LColor.blue,3);
	      bb.addPoint(new ChartPoint(-98,-20));
	      bb.addPoint(new ChartPoint(-49,-40));
	      bb.addPoint(new ChartPoint(-5,Float.NaN));
	      bb.addPoint(new ChartPoint(17,139));
	      bb.addPoint(new ChartPoint(54,160));
	      bb.addPoint(new ChartPoint(209,90));
	      bb.addPoint(new ChartPoint(317,70));
	      bb.addPoint(new ChartPoint(350,79));
	      bb.addPoint(new ChartPoint(461,175));
	      bb.addPoint(new ChartPoint(495,153));
	  
	      XyChartView  canvas  = new XyChartView(400, 400);
	     
	      
	      // add lines to chart
	     canvas.addSerie(rr);
	     canvas.addSerie(gg);
	     canvas.addSerie(bb);
	      // add lines to chart


	  //   canvas.setXgrid(false,-1,1,10);
	  
	      
	      
		
		frame.add(canvas);
		frame.setVisible(true);


	}
}
