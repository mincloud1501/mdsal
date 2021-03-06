/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.dom.adapter.test.util;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.opendaylight.mdsal.dom.api.DOMSchemaService;
import org.opendaylight.mdsal.dom.api.DOMSchemaServiceExtension;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.util.ListenerRegistry;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.model.api.SchemaContextListener;
import org.opendaylight.yangtools.yang.model.api.SchemaContextProvider;

public final class MockSchemaService implements DOMSchemaService, SchemaContextProvider {

    private SchemaContext schemaContext;

    final ListenerRegistry<SchemaContextListener> listeners = ListenerRegistry.create();

    @Override
    public synchronized SchemaContext getGlobalContext() {
        return schemaContext;
    }

    @Override
    public synchronized SchemaContext getSessionContext() {
        return schemaContext;
    }

    @Override
    public ListenerRegistration<SchemaContextListener> registerSchemaContextListener(
            final SchemaContextListener listener) {
        return listeners.register(listener);
    }

    @Override
    public synchronized SchemaContext getSchemaContext() {
        return schemaContext;
    }

    @Override
    public ClassToInstanceMap<DOMSchemaServiceExtension> getExtensions() {
        return ImmutableClassToInstanceMap.of();
    }

    public synchronized void changeSchema(final SchemaContext newContext) {
        schemaContext = newContext;
        for (ListenerRegistration<? extends SchemaContextListener> listener : listeners.getRegistrations()) {
            listener.getInstance().onGlobalContextUpdated(schemaContext);
        }
    }
}
