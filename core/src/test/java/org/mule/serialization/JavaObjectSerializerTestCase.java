/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.serialization;

import org.mule.api.serialization.SerializationException;
import org.mule.tck.SerializationTestUtils;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import org.junit.Test;

public class JavaObjectSerializerTestCase extends AbstractObjectSerializerContractTestCase
{

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        serializer = SerializationTestUtils.getJavaSerializer(AbstractMuleContextTestCase.muleContext);
    }

    @Test(expected = SerializationException.class)
    public void notSerializable() throws Exception
    {
        serializer.serialize(new Object());
    }

}
