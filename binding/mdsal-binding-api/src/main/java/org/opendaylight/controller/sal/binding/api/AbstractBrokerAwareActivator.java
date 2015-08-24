/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.sal.binding.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public abstract class AbstractBrokerAwareActivator implements BundleActivator {

    private static final ExecutorService mdActivationPool = Executors.newCachedThreadPool();
    private BundleContext context;
    private ServiceTracker<BindingAwareBroker, BindingAwareBroker> tracker;
    private BindingAwareBroker broker;
    private final ServiceTrackerCustomizer<BindingAwareBroker, BindingAwareBroker> customizer = new ServiceTrackerCustomizer<BindingAwareBroker, BindingAwareBroker>() {

        @Override
        public BindingAwareBroker addingService(final ServiceReference<BindingAwareBroker> reference) {
            broker = context.getService(reference);
            mdActivationPool.execute(new Runnable() {

                @Override
                public void run() {
                    onBrokerAvailable(broker, context);
                }
            });
            return broker;
        }

        @Override
        public void modifiedService(final ServiceReference<BindingAwareBroker> reference, final BindingAwareBroker service) {
            removedService(reference, service);
            addingService(reference);
        }

        @Override
        public void removedService(final ServiceReference<BindingAwareBroker> reference, final BindingAwareBroker service) {
            broker = context.getService(reference);
            mdActivationPool.execute(new Runnable() {

                @Override
                public void run() {
                    onBrokerRemoved(broker, context);
                }
            });
        }

    };


    @Override
    public final void start(final BundleContext context) throws Exception {
        this.context = context;
        startImpl(context);
        tracker = new ServiceTracker<>(context, BindingAwareBroker.class, customizer);
        tracker.open();

    }



    @Override
    public final  void stop(final BundleContext context) throws Exception {
        tracker.close();
        stopImpl(context);
    }

    protected void startImpl(final BundleContext context) throws Exception {
        // NOOP
    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle. In general, this
     * method should undo the work that the {@code BundleActivator.start} method
     * started. There should be no active threads that were started by this
     * bundle when this bundle returns. A stopped bundle must not call any
     * Framework objects.
     *
     * <p>
     * This method must complete and return to its caller in a timely manner.
     *
     * @param context The execution context of the bundle being stopped.
     */
    protected void stopImpl(final BundleContext context) {
        // NOOP
    }


    protected abstract void onBrokerAvailable(BindingAwareBroker broker, BundleContext context);

    protected void onBrokerRemoved(final BindingAwareBroker broker, final BundleContext context) {
        stopImpl(context);
    }
}