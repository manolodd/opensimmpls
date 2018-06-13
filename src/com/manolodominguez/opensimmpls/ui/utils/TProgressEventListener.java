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
package com.manolodominguez.opensimmpls.ui.utils;

import com.manolodominguez.opensimmpls.hardware.timer.IProgressEventListener;
import com.manolodominguez.opensimmpls.hardware.timer.TProgressEvent;
import javax.swing.JProgressBar;

/**
 * This class implements an event listener that receives progress events and
 * updates a given JProgressBar.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TProgressEventListener implements IProgressEventListener {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TProgressEventListener.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param progressBar the progress bar that has to be updated by this
     * TProgressListener.
     * @since 2.0
     */
    public TProgressEventListener(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * This method receives a progress event and updates the corresponding
     * progress bar using the value included in that event.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param progressEvent the event that triggers this method and includes a
     * value used to update the progress bar current value.
     * @since 2.0
     */
    @Override
    public void receiveProgressEvent(TProgressEvent progressEvent) {
        this.progressBar.setValue(progressEvent.getProgressPercentage());
    }

    private JProgressBar progressBar;
}
