/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package org.mule.serialization;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.serialization.ObjectSerializer;
import org.mule.el.datetime.DateTime;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import org.junit.Test;

public abstract class AbstractObjectSerializerContractTestCase extends AbstractMuleContextTestCase
{

    protected ObjectSerializer serializer;

    @Test(expected = IllegalArgumentException.class)
    public final void nullBytes() throws Exception
    {
        serializer.deserialize((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void nullStream() throws Exception
    {
        serializer.deserialize((InputStream) null);
    }

    @Test
    public final void nullObject() throws Exception
    {
        byte[] bytes = serializer.serialize(null);
        Object object = serializer.deserialize(bytes);
        assertNull(object);
    }

    @Test
    public final void serializeWithoutDefaultConstructor() throws Exception
    {
        Calendar calendar = Calendar.getInstance();
        Locale locale = Locale.ITALIAN;

        DateTime dateTime = new DateTime(calendar, locale);
        dateTime.changeTimeZone("Pacific/Midway");

        MuleEvent event = getTestEvent(dateTime);
        byte[] bytes = serializer.serialize(event.getMessage());

        MuleMessage message = serializer.deserialize(bytes);
        DateTime deserealized = (DateTime) message.getPayload();

        assertEquals(calendar, deserealized.toCalendar());

        // test that the locale matches
        assertEquals(dateTime.format(), deserealized.format());
    }
}
