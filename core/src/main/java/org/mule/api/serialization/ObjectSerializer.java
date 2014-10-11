/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.serialization;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Defines a component capable to serialize/deserialize objects into/from an array of
 * {@link byte}s. Unlike usual serializing components, this one doesn't enforce the
 * serialized object to implement {@link Serializable}. However, some implementations
 * might require that condition and throw {@link IllegalArgumentException} if not
 * met.
 * <p/>
 * Implementations are also responsible for the correct initialization of classes
 * implementing the {@link org.mule.util.store.DeserializationPostInitialisable}
 * interface
 *
 * @since 3.6.0
 */
public interface ObjectSerializer
{

    /**
     * Serealizes the given object into a an array of {@link byte}s
     *
     * @param object the object to be serialized. Might be <code>null</code>
     * @return an array of {@link byte}
     * @throws SerializationException in case of unexpected exception
     */
    public byte[] serialize(Object object);

    public void serialize(Object object, OutputStream out);

    /**
     * Deserializes the given bytes. Unexpected behavior can result of deserializing
     * a byte[] that was generated with another implementation.
     * Implementation will choose the {@link java.lang.ClassLoader}
     * to use for deserialization.
     *
     * @param bytes an array of byte that an original object was serialized into
     * @return the deserialized object
     * @throws IllegalArgumentException if {@code bytes} is {@code null}
     * @throws SerializationException   in case of unexpected exception
     */
    public <T> T deserialize(byte[] bytes);

    /**
     * Deserializes the given bytes. Unexpected behavior can result of deserializing
     * a byte[] that was generated with another implementation.
     *
     * @param bytes       an array of byte that an original object was serialized into
     * @param classLoader the {@link java.lang.ClassLoader} to deserialize with
     * @return the deserialized object
     * @throws IllegalArgumentException if {@code bytes} is {@code null}
     * @throws SerializationException   in case of unexpected exception
     */
    public <T> T deserialize(byte[] bytes, ClassLoader classLoader);

    /**
     * Deserializes the given stream of bytes. Unexpected behavior can result of deserializing
     * a stream that was generated with another implementation.
     * Implementation will choose the {@link java.lang.ClassLoader}
     * to use for deserialization.
     * <p/>
     * Even if deserialization fails, this method will close the
     * {@code inputStream}
     *
     * @param inputStream a stream of bytes that an original object was serialized into
     * @return the deserialized object
     * @throws IllegalArgumentException if {@code inputStream} is {@code null}
     * @throws SerializationException   in case of unexpected exception
     */
    public <T> T deserialize(InputStream inputStream);

    /**
     * Deserializes the given stream of bytes. Unexpected behavior can result of deserializing
     * a stream that was generated with another implementation.
     * <p/>
     * Even if deserialization fails, this method will close the
     * {@code inputStream}
     *
     * @param inputStream a stream of bytes that an original object was serialized into
     * @param classLoader the {@link java.lang.ClassLoader} to deserialize with
     * @return the deserialized object
     * @throws IllegalArgumentException if {@code inputStream} is {@code null}
     * @throws SerializationException   in case of unexpected exception
     */
    public <T> T deserialize(InputStream inputStream, ClassLoader classLoader);
}
