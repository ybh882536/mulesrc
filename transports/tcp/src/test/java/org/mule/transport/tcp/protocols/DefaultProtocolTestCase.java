/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.tcp.protocols;

import static org.junit.Assert.assertEquals;
import org.mule.api.MuleContext;
import org.mule.api.serialization.ObjectSerializer;
import org.mule.tck.SerializationTestUtils;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.transport.tcp.TcpProtocol;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

@SmallTest
public class DefaultProtocolTestCase extends AbstractMuleTestCase
{

    protected TcpProtocol protocol;
    protected int expectedLength;
    protected ObjectSerializer serializer;

    @Mock
    private MuleContext muleContext;


    @Before
    public void before()
    {
        this.serializer = SerializationTestUtils.getJavaSerializerWithMockContext();
        protocol = new DirectProtocol(serializer);
        expectedLength = SlowInputStream.FULL_LENGTH;
    }

    @Test
    public void testRead() throws Exception
    {
        byte[] result = (byte[]) protocol.read(new SlowInputStream());
        assertEquals(expectedLength, result.length);
    }

    protected TcpProtocol getProtocol()
    {
        return protocol;
    }

}
