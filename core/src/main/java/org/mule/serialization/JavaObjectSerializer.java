/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */

package org.mule.serialization;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.api.MuleContext;
import org.mule.api.serialization.ObjectSerializer;
import org.mule.api.serialization.SerializationException;
import org.mule.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Implementation of {@link org.mule.api.serialization.ObjectSerializer} that uses Java's default serialization
 * mechanism. This means that exceptions will come from serializing objects that do
 * not implement {@link Serializable}
 *
 * @since 3.5.0
 */
public class JavaObjectSerializer implements ObjectSerializer
{

    private MuleContext muleContext;

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if object is not a {@link Serializable}
     */
    @Override
    public byte[] serialize(Object object)
    {
        if (object != null && !(object instanceof Serializable))
        {
            throw new SerializationException(String.format(
                    "Was expecting a Serializable type. %s was found instead", object.getClass()
                            .getCanonicalName()));
        }

        try
        {
            return SerializationUtils.serialize((Serializable) object);
        }
        catch (Exception e)
        {
            throw new SerializationException("Could not serialize object", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(byte[] bytes)
    {
        checkArgument(bytes != null, "The byte[] must not be null");
        return deserialize(new ByteArrayInputStream(bytes));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(InputStream inputStream)
    {
        checkArgument(inputStream != null, "Cannot deserealize a null stream");
        try
        {
            return (T) SerializationUtils.deserialize(inputStream, muleContext);
        }
        catch (Exception e)
        {
            throw new SerializationException("Could not deserialize object", e);
        }
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        muleContext = context;
    }

}
