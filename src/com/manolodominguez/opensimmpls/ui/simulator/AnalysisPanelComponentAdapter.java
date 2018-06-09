/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.opensimmpls.ui.simulator;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import org.jfree.chart.ChartPanel;

/**
 * This class implements a component adapter that is used to keep aspect ratio
 * for charts of analysis panel after a window resizing.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class AnalysisPanelComponentAdapter extends ComponentAdapter {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of AnalysisPanelComponentAdapter.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public AnalysisPanelComponentAdapter() {
        this.chartPanels = new LinkedList<>();
        this.analysisPanelsize = new Dimension(DEFAULT_CHART_WIDTH, DEFAULT_CHART_HEIGHT);
        resizeCharts();
    }

    /**
     * This method is called when the component is hidden.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param e The event of the effected component that triggers this method.
     * @since 2.0
     */
    @Override
    public void componentHidden(ComponentEvent e) {
        super.componentHidden(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    /**
     * This method is called when the component is shown.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param e The event of the effected component that triggers this method.
     * @since 2.0
     */
    @Override
    public void componentShown(ComponentEvent e) {
        super.componentShown(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    /**
     * This method is called when the component is moved.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param e The event of the effected component that triggers this method.
     * @since 2.0
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        super.componentMoved(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    /**
     * This method is called when the component is resized.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param e The event of the effected component that triggers this method.
     * @since 2.0
     */
    @Override
    public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    /**
     * This method adds a new ChartPanel to keep its aspect ration after a
     * resizing.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param chartPanel a new ChartPanel to keep its aspect ration after a
     * resizing.
     * @since 2.0
     */
    public void addChartPanel(ChartPanel chartPanel) {
        if (chartPanel != null) {
            this.chartPanels.add(chartPanel);
        }
    }

    /**
     * This method sets the initial size of the charts in the analysis panel
     * taking into account the size of the analysis panel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param analysisPanelsize the size of the analysis panel.
     * @since 2.0
     */
    public void setInitialChartsSize(Dimension analysisPanelsize) {
        this.analysisPanelsize = analysisPanelsize;
        resizeCharts();
    }

    /**
     * This method sets the size of every charts in the analysis panel to keep
     * the predefined aspect ratio.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param analysisPanelsize the size of the analysis panel.
     * @since 2.0
     */
    private void resizeCharts() {
        for (ChartPanel cp : this.chartPanels) {
            if (cp != null) {
                cp.setPreferredSize(new Dimension((int) this.analysisPanelsize.getWidth() / CHARTS_PER_ROW, (int) this.analysisPanelsize.getWidth() / CHARTS_PER_ROW * ASPECT_RATIO_HEIGHT / ASPECT_RATIO_WIDTH));
            }
        }
    }

    private static final int DEFAULT_CHART_WIDTH = 320;
    private static final int DEFAULT_CHART_HEIGHT = 240;
    private static final int CHARTS_PER_ROW = 2;
    private static final int ASPECT_RATIO_WIDTH = 4;
    private static final int ASPECT_RATIO_HEIGHT = 3;

    private LinkedList<ChartPanel> chartPanels;
    private Dimension analysisPanelsize;
}
