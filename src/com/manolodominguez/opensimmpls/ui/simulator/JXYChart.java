/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manolodominguez.opensimmpls.ui.simulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author manolodd
 */
public class JXYChart {

    public JXYChart(String chartTitle, String xAxisTitle, String yAxisTitle, XYSeriesCollection series) {
        this.series = series;
        this.chart = ChartFactory.createScatterPlot(chartTitle, xAxisTitle, yAxisTitle, this.series, PlotOrientation.VERTICAL, true, true, false);
        this.plot = this.chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(false);
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        this.plot.setRenderer(renderer);
        this.plot.setBackgroundPaint(Color.white);
        this.plot.setRangeGridlinesVisible(true);
        this.plot.setRangeGridlinePaint(Color.DARK_GRAY);
        this.plot.setDomainGridlinesVisible(true);
        this.plot.setDomainGridlinePaint(Color.DARK_GRAY);
        this.chart.getLegend().setFrame(BlockBorder.NONE);
        this.chart.setTitle(new TextTitle(chartTitle, new Font("Serif", Font.BOLD, 18)));
        this.chartPanel = new ChartPanel(this.chart, false, true, false, false, false);
        this.chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.chartPanel.setBackground(Color.white);
        this.chartPanel.setDomainZoomable(false);
        this.chartPanel.setRangeZoomable(false);
    }

    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }

    XYDataset series;
    JFreeChart chart;
    ChartPanel chartPanel;
    XYPlot plot;
}
