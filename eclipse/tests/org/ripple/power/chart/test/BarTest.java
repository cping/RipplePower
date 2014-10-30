package org.ripple.power.chart.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.BarChartCanvas;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;
import org.ripple.power.ui.graphics.chart.StackedBarChartCanvas;

public class BarTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Dimension size = new Dimension(160, 200);
		frame.setPreferredSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setPreferredSize(size);
		frame.setSize(size);
	
		  ChartValueSerie rr = new ChartValueSerie(LColor.RED.getRGB(),1);
	      rr.addPoint(new ChartValue("Total",100f));
	      rr.addPoint(new ChartValue("You",1f,LColor.green));
	    

	     BarChartCanvas canvas  = new BarChartCanvas(160, 200);
	     canvas.setYLabelFlag("%");
	     canvas.setGridAA(false);
	     
	      canvas.addSerie(rr);
	      canvas.offsetX = -25;
	      
	      
		
		frame.add(canvas);
		frame.setVisible(true);


	}
}
