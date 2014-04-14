/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.examples.lib.timerbean;

import java.beans.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

/** The BeanInfo for the Timer bean.
*
* @version  1.00, Jul 20, 1998
*/
public class TimerBeanInfo extends SimpleBeanInfo {

    /** Icon for image data objects. */
    private Image icon;

    /** Array of property descriptors. */
    private static PropertyDescriptor[] desc;

    /** Array of event set descriptors. */
    private static EventSetDescriptor[] events;

    // initialization of the array of descriptors
    static {
        try {
            desc = new PropertyDescriptor[2];
            desc[0] = new PropertyDescriptor("Delay", Timer.class, "getDelay", "setDelay");
            desc[1] = new PropertyDescriptor("Once Only", Timer.class, "getOnceOnly", "setOnceOnly");

            events = new EventSetDescriptor[1];
            Method listenerMethod = TimerListener.class.getMethod(
                                        "onTime",
                                        new Class[] { ActionEvent.class }
                                    );
            Method addListenerMethod = Timer.class.getMethod(
                                           "addTimerListener",
                                           new Class[] { TimerListener.class }
                                       );
            Method removeListenerMethod = Timer.class.getMethod(
                                              "removeTimerListener",
                                              new Class[] { TimerListener.class }
                                          );
            events[0] = new EventSetDescriptor("timer",
                                               TimerListener.class,
                                               new Method[] { listenerMethod },
                                               addListenerMethod,
                                               removeListenerMethod);
        }
        catch (IntrospectionException ex) {
            ex.printStackTrace();
        }
        catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public TimerBeanInfo() {
        icon = loadImage ("/org/netbeans/examples/lib/timerbean/timer.gif");
    }

    /** Provides the Timer's icon */
    public Image getIcon(int type) {
        return icon;
    }

    /** Descriptor of valid properties
    * @return array of properties
    */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return desc;
    }

    /** Descriptor of valid events
    * @return array of event sets
    */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return events;
    }

    public int getDefaultEventIndex() {
        return 0;
    }

}
