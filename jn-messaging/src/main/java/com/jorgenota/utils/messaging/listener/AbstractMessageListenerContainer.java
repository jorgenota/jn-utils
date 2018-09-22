/*
 * Copyright 2013-2014 the original author or authors.
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

package com.jorgenota.utils.messaging.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

/**
 * Abstract base class for message listener containers providing basic lifecycle capabilities and collaborator for the
 * concrete sub classes. This class implements all lifecycle and configuration specific interface used by the Spring
 * container to create, initialize and start the container.
 */
@Slf4j
public abstract class AbstractMessageListenerContainer implements InitializingBean, DisposableBean, SmartLifecycle {

    protected final String className = ClassUtils.getShortName(this.getClass());
    private final Object lifecycleMonitor = new Object();
    private boolean autoStartup = true;
    private int phase = Integer.MAX_VALUE;

    //Settings that are changed at runtime
    private boolean active;
    private boolean running;

    @Override
    public boolean isAutoStartup() {
        return this.autoStartup;
    }

    /**
     * Configures if this container should be automatically started. The default value is true
     *
     * @param autoStartup - false if the container will be manually started
     */
    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public void stop(@NonNull Runnable callback) {
        this.stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return this.phase;
    }

    /**
     * Configure a custom phase for the container to start. This allows to start other beans that also implements the
     * {@link SmartLifecycle} interface.
     *
     * @param phase - the phase that defines the phase respecting the {@link org.springframework.core.Ordered} semantics
     */
    public void setPhase(int phase) {
        this.phase = phase;
    }

    public boolean isActive() {
        synchronized (this.lifecycleMonitor) {
            return this.active;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        synchronized (this.lifecycleMonitor) {
            doInitialize();
            this.active = true;
            this.lifecycleMonitor.notifyAll();
        }
    }

    @Override
    public void start() {
        log.debug("Starting container {}", this.className);
        synchronized (this.lifecycleMonitor) {
            doStart();
            this.running = true;
            this.lifecycleMonitor.notifyAll();
        }
    }

    @Override
    public void stop() {
        log.debug("Stopping container {}", this.className);
        synchronized (this.lifecycleMonitor) {
            doStop();
            this.running = false;
            this.lifecycleMonitor.notifyAll();
        }
    }

    @Override
    public boolean isRunning() {
        synchronized (lifecycleMonitor) {
            return this.running;
        }
    }

    @Override
    public void destroy() {
        synchronized (this.lifecycleMonitor) {
            stop();
            this.active = false;
            doDestroy();
        }
    }

    protected abstract void doInitialize();

    protected abstract void doStart();

    protected abstract void doStop();

    protected abstract void doDestroy();
}
