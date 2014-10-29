package org.ripple.power.ui.graphics.chart.test;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.ripple.power.ui.graphics.LColor;
import org.ripple.power.ui.graphics.chart.ChartValue;
import org.ripple.power.ui.graphics.chart.ChartValueSerie;
import org.ripple.power.ui.graphics.chart.LineChartCanvas;

public class LineTest {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Dimension size = new Dimension(400, 400);
		frame.setPreferredSize(size);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		frame.setPreferredSize(size);
		frame.setSize(size);
		LineChartCanvas chart = new LineChartCanvas(frame);
		      // create RED dummy serie
		      ChartValueSerie rr = new ChartValueSerie(LColor.red,1);
		      rr.addPoint(new ChartValue("jan",99));
		      rr.addPoint(new ChartValue("feb",80));
		      rr.addPoint(new ChartValue("mar",180));
		      rr.addPoint(new ChartValue("apr",99));
		      rr.addPoint(new ChartValue("may",80));
		      rr.addPoint(new ChartValue("jun",120));
		      rr.addPoint(new ChartValue("jul",20));
		      rr.addPoint(new ChartValue("aug",50));
		      rr.addPoint(new ChartValue("sep",109));
		      
		      // create GREEN dummy serie
		      ChartValueSerie gg = new ChartValueSerie(LColor.green,2);
		      gg.addPoint(new ChartValue("jan",-10));
		      gg.addPoint(new ChartValue("feb",20));
		      gg.addPoint(new ChartValue("mar",-50));
		      gg.addPoint(new ChartValue("apr",89));
		      gg.addPoint(new ChartValue("may",20));
		      gg.addPoint(new ChartValue("jun",Float.NaN));
		      gg.addPoint(new ChartValue("jul",99));
		      gg.addPoint(new ChartValue("aug",75));
		      gg.addPoint(new ChartValue("sep",33));
		      
		      // create BLUE dummy serie
		      ChartValueSerie bb = new ChartValueSerie(LColor.blue,3);
		      bb.addPoint(new ChartValue("jan",-20));
		      bb.addPoint(new ChartValue("feb",-40));
		      bb.addPoint(new ChartValue("mar",Float.NaN));
		      bb.addPoint(new ChartValue("apr",139));
		      bb.addPoint(new ChartValue("may",160));
		      bb.addPoint(new ChartValue("jun",90));
		      bb.addPoint(new ChartValue("jul",70));
		      bb.addPoint(new ChartValue("aug",79));
		      bb.addPoint(new ChartValue("sep",175));
		      bb.addPoint(new ChartValue("oct",153));
		      
		      // add lines to chart
		      chart.addSerie(rr);
		      chart.addSerie(gg);
		      chart.addSerie(bb);
		frame.add(chart);
		frame.setVisible(true);


	}
}
