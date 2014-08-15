/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.serialization;

import org.mule.api.serialization.SerializationException;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import org.junit.Test;

public class JavaObjectSerializerTestCase extends AbstractObjectSerializerContractTestCase
{

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        JavaObjectSerializer serializer = new JavaObjectSerializer();
        serializer.setMuleContext(AbstractMuleContextTestCase.muleContext);

        this.serializer = serializer;
    }

    @Test(expected = SerializationException.class)
    public void notSerializable() throws Exception
    {
        serializer.serialize(new Object());
    }

}
