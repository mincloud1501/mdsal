/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.mdsal.binding.generator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.opendaylight.mdsal.binding.generator.impl.SupportTestUtil.containsAttributes;
import static org.opendaylight.mdsal.binding.generator.impl.SupportTestUtil.containsMethods;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opendaylight.mdsal.binding.generator.api.BindingGenerator;
import org.opendaylight.mdsal.binding.model.api.GeneratedProperty;
import org.opendaylight.mdsal.binding.model.api.GeneratedTransferObject;
import org.opendaylight.mdsal.binding.model.api.GeneratedType;
import org.opendaylight.mdsal.binding.model.api.Type;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.test.util.YangParserTestUtils;

public class BitAndUnionTOEnclosingTest {

    private static List<Type> genTypes = null;
    private static GeneratedType parentContainer = null;

    @BeforeClass
    public static void loadTestResources() {
        final SchemaContext context = YangParserTestUtils.parseYangResource("/bit_and_union.yang");

        assertNotNull(context);
        final BindingGenerator bindingGen = new BindingGeneratorImpl();
        genTypes = bindingGen.generateTypes(context);

        for (Type type : genTypes) {
            if (type instanceof GeneratedType) {
                GeneratedType genType = (GeneratedType) type;
                if (genType.getName().equals("ParentContainer") && !(genType instanceof GeneratedTransferObject)) {
                    parentContainer = genType;
                }
            }
        }
    }

    @Test
    public void testNestedTypesInLeaf() {
        GeneratedTransferObject lfLeaf = null;
        int lfLeafCounter = 0;
        GeneratedTransferObject lf1Leaf = null;
        int lf1LeafCounter = 0;
        GeneratedTransferObject lf2Leaf = null;
        int lf2LeafCounter = 0;
        List<GeneratedType> enclosedTypes = parentContainer.getEnclosedTypes();
        for (GeneratedType genType : enclosedTypes) {
            if (genType instanceof GeneratedTransferObject) {
                if (genType.getName().equals("Lf")) {
                    lfLeaf = (GeneratedTransferObject) genType;
                    lfLeafCounter++;
                } else if (genType.getName().equals("Lf$1")) {
                    lf1Leaf = (GeneratedTransferObject) genType;
                    lf1LeafCounter++;
                } else if (genType.getName().equals("Lf$2")) {
                    lf2Leaf = (GeneratedTransferObject) genType;
                    lf2LeafCounter++;
                }

            }
        }

        // nested types in leaf, contains Lf?
        assertNotNull("Lf TO wasn't found.", lfLeaf);
        assertEquals("Lf TO has incorrect number of occurences.", 1, lfLeafCounter);
        assertEquals("Lf has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626.ParentContainer",
                lfLeaf.getIdentifier().immediatelyEnclosingClass().get().toString());

        assertEquals("Lf generated TO has incorrect number of properties", 2, lfLeaf.getProperties().size());
        containsAttributes(lfLeaf, true, true, true, new NameTypePattern("string", "String"));
        containsAttributes(lfLeaf, true, false, true, new NameTypePattern("lf$1", "Lf$1"));

        // nested types in leaf, contains Lf1?
        assertNotNull("Lf$1 TO wasn't found.", lf1Leaf);
        assertEquals("Lf$1 TO has incorrect number of occurences.", 1, lf1LeafCounter);
        assertEquals("Lf$1 has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626.ParentContainer",
                lf1Leaf.getIdentifier().immediatelyEnclosingClass().get().toString());

        assertEquals("Lf generated TO has incorrect number of properties", 4, lf1Leaf.getProperties().size());
        containsAttributes(lf1Leaf, true, true, true, new NameTypePattern("uint32", "Long"));
        containsAttributes(lf1Leaf, true, true, true, new NameTypePattern("int8", "Byte"));
        containsAttributes(lf1Leaf, true, true, true, new NameTypePattern("string", "String"));
        containsAttributes(lf1Leaf, true, false, true, new NameTypePattern("lf$2", "Lf$2"));

        // nested types in leaf, contains Lf2?
        assertNotNull("Lf$2 TO wasn't found.", lf2Leaf);
        assertEquals("Lf$2 TO has incorrect number of occurences.", 1, lf2LeafCounter);
        assertEquals("Lf$2 has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626.ParentContainer",
                lf2Leaf.getIdentifier().immediatelyEnclosingClass().get().toString());

        assertEquals("Lf generated TO has incorrect number of properties", 2, lf2Leaf.getProperties().size());
        containsAttributes(lf2Leaf, true, true, true, new NameTypePattern("string", "String"));
        containsAttributes(lf2Leaf, true, true, true, new NameTypePattern("uint64", "BigInteger"));
    }

