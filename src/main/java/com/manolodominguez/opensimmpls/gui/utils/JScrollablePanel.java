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
package com.manolodominguez.opensimmpls.gui.utils;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * This class implements a panel whose behaviour once added to a JScrollPane is
 * correct. JPanel are not scrollable and if one mix some JPanel inside a
 * JScrollPane they do not respond well to resizing actions. This class solves
 * the problem.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
@SuppressWarnings("serial")
public class JScrollablePanel extends JPanel implements Scrollable {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of JScrollablePanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param layout The desired layout for this JScrollablePanel.
     * @since 2.0
     */
    public JScrollablePanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * This method is the constructor of the class. It is create a new instance
     * of JScrollablePanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public JScrollablePanel() {
    }

    /**
     * This method gets the preferred scrollable viewport size of this
     * JScrollablePanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return the preferred scrollable viewport size of this JScrollablePanel.
     * @since 2.0
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }

    /**
     * This method gets the scrollable unit increment of this JScrollablePanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or
     * SwingConstants.HORIZONTAL
     * @param direction Less than zero to scroll up/left, greater than zero for
     * down/right.
     * @return the scrollable unir increment of this JScrollablePanel.
     * @since 2.0
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }

    /**
     * This method gets the scrollable block increment of this JScrollablePanel.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or
     * SwingConstants.HORIZONTAL
     * @param direction Less than zero to scroll up/left, greater than zero for
     * down/right.
     * @return the scrollable block increment of this JScrollablePanel.
     * @since 2.0
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 50;
    }

    /**
     * This method checks whether a viewport should always force the width of
     * this JScrollPanel to match the width of the viewport.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if a viewport should always force the width of this
     * JScrollPanel to match the width of the viewport. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * This method checks whether a viewport should always force the height of
     * this JScrollPanel to match the height of the viewport.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TRUE, if a viewport should always force the height of this
     * JScrollPanel to match the height of the viewport. Otherwise, FALSE.
     * @since 2.0
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
