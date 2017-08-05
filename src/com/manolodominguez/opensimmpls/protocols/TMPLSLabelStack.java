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
package com.manolodominguez.opensimmpls.protocols;

import java.util.Iterator;
import java.util.LinkedList;
import com.manolodominguez.opensimmpls.utils.EIDGeneratorOverflow;
import com.manolodominguez.opensimmpls.utils.TIDGenerator;

/**
 * This class implements the MPLS label stack of a MPLS packet.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TMPLSLabelStack {

    /**
     * This method is the constructor of the class. It is create a new empty
     * MPLS label stack.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
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
     * @since 2.0
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
     * @since 2.0
     */
    public void pushTop(TMPLSLabel mplsLabel) {
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
     * @since 2.0
     */
    public TMPLSLabel getTop() {
        return (TMPLSLabel) this.stack.getLast();
    }

    /**
     * This method removes the MPLS label from the top of the MPLS label stack.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
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
     * @since 2.0
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
     * @since 2.0
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