    @Test
    public void testNestedTypesInTypedef() {

        GeneratedTransferObject typeUnionTypedef = null;
        int typeUnionTypedefCounter = 0;

        for (Type type : genTypes) {
            if (type instanceof GeneratedType) {
                GeneratedType genType = (GeneratedType) type;
                if (genType.getName().equals("TypeUnion") && genType instanceof GeneratedTransferObject) {
                    typeUnionTypedef = (GeneratedTransferObject) genType;
                    typeUnionTypedefCounter++;
                }
            }
        }

        assertNotNull("TypeUnion TO wasn't found.", typeUnionTypedef);
        assertEquals("TypeUnion TO has incorrect number of occurences.", 1, typeUnionTypedefCounter);

        assertNotNull("TypeUnion TO wasn't found.", typeUnionTypedef);
        assertEquals("TypeUnion TO has incorrect number of occurences.", 1, typeUnionTypedefCounter);
        assertEquals("TypeUnion has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626", typeUnionTypedef.getPackageName());

        assertEquals("TypeUnion generated TO has incorrect number of properties", 2, typeUnionTypedef.getProperties()
                .size());
        containsAttributes(typeUnionTypedef, true, true, true, new NameTypePattern("string", "String"));
        containsAttributes(typeUnionTypedef, true, false, true, new NameTypePattern("typeUnion$1", "TypeUnion$1"));

        List<GeneratedType> nestedUnions = typeUnionTypedef.getEnclosedTypes();
        assertEquals("Incorrect number of nested unions", 2, nestedUnions.size());

        GeneratedTransferObject typeUnion1 = null;
        int typeUnion1Counter = 0;
        GeneratedTransferObject typeUnion2 = null;
        int typeUnion2Counter = 0;
        for (GeneratedType genType : nestedUnions) {
            if (genType instanceof GeneratedTransferObject) {
                if (genType.getName().equals("TypeUnion$1")) {
                    typeUnion1 = (GeneratedTransferObject) genType;
                    typeUnion1Counter++;
                } else if (genType.getName().equals("TypeUnion$2")) {
                    typeUnion2 = (GeneratedTransferObject) genType;
                    typeUnion2Counter++;
                }
            }
        }

        assertNotNull("TypeUnion$1 TO wasn't found.", typeUnion1);
        assertEquals("TypeUnion$1 TO has incorrect number of occurences.", 1, typeUnion1Counter);

        assertEquals("TypeUnion$1 has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626", typeUnion1.getPackageName());

        assertEquals("TypeUnion1 generated TO has incorrect number of properties", 4,
            typeUnion1.getProperties().size());

        containsAttributes(typeUnion1, true, true, true, new NameTypePattern("uint32", "Long"));
        containsAttributes(typeUnion1, true, true, true, new NameTypePattern("int8", "Byte"));
        containsAttributes(typeUnion1, true, true, true, new NameTypePattern("string", "String"));
        containsAttributes(typeUnion1, true, false, true, new NameTypePattern("typeUnion$2", "TypeUnion$2"));

        assertNotNull("TypeUnion$2 TO wasn't found.", typeUnion2);
        assertEquals("TypeUnion$2 TO has incorrect number of occurences.", 1, typeUnion2Counter);

        assertEquals("TypeUnion$2 has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626", typeUnion2.getPackageName());

        assertEquals("TypeUnion2 generated TO has incorrect number of properties", 2,
            typeUnion2.getProperties().size());
        containsAttributes(typeUnion2, true, true, true, new NameTypePattern("string", "String"));
        containsAttributes(typeUnion2, true, true, true, new NameTypePattern("uint64", "BigInteger"));

    }

