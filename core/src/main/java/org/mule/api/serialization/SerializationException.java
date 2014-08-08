/*
 * (c) 2003-2014 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */

package org.mule.api.serialization;

import org.mule.api.MuleRuntimeException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;

/**
 * Exception to signal an error during serialization/deserialization process
 * 
 * @since 3.6.0
 */
public class SerializationException extends MuleRuntimeException
{

    private static final long serialVersionUID = -2550225226351711742L;

    public SerializationException(String message, Throwable cause)
    {
        this(MessageFactory.createStaticMessage(message), cause);
    }

    public SerializationException(String message)
    {
        this(MessageFactory.createStaticMessage(message));
    }

    public SerializationException(Message message, Throwable cause)
    {
        super(message, cause);
    }

    public SerializationException(Message message)
    {
        super(message);
    }

}
