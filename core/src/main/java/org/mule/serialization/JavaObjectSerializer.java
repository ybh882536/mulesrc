/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */

package org.mule.serialization;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.api.serialization.SerializationException;
import org.mule.util.SerializationUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Implementation of {@link org.mule.api.serialization.ObjectSerializer} that uses Java's default serialization
 * mechanism. This means that exceptions will come from serializing objects that do
 * not implement {@link Serializable}
 *
 * @since 3.5.0
 */
public class JavaObjectSerializer extends AbstractObjectSerializer
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] doSerialize(Object object) throws Exception
    {
        if (object != null && !(object instanceof Serializable))
        {
            throw new SerializationException(String.format(
                    "Was expecting a Serializable type. %s was found instead", object.getClass()
                            .getName()));
        }

        return SerializationUtils.serialize((Serializable) object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> T doDeserialize(InputStream inputStream, ClassLoader classLoader)
    {
        checkArgument(inputStream != null, "Cannot deserialize a null stream");
        checkArgument(classLoader != null, "Cannot deserialize with a null classloader");

        return (T) SerializationUtils.deserialize(inputStream, classLoader, muleContext);
    }

    @Override
    protected <T> T postInitialize(T object)
    {
        //does nothing since SerializationUtils already does this on its own
        return object;
    }
}
