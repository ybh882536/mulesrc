/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */

package org.mule.api.serialization;

import org.mule.api.context.MuleContextAware;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Defines a component capable to serialize/deserealize objets into/from an arra of
 * {@link byte}s. Unlike usual serializing components, this one doesn't enforce the
 * serialized object to implement {@link Serializable}. However, some implementations
 * might require that condition and throw {@link IllegalArgumentException} if not
 * met.
 *
 * @since 3.6.0
 */
public interface ObjectSerializer extends MuleContextAware
{

    /**
     * Serealizes the given object into a an array of {@link byte}s
     *
     * @param object the object to be serialized. Might be <code>null</code>
     * @return an array of {@link byte}
     * @throws SerializationException in case of unexpected exception
     */
    public byte[] serialize(Object object);

    /**
     * Deserealizes the given bytes. Unexpected behavior can result of deserealizing
     * a byte[] that was generated with another implementation.
     *
     * @param bytes an array of byte that an original object was serialized into
     * @return the deserealized object
     * @throws IllegalArgumentException if {@code bytes} is {@code null}
     * @throws SerializationException   in case of unexpected exception
     */
    public <T> T deserialize(byte[] bytes);

    /**
     * Deserealizes the given stream of bytes. Unexpected behavior can result of deserealizing
     * a stream that was generated with another implementation.
     *
     * @param inputStream a stream of bytes that an original object was serialized into
     * @return the deserealized object
     * @throws IllegalArgumentException if {@code inputStream} is {@code null}
     * @throws SerializationException   in case of unexpected exception
     */
    public <T> T deserialize(InputStream inputStream);
}
