/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.exception;

import java.util.Collections;
import java.util.List;

import org.mule.api.MuleEvent;
import org.mule.api.MuleRuntimeException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.exception.MessageExceptionHandlerAcceptor;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.config.i18n.CoreMessages;
import org.mule.message.DefaultExceptionPayload;
import org.mule.processor.AbstractMuleObjectOwner;

/**
 * Selects which exception strategy to execute based on filtering.
 *
 * Exception listeners must implement {@link org.mule.api.exception.MessageExceptionHandlerAcceptor} to be part of ChoiceMessagingExceptionStrategy
 */
public class ChoiceMessagingExceptionStrategy extends AbstractMuleObjectOwner<MessageExceptionHandlerAcceptor> implements MessagingExceptionHandler, MuleContextAware, Lifecycle
{
    private List<MessageExceptionHandlerAcceptor> exceptionListeners;

    @Override
    public MuleEvent handleException(Exception exception, MuleEvent event)
    {
        event.getMessage().setExceptionPayload(new DefaultExceptionPayload(exception));
        for (MessageExceptionHandlerAcceptor exceptionListener : exceptionListeners)
        {
            if (exceptionListener.accept(event))
            {
                event.getMessage().setExceptionPayload(null);
                return exceptionListener.handleException(exception,event);
            }
        }
        throw new MuleRuntimeException(CoreMessages.createStaticMessage("Default exception strategy must accept any event."));
    }

    public void setExceptionListeners(List<MessageExceptionHandlerAcceptor> exceptionListeners)
    {
        this.exceptionListeners = exceptionListeners;
    }

    @Override
    public void initialise() throws InitialisationException
    {
        addDefaultExceptionStrategyIfRequired();
        super.initialise();
        validateConfiguredExceptionStrategies();
    }

    private void addDefaultExceptionStrategyIfRequired()
    {
        if (!exceptionListeners.get(exceptionListeners.size()-1).acceptsAll())
        {
            this.exceptionListeners.add(new MessageExceptionStrategyAcceptorDelegate(getMuleContext().getDefaultExceptionStrategy()));
        }
    }

    @Override
    protected List<MessageExceptionHandlerAcceptor> getOwnedObjects() {
        return Collections.unmodifiableList(exceptionListeners);
    }

    private void validateConfiguredExceptionStrategies()
    {
        for (int i = 0; i < exceptionListeners.size()-1; i++)
        {
             MessageExceptionHandlerAcceptor messageExceptionHandlerAcceptor = exceptionListeners.get(i);
             if (messageExceptionHandlerAcceptor.acceptsAll())
             {
                 throw new MuleRuntimeException(CoreMessages.createStaticMessage("Only last exception strategy inside <choice-exception-strategy> can accept any message. Maybe expression attribute is empty."));
             }
        }
    }

}
