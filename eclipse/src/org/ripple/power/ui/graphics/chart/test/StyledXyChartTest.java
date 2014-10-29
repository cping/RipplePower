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
import org.ripple.power.ui.graphics.chart.StyledChartPoint;
import org.ripple.power.ui.graphics.chart.StyledChartPointSerie;
import org.ripple.power.ui.graphics.chart.StyledXyChartCanvas;

public class StyledXyChartTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Dimension size = new Dimension(400, 400);
		frame.setPreferredSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setPreferredSize(size);
		frame.setSize(size);
		 // create FIRST dummy serie
	      StyledChartPointSerie rr = new StyledChartPointSerie(2);
	      rr.addPoint(new StyledChartPoint(-90, 99,0xff99cc00,0xffeeeeee));
	      rr.addPoint(new StyledChartPoint(-49, 80,0xffff4444,0xffffcccc));
	      rr.addPoint(new StyledChartPoint( -5,180,0xff99cc00,0xffeeff99));
	      rr.addPoint(new StyledChartPoint( 17, 99,0xffffbb33,0xffffee99));
	      rr.addPoint(new StyledChartPoint( 54, 80,0xff33bbee,0xffeeeeee));
	      rr.addPoint(new StyledChartPoint(125,120,0xff99cc00,0xffeeeeee));
	      rr.addPoint(new StyledChartPoint(158, 20,0xffff4444,0xffeeeeee));
	      rr.addPoint(new StyledChartPoint(209, 50,0xffff4444,0xffffcccc));
	      rr.addPoint(new StyledChartPoint(297,109,0xff33bbee,0xff99ddff));
	      
	      // create SECOND dummy serie
	      StyledChartPointSerie gg = new StyledChartPointSerie(2);
	      gg.addPoint(new StyledChartPoint( 17,-10,LColor.black,LColor.red,0xffff8800,5));
	      gg.addPoint(new StyledChartPoint( 54, 20,LColor.black,LColor.red,0xffcc0000,5));
	      gg.addPoint(new StyledChartPoint(125,-50,LColor.black,LColor.red,0xff669900,5));
	      gg.addPoint(new StyledChartPoint(158, 89,LColor.black,LColor.red,LColor.gray,8));
	      gg.addPoint(new StyledChartPoint(209, 20,LColor.black,LColor.red,LColor.gray,4));
	      gg.addPoint(new StyledChartPoint(217,Float.NaN,LColor.black,LColor.red,LColor.gray,4));
	      gg.addPoint(new StyledChartPoint(250, 99,LColor.black,LColor.red,LColor.gray,4));
	      gg.addPoint(new StyledChartPoint(261, 75,LColor.black,LColor.red,LColor.gray,4));
	      gg.addPoint(new StyledChartPoint(295, 33,LColor.black,LColor.red,LColor.gray,4));

	  
	     StyledXyChartCanvas  canvas  = new StyledXyChartCanvas(400, 400);
	     
	      
	      // add lines to chart
	     canvas.addSerie(rr);
	     canvas.addSerie(gg);
	      
	      // add lines to chart


	  //   canvas.setXgrid(false,-1,1,10);
	  
	      
	      
		
		frame.add(canvas);
		frame.setVisible(true);


	}
}