    @Test
    public void bitAndUnionEnclosingTest() {

        assertNotNull("Parent container object wasn't found.", parentContainer);
        containsMethods(parentContainer, new NameTypePattern("getLf", "Lf"));

        GeneratedTransferObject bitLeaf = null;
        GeneratedTransferObject unionLeaf = null;
        List<GeneratedType> enclosedTypes = parentContainer.getEnclosedTypes();
        for (GeneratedType genType : enclosedTypes) {
            if (genType instanceof GeneratedTransferObject) {
                if (genType.getName().equals("BitLeaf")) {
                    bitLeaf = (GeneratedTransferObject) genType;
                } else if (genType.getName().equals("UnionLeaf")) {
                    unionLeaf = (GeneratedTransferObject) genType;
                }
            }
        }

        assertNotNull("BitLeaf TO wasn't found.", bitLeaf);
        assertNotNull("UnionLeaf TO wasn't found.", unionLeaf);

        assertEquals("BitLeaf has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626.ParentContainer",
                bitLeaf.getIdentifier().immediatelyEnclosingClass().get().toString());
        assertEquals("UnionLeaf has incorrect package name.",
                "org.opendaylight.yang.gen.v1.urn.bit.union.in.leaf.rev130626.ParentContainer",
                unionLeaf.getIdentifier().immediatelyEnclosingClass().get().toString());

        List<GeneratedProperty> propertiesBitLeaf = bitLeaf.getProperties();
        GeneratedProperty firstBitProperty = null;
        GeneratedProperty secondBitProperty = null;
        GeneratedProperty thirdBitProperty = null;

        for (GeneratedProperty genProperty : propertiesBitLeaf) {
            if (genProperty.getName().equals("firstBit")) {
                firstBitProperty = genProperty;
            } else if (genProperty.getName().equals("secondBit")) {
                secondBitProperty = genProperty;
            } else if (genProperty.getName().equals("thirdBit")) {
                thirdBitProperty = genProperty;
            }
        }

        assertNotNull("firstBit property wasn't found", firstBitProperty);
        assertNotNull("secondBit property wasn't found", secondBitProperty);
        assertNotNull("thirdBit property wasn't found", thirdBitProperty);

        assertEquals("firstBit property has incorrect type", "Boolean", firstBitProperty.getReturnType().getName());
        assertEquals("secondBit property has incorrect type", "Boolean", secondBitProperty.getReturnType().getName());
        assertEquals("thirdBit property has incorrect type", "Boolean", thirdBitProperty.getReturnType().getName());

        GeneratedProperty uint32Property = null;
        GeneratedProperty stringProperty = null;
        GeneratedProperty uint8Property = null;
        List<GeneratedProperty> propertiesUnionLeaf = unionLeaf.getProperties();
        for (GeneratedProperty genProperty : propertiesUnionLeaf) {
            if (genProperty.getName().equals("int32")) {
                uint32Property = genProperty;
            } else if (genProperty.getName().equals("string")) {
                stringProperty = genProperty;
            } else if (genProperty.getName().equals("uint8")) {
                uint8Property = genProperty;
            }
        }

        assertNotNull("uint32 property wasn't found", uint32Property);
        assertNotNull("string property wasn't found", stringProperty);
        assertNotNull("uint8 property wasn't found", uint8Property);

        assertEquals("uint32 property has incorrect type", "Integer", uint32Property.getReturnType().getName());
        assertEquals("string property has incorrect type", "String", stringProperty.getReturnType().getName());
        assertEquals("uint8 property has incorrect type", "Short", uint8Property.getReturnType().getName());

    }

}
