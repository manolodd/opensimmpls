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
        renderer.setSeriesStroke(0, new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(2, Color.GREEN);
        renderer.setSeriesStroke(2, new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(3, Color.MAGENTA);
        renderer.setSeriesStroke(3, new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(4, Color.ORANGE);
        renderer.setSeriesStroke(4, new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(5, Color.BLACK);
        renderer.setSeriesStroke(5, new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        this.plot.setRenderer(renderer);
        this.plot.setBackgroundPaint(Color.WHITE);
        this.plot.setRangeGridlinesVisible(true);
        this.plot.setRangeGridlinePaint(Color.DARK_GRAY);
        this.plot.setDomainGridlinesVisible(true);
        this.plot.setDomainGridlinePaint(Color.DARK_GRAY);
        this.chart.getLegend().setFrame(BlockBorder.NONE);
        this.chart.setTitle(new TextTitle(chartTitle, TITLE_FONT));
        this.chartPanel = new ChartPanel(this.chart, false, true, false, false, false);
        this.chartPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        this.chartPanel.setBackground(Color.WHITE);
        this.chartPanel.setDomainZoomable(false);
        this.chartPanel.setRangeZoomable(false);
    }

    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }

    private static final float STROKE_WIDTH = 1.0f;
    private static final int BORDER_PADDING = 15;
    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 18);

    private XYDataset series;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private XYPlot plot;
}
