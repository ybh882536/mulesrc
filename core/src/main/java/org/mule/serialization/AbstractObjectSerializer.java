/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.serialization;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.serialization.ObjectSerializer;
import org.mule.api.serialization.SerializationException;
import org.mule.util.IOUtils;
import org.mule.util.store.DeserializationPostInitialisable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Base class for implementations of {@link org.mule.api.serialization.ObjectSerializer}
 * This class implements all the base behavioral contract allowing its extensions to
 * only care about the actual serialization/deserialization part.
 *
 * @since 3.6.0
 */
public abstract class AbstractObjectSerializer implements ObjectSerializer, MuleContextAware
{

    protected MuleContext muleContext;

    /**
     * Serializes the given object. Should not care about error handling
     *
     * @param object the object to be serialized
     * @return an array of bytes
     * @throws Exception any exception thrown. Base class will handle accordingly
     */
    protected abstract byte[] doSerialize(Object object) throws Exception;

    /**
     * Deserializes the given {@code inputStream} using the provided {@code classLoader}.
     * No need to worry about error handling or deserialization post initialization. Base class
     * does all of that automatically
     *
     * @param inputStream an open {@link java.io.InputStream}, not to be explicitly closed in this method
     * @param classLoader a {@link java.lang.ClassLoader}
     * @return a deserialized object
     */
    protected abstract <T> T doDeserialize(InputStream inputStream, ClassLoader classLoader);

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if object is not a {@link java.io.Serializable}
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
            return doSerialize(object);
        }
        catch (Exception e)
        {
            throw new SerializationException("Could not serialize object", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if object is not a {@link java.io.Serializable}
     */
    @Override
    public void serialize(Object object, OutputStream out)
    {
        try
        {
            byte[] bytes = serialize(object);
            out.write(bytes);
            out.flush();
        }
        catch (IOException e)
        {
            throw new SerializationException("Could not write to output stream", e);
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(byte[] bytes)
    {
        return deserialize(bytes, muleContext.getExecutionClassLoader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(byte[] bytes, ClassLoader classLoader)
    {
        checkArgument(bytes != null, "The byte[] must not be null");
        return deserialize(new ByteArrayInputStream(bytes), classLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(InputStream inputStream)
    {
        return deserialize(inputStream, muleContext.getExecutionClassLoader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T deserialize(InputStream inputStream, ClassLoader classLoader)
    {
        checkArgument(inputStream != null, "Cannot deserialize a null stream");
        checkArgument(classLoader != null, "Cannot deserialize with a null classloader");
        try
        {
            return (T) postInitialize(doDeserialize(inputStream, classLoader));
        }
        catch (Exception e)
        {
            throw new SerializationException("Could not deserialize object", e);
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }

    protected <T> T postInitialize(T object)
    {
        if (object instanceof DeserializationPostInitialisable)
        {
            try
            {
                DeserializationPostInitialisable.Implementation.init(object, muleContext);
            }
            catch (Exception e)
            {
                throw new SerializationException(String.format(
                        "Could not initialize instance of %s after deserialization", object.getClass()
                                .getName()), e);
            }
        }

        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setMuleContext(MuleContext context)
    {
        muleContext = context;
    }

}
