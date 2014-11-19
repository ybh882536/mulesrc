/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.spi;

import org.mule.extensions.introspection.Extension;
import org.mule.extensions.introspection.ExtensionBuilder;
import org.mule.extensions.introspection.ExtensionDescriber;
import org.mule.extensions.introspection.ExtensionDescribingContext;

/**
 * A post processor that allows doing extra task before a
 * {@link ExtensionDescriber} finishes
 * describing a {@link Extension}
 * <p/>
 * This allows to customize the discovery process by given a hooking point
 * for manipulating the {@link ExtensionBuilder} before
 * it actually generates the final {@link Extension} instance.
 * <p/>
 * Instances are to be discovered by standard SPI mechanism
 *
 * @since 1.0
 */
public interface ExtensionDescriberPostProcessor
{

    /**
     * Optionally applies extra logic into any of the {@code context}'s
     * attributes
     *
     * @param context the current {@link ExtensionDescribingContext}
     */
    void postProcess(ExtensionDescribingContext context);

}
