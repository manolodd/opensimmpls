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
package com.manolodominguez.opensimmpls.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a generic semaphore that can be used to limit the
 * number of threads that can acces a piece of code simultaneously. That's
 * useful in multithreading environments.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSemaphore {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TSemaphore.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSemaphore() {
        this.semaphore = GREEN;
    }

    /**
     * When this method is called from a thread, the thread is blocked if there
     * is a thread that has previously called the method, until that thread
     * releases the semaphore. If no thread has locked the semaphore, then the
     * calling thread takes control and blocks the rest until it releases the
     * semaphore.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void setRed() {
        while (this.semaphore == RED) {
            try {
                wait();
            } catch (InterruptedException e) {
                this.logger.error(e.getMessage(), e);
            }
        }
        this.semaphore = RED;
    }

    /**
     * When a thread calls this method, it releases the semaphore. In this way,
     * the next thread that called the setRed() method and was blocked, will be
     * released.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public synchronized void setGreen() {
        this.semaphore = GREEN;
        notify();
    }

    private boolean semaphore;

    private static final boolean RED = true;
    private static final boolean GREEN = false;
    private final Logger logger = LoggerFactory.getLogger(TSemaphore.class);
}
