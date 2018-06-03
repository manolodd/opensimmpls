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
        renderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(2, Color.GREEN);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(3, Color.MAGENTA);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(4, Color.ORANGE);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(5, Color.BLACK);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        this.plot.setRenderer(renderer);
        this.plot.setBackgroundPaint(Color.white);
        this.plot.setRangeGridlinesVisible(true);
        this.plot.setRangeGridlinePaint(Color.DARK_GRAY);
        this.chart.getLegend().setFrame(BlockBorder.NONE);
        this.chart.setTitle(new TextTitle(chartTitle, new Font("Serif", Font.BOLD, 18)));
        this.chartPanel = new ChartPanel(this.chart, false, true, false, false, false);
        this.chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.chartPanel.setBackground(Color.white);
        this.chartPanel.setDomainZoomable(false);
        this.chartPanel.setRangeZoomable(false);
    }

    public JFreeChart getChart() {
        return this.chart;
    }

    public ChartPanel getChartPanel() {
        return this.chartPanel;
    }

    DefaultCategoryDataset categories;
    JFreeChart chart;
    ChartPanel chartPanel;
    CategoryPlot plot;
}
