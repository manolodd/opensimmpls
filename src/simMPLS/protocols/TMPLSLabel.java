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

/**
 * This class implements a MPLS label.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TMPLSLabel implements Comparable {

    /**
     * This method is the constructor of the class. It is create a new MPLS
     * label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Label identifier. It will be used with sorting purposes.
     * @since 1.0
     */
    public TMPLSLabel(int id) {
        //FIX: Change these harcoded values and use constants instead.
        this.ttl = 256;
        this.label = 16;
        this.exp = 0;
        this.bos = true;
        this.identifier = id;
    }

    /**
     * This method is the constructor of the class. It is create a new MPLS
     * label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 1.0
     */
    public TMPLSLabel() {
        //FIX: Change these harcoded values and use constants instead.
        this.ttl = 256;
        this.label = 16;
        this.exp = 0;
        this.bos = true;
        this.identifier = 0;
    }

    /**
     * This method sets the label identifier that will be used for sorting
     * purposes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Label identifier. It will be used with sorting purposes.
     * @since 1.0
     */
    public void setID(int id) {
        this.identifier = id;
    }

    /**
     * This method gets the label identifier that will be used for sorting
     * purposes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Label identifier. It will be used with sorting purposes.
     * @since 1.0
     */
    public int getID() {
        return this.identifier;
    }

    /**
     * This method sets the value for "label" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param label The value for the "label" field of this MPLS label.
     * @since 1.0
     */
    public void setLabel(int label) {
        this.label = label;
    }

    /**
     * This method gets the value of "label" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return label The value of the "label" field of this MPLS label.
     * @since 1.0
     */
    public int getLabel() {
        return this.label;
    }

    /**
     * This method sets the value for "TTL" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param ttl The value for the "TTL" field of this MPLS label.
     * @since 1.0
     */
    public void setTTL(int ttl) {
        this.ttl = ttl;
    }

    /**
     * This method sets the value of "TTL" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The value of the "TTL" field of this MPLS label.
     * @since 1.0
     */
    public int getTTL() {
        return this.ttl;
    }

    /**
     * This method sets the value for "EXP" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param exp The value for the "EXP" field of this MPLS label.
     * @since 1.0
     */
    public void setEXP(int exp) {
        this.exp = exp;
    }

    /**
     * This method gets the value for "EXP" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The value of the "EXP" field of this MPLS label.
     * @since 1.0
     */
    public int getEXP() {
        return this.exp;
    }

    /**
     * This method sets the value for "BoS" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param bos The value for the "BoS" field of this MPLS label.
     * @since 1.0
     */
    public void setBoS(boolean bos) {
        this.bos = bos;
    }

    /**
     * This method gets the value for "BoS" field of this MPLS label.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The value of the "BoS" field of this MPLS label.
     * @since 1.0
     */
    public boolean getBoS() {
        return this.bos;
    }

    /**
     * This metod compares the current instance to the one specified as a
     * parameter, for sorting purposes.
     *
     * @param mplsLabelObject The object to be compared to the current instance.
     * @return -1, 0 or 1, depending on whether the current instance is lower,
     * equal or greater than the one passed in as a parameter.
     * @since 1.0
     */
    @Override
    public int compareTo(Object mplsLabelObject) {
        TMPLSLabel label2 = (TMPLSLabel) mplsLabelObject;
        //FIX: change returned values and use class constants insted.
        if (this.getID() > label2.getID()) {
            return 1;
        } else if (this.getID() == label2.getID()) {
            return 0;
        } else {
            return -1;
        }
    }

    private int ttl;
    private int label;
    private int exp;
    private boolean bos;
    private int identifier;
}
