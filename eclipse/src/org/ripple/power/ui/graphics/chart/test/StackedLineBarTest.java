package org.ripple.power.ui.graphics.chart.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.BarChartCanvas;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.ui.graphics.chart.StackedBarChartCanvas;
import org.ripple.power.ui.graphics.chart.StackedLineChartCanvas;

public class StackedLineBarTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Dimension size = new Dimension(400, 400);
		frame.setPreferredSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setPreferredSize(size);
		frame.setSize(size);
		   // create RED dummy serie
	      ChartValueSerie rr = new ChartValueSerie(LColor.red,1);
	      rr.addPoint(new ChartValue("jan",10));
	      rr.addPoint(new ChartValue("feb",15));
	      rr.addPoint(new ChartValue("mar",25));
	      rr.addPoint(new ChartValue("apr",30));
	      rr.addPoint(new ChartValue("may",15));
	      rr.addPoint(new ChartValue("jun",30));
	      rr.addPoint(new ChartValue("jul",70));
	      rr.addPoint(new ChartValue("aug",100));
	      rr.addPoint(new ChartValue("sep",130));
	      rr.addPoint(new ChartValue("oct",125));
	      rr.addPoint(new ChartValue("nov",120));
	      rr.addPoint(new ChartValue("dec",115));
	      
	      // create GREEN dummy serie
	      ChartValueSerie gg = new ChartValueSerie(LColor.green,2);
	      gg.addPoint(new ChartValue("jan",15));
	      gg.addPoint(new ChartValue("feb",30));
	      gg.addPoint(new ChartValue("mar",50));
	      gg.addPoint(new ChartValue("apr",75));
	      gg.addPoint(new ChartValue("may",100));
	      gg.addPoint(new ChartValue("jun",70));
	      gg.addPoint(new ChartValue("jul",60));
	      gg.addPoint(new ChartValue("aug",45));
	      gg.addPoint(new ChartValue("sep",20));
	      gg.addPoint(new ChartValue("oct",15));
	      gg.addPoint(new ChartValue("nov",10));
	      gg.addPoint(new ChartValue("dec",5));
	      
	      // create BLUE dummy serie
	      ChartValueSerie bb = new ChartValueSerie(LColor.blue,3);
	      bb.addPoint(new ChartValue("jan",150));
	      bb.addPoint(new ChartValue("feb",120));
	      bb.addPoint(new ChartValue("mar",100));
	      bb.addPoint(new ChartValue("apr",90));
	      bb.addPoint(new ChartValue("may",80));
	      bb.addPoint(new ChartValue("jun",70));
	      bb.addPoint(new ChartValue("jul",55));
	      bb.addPoint(new ChartValue("aug",40));
	      bb.addPoint(new ChartValue("sep",25));
	      bb.addPoint(new ChartValue("oct",35));
	      bb.addPoint(new ChartValue("nov",40));
	      bb.addPoint(new ChartValue("dec",50));
	   
	  
	     StackedLineChartCanvas  canvas  = new StackedLineChartCanvas(400, 400);
	     
	      // add lines to chart
	     canvas.setLabelMaxNum(12);
	     canvas.addSerie(rr);
	     canvas.addSerie(gg);
	     canvas.addSerie(bb);
	      
	      // add lines to chart


	  //   canvas.setXgrid(false,-1,1,10);
	  
	      
	      
		
		frame.add(canvas);
		frame.setVisible(true);


	}
}
