/*
 * Copyright (c) 2014 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.generator.util;

/**
 * Factory class for creating SourceCodeGenerator instances.
 *
 * @author Thomas Pantelis
 * @deprecated Code generation is a concert separate from type mapping and is an implementation detail.
 */
@Deprecated
public class SourceCodeGeneratorFactory {

    private static final String GENERATE_CODEC_SOURCE_PROP = "org.opendaylight.yangtools.sal.generateCodecSource";

    private static final SourceCodeGenerator NULL_GENERATOR = new NullSourceCodeGenerator();

    /**
     * Gets a SourceCodeGenerator instance.
     *
     * <p>
     * Generation of source code is controlled by the <i>org.opendaylight.yangtools.sal.generateCodecSource</i>
     * system property. If set to true, a DefaultSourceCodeGenerator instance is returned, otherwise a
     * NullSourceCodeGenerator is returned.
     *
     * @param generatedSourceDir the directory in which to put generated source files. If null,
     *     a default is used (see DefaultSourceCodeGenerator).
     */
    public SourceCodeGenerator getInstance(final String generatedSourceDir) {
        boolean generateSource = Boolean.getBoolean(GENERATE_CODEC_SOURCE_PROP);
        if (generateSource) {
            return new DefaultSourceCodeGenerator(generatedSourceDir);
        }

        return NULL_GENERATOR;
    }
}
