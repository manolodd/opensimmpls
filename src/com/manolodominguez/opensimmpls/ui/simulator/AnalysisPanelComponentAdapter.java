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
 *
 * @author manolodd
 */
public class AnalysisPanelComponentAdapter extends ComponentAdapter {

    public AnalysisPanelComponentAdapter() {
        this.chartPanels = new LinkedList<>();
        this.analysisPanelsize = new Dimension(320, 240);
        resizeCharts();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        super.componentHidden(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        super.componentShown(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        super.componentMoved(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        this.analysisPanelsize = e.getComponent().getSize();
        resizeCharts();
    }

    public void addChartPanel(ChartPanel chartPanel) {
        if (chartPanel != null) {
            this.chartPanels.add(chartPanel);
        }
    }

    public void setInitialChartsSize(Dimension analysisPanelsize) {
        this.analysisPanelsize = analysisPanelsize;
        resizeCharts();
    }

    private void resizeCharts() {
        for (ChartPanel cp : this.chartPanels) {
            cp.setPreferredSize(new Dimension((int) this.analysisPanelsize.getWidth() / 2, (int) this.analysisPanelsize.getWidth() / 2 * 3 / 4));
        }
    }

    private LinkedList<ChartPanel> chartPanels;
    private Dimension analysisPanelsize;
}
