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
package simMPLS.hardware.timer;

/**
 * This class implements a timestamp.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 1.1
 */
public class TTimestamp implements Comparable {

    /**
     * This method is the constructor of the class. Is creates a new instance of
     * TTimestamp that represent a given moment. Through this timestamp, a
     * moment is represented as M:N where M=milliseconds and N=nanoseconds.
     *
     * @param millisecond The millisecond part of the timestamp.
     * @param nanosecond The nanosecond part of the timestamp.
     * @since 1.0
     */
    public TTimestamp(long millisecond, int nanosecond) {
        this.millisecond = millisecond;
        this.nanosecond = nanosecond;
    }

    /**
     * This method compares the current timestamp with another that is specified
     * as an argument.
     *
     * @param anotherTimestamp An external TTimestamp instace to be compared
     * with the current one.
     * @return ARGUMENT_IS_GREATER, BOTH_ARE_EQUAL or ARGUMENT_IS_LOWER,
     * depending on whether the specified argument is greater, equal or lower
     * than the current instance.
     * @since 1.0
     */
    @Override
    public int compareTo(Object anotherTimestamp) {
        TTimestamp argument = (TTimestamp) anotherTimestamp;
        if (this.millisecond < argument.getMillisecond()) {
            return this.ARGUMENT_IS_GREATER;
        }
        if (this.millisecond > argument.getMillisecond()) {
            return this.ARGUMENT_IS_LOWER;
        }
        if (this.millisecond == argument.getMillisecond()) {
            if (this.nanosecond < argument.getNanosecond()) {
                return this.ARGUMENT_IS_GREATER;
            }
            if (this.nanosecond > argument.getNanosecond()) {
                return this.ARGUMENT_IS_LOWER;
            }
            if (this.nanosecond == argument.getNanosecond()) {
                return this.BOTH_ARE_EQUAL;
            }
        }
        return this.BOTH_ARE_EQUAL;
    }

    /**
     * This method gets the nanosecond component of the timestamp.
     *
     * @return The nanosecond component of the timestamp.
     * @since 1.0
     */
    public int getNanosecond() {
        return this.nanosecond;
    }

    /**
     * This method set the nanosecond component of the timestamp.
     *
     * @param nanosecond The nanosecond component of the timestamp.
     * @since 1.0
     */
    public void setNanosecond(int nanosecond) {
        this.nanosecond = nanosecond;
    }

    /**
     * This method adds the specified number of nanoseconds to the current
     * timestamp.
     *
     * @param addedNanosecond The number of nanoseconds to be added to the
     * current timestamp.
     * @since 1.0
     */
    public void increaseNanoseconds(int addedNanosecond) {
        this.nanosecond += addedNanosecond;
        long integerDivision = (this.nanosecond / 1000000);
        if (integerDivision > 0) {
            increaseMiliseconds(integerDivision);
            this.nanosecond %= 1000000;
        }
    }

    /**
     * This method get the millisecond component of the timestamp.
     *
     * @return The millisecond component of the timestamp.
     * @since 1.0
     */
    public long getMillisecond() {
        return this.millisecond;
    }

    /**
     * This method set the millisecond component of the timestamp.
     *
     * @param millisecond The millisecond component of the timestamp.
     * @since 1.0
     */
    public void setMillisecond(long millisecond) {
        this.millisecond = millisecond;
    }

    /**
     * This method set the current timestamp using the values of another
     * timestamp specified as an argument.
     *
     * @param anotherTimestamp A timestamp that will be used to set the values
     * of the current one..
     * @since 1.0
     */
    public void setTimestamp(TTimestamp anotherTimestamp) {
        this.millisecond = anotherTimestamp.getMillisecond();
        this.nanosecond = anotherTimestamp.getNanosecond();
    }

    /**
     * This method adds the specified number of milliseconds to the current
     * timestamp.
     *
     * @param addedMilliseconds The number of milliseconds to be added to the
     * current timestamp.
     * @since 1.0
     */
    public void increaseMiliseconds(long addedMilliseconds) {
        this.millisecond += addedMilliseconds;
    }

    /**
     * This method gets the moment represented by the current timestamp, in
     * nanoseconds.
     *
     * @return The moment represented by the current timestamp, in nanoseconds.
     * @since 1.0
     */
    public long getTotalAsNanoseconds() {
        return (long) ((this.getMillisecond() * 1000000) + this.getNanosecond());
    }
    public static final int ARGUMENT_IS_GREATER = -1;
    public static final int BOTH_ARE_EQUAL = 0;
    public static final int ARGUMENT_IS_LOWER = 1;
    private long millisecond;
    private int nanosecond;
}
