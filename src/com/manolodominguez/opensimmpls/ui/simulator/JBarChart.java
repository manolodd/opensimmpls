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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author manolodd
 */
public class JBarChart {

    public JBarChart(String chartTitle, String xAxisTitle, String yAxisTitle, DefaultCategoryDataset series) {
        this.categories = series;
        this.chart = ChartFactory.createBarChart(chartTitle, null, yAxisTitle, this.categories, PlotOrientation.VERTICAL, true, true, false);
        this.plot = this.chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.1);
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

    private DefaultCategoryDataset categories;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private CategoryPlot plot;
}
