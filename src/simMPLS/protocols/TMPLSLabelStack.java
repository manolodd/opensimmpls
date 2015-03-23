/* 
 * Copyright (C) 2014 Manuel Domínguez-Dorado <ingeniero@manolodominguez.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simMPLS.protocols;

import java.util.Iterator;
import java.util.LinkedList;
import simMPLS.utils.EIDGeneratorOverflow;
import simMPLS.utils.TIDGenerator;

/**
 * This class implements the MPLS label stack of a MPLS packet.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TMPLSLabelStack {

    /**
     * This method is the constructor of the class. It is create a new empty
     * MPLS label stack.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TMPLSLabelStack() {
        this.stack = new LinkedList();
        this.idGenerator = new TIDGenerator();
    }

    /**
     * This method gets the size of the label stack in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The size of the lable stack in bytes (octects).
     * @since 1.0
     */
    public int getSize() {
        return stack.size();
    }

    /**
     * This method adds a new MPLS label to the top of the label stack (push).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsLabel The MPLS label to be inserted in the top of the label
     * stack. MPLS.
     * @since 1.0
     */
    public void pushLabel(TMPLSLabel mplsLabel) {
        try {
            mplsLabel.setID(this.idGenerator.getNew());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        this.stack.addLast(mplsLabel);
    }

    /**
     * This method gets the MPLS label from the top of the MPLS label stack, but
     * does not remove it.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return MPLS label in top of MPLS label stack.
     * @since 1.0
     */
    public TMPLSLabel getTop() {
        return (TMPLSLabel) this.stack.getLast();
    }

    /**
     * This method removes the MPLS label from the top of the MPLS label stack.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void popTop() {
        this.stack.removeLast();
    }

    /**
     * This method replaces (swap) the MPLS label in top of the MPLS label stack
     * with the one specified as an argument.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsLabel the MPLS label that will replace the one in top of the
     * MPLS label stack.
     * @since 1.0
     */
    public void swapTop(TMPLSLabel mplsLabel) {
        this.popTop();
        try {
            mplsLabel.setID(this.idGenerator.getNew());
        } catch (EIDGeneratorOverflow e) {
            e.printStackTrace();
        }
        this.stack.addLast(mplsLabel);
    }

    /**
     * This method removes all MPLS labels from the label stack.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public void clear() {
        Iterator iterator = this.stack.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    private LinkedList stack;
    private TIDGenerator idGenerator;
}
