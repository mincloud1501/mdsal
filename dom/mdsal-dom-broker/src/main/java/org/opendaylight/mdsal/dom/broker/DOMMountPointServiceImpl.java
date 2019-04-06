/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.mdsal.dom.broker;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.MutableClassToInstanceMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.opendaylight.mdsal.dom.api.DOMMountPoint;
import org.opendaylight.mdsal.dom.api.DOMMountPointListener;
import org.opendaylight.mdsal.dom.api.DOMMountPointService;
import org.opendaylight.mdsal.dom.api.DOMService;
import org.opendaylight.mdsal.dom.spi.SimpleDOMMountPoint;
import org.opendaylight.yangtools.concepts.AbstractObjectRegistration;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.concepts.ObjectRegistration;
import org.opendaylight.yangtools.util.ListenerRegistry;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DOMMountPointServiceImpl implements DOMMountPointService {
    private static final Logger LOG = LoggerFactory.getLogger(DOMMountPointServiceImpl.class);

    private final Map<YangInstanceIdentifier, DOMMountPoint> mountPoints = new HashMap<>();

    private final ListenerRegistry<DOMMountPointListener> listeners = ListenerRegistry.create();

    @Override
    public Optional<DOMMountPoint> getMountPoint(final YangInstanceIdentifier path) {
        return Optional.ofNullable(mountPoints.get(path));
    }

    @Override
    public DOMMountPointBuilder createMountPoint(final YangInstanceIdentifier path) {
        checkState(!mountPoints.containsKey(path), "Mount point already exists");
        return new DOMMountPointBuilderImpl(path);
    }

    @Override
    public ListenerRegistration<DOMMountPointListener> registerProvisionListener(final DOMMountPointListener listener) {
        return listeners.register(listener);
    }

    /**
     * Deprecated.
     *
     * @deprecated this method should never have been exposed publicly - registration should be done via the public
     *             {@link #createMountPoint} interface. As such, this method expects the {@code mountPoint} part to be
     *             of type {@link SimpleDOMMountPoint}.
     */
    @Deprecated
    public ObjectRegistration<DOMMountPoint> registerMountPoint(final DOMMountPoint mountPoint) {
        Preconditions.checkArgument(mountPoint instanceof SimpleDOMMountPoint,
                "Expected mountpoint argument to be of type SimpleDOMMountPoint");
        return doRegisterMountPoint((SimpleDOMMountPoint) mountPoint);
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    private ObjectRegistration<DOMMountPoint> doRegisterMountPoint(final SimpleDOMMountPoint mountPoint) {
        final YangInstanceIdentifier mountPointId = mountPoint.getIdentifier();
        synchronized (mountPoints) {
            final DOMMountPoint prev = mountPoints.putIfAbsent(mountPointId, mountPoint);
            checkState(prev == null, "Mount point %s already exists as %s", mountPointId, prev);
        }
        listeners.forEach(listener -> {
            try {
                listener.getInstance().onMountPointCreated(mountPointId);
            } catch (final Exception ex) {
                LOG.error("Listener {} failed on mount point {} created event", listener, mountPoint, ex);
            }
        });

        return new AbstractObjectRegistration<DOMMountPoint>(mountPoint) {
            @Override
            protected void removeRegistration() {
                doUnregisterMountPoint(getInstance().getIdentifier());
            }
        };
    }

    /**
     * Unregisters mountpoint.
     *
     * @param mountPointId Mountpoint identifier.
     *
     * @deprecated this method should never have been exposed publicly - mountpoint should be unregistered by simply
     *             closing its registration.
     *
     */
    @Deprecated
    public void unregisterMountPoint(final YangInstanceIdentifier mountPointId) {
        doUnregisterMountPoint(mountPointId);
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    private void doUnregisterMountPoint(final YangInstanceIdentifier mountPointId) {
        synchronized (mountPoints) {
            if (mountPoints.remove(mountPointId) == null) {
                LOG.warn("Removing non-existent mount point {} at", mountPointId, new Throwable());
                return;
            }
        }

        listeners.forEach(listener -> {
            try {
                listener.getInstance().onMountPointRemoved(mountPointId);
            } catch (final Exception ex) {
                LOG.error("Listener {} failed on mount point {} removed event", listener, mountPointId, ex);
            }
        });
    }

    final class DOMMountPointBuilderImpl implements DOMMountPointBuilder {

        private final MutableClassToInstanceMap<DOMService> services = MutableClassToInstanceMap.create();
        private final YangInstanceIdentifier path;
        private SchemaContext schemaContext;

        private SimpleDOMMountPoint mountPoint;

        DOMMountPointBuilderImpl(final YangInstanceIdentifier path) {
            this.path = requireNonNull(path);
        }

        @VisibleForTesting
        SchemaContext getSchemaContext() {
            return schemaContext;
        }

        @VisibleForTesting
        Map<Class<? extends DOMService>, DOMService> getServices() {
            return services;
        }

        @Override
        public <T extends DOMService> DOMMountPointBuilder addService(final Class<T> type, final T impl) {
            services.putInstance(requireNonNull(type), requireNonNull(impl));
            return this;
        }

        @Override
        public DOMMountPointBuilder addInitialSchemaContext(final SchemaContext ctx) {
            schemaContext = requireNonNull(ctx);
            return this;
        }

        @Override
        public ObjectRegistration<DOMMountPoint> register() {
            checkState(mountPoint == null, "Mount point is already built.");
            mountPoint = SimpleDOMMountPoint.create(path, services, schemaContext);
            return doRegisterMountPoint(mountPoint);
        }
    }
}
