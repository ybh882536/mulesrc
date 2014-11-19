/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.extensions.introspection.Capable;
import org.mule.extensions.introspection.Extension;
import org.mule.extensions.introspection.declaration.Construct;
import org.mule.extensions.introspection.declaration.DeclarationConstruct;
import org.mule.extensions.introspection.declaration.HasCapabilities;
import org.mule.module.extensions.CapabilityExtractor;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Utility class that extracts all the capabilities in a given extension and registers it on a builder
 * To do this, it relies on {@link CapabilityExtractor} that are obtained via SPI
 *
 * @since 3.7.0
 */
public final class CapabilitiesResolver
{

    /**
     * Resolves the capabilities present in {@code extensionType} and registers them in
     * {@code builder}
     *
     * @param declaration     a {@link DeclarationConstruct} describing the {@link Extension} to be built
     * @param capableType     the type of the {@link Capable} class
     * @param capableCallback a {@link HasCapabilities} on which the {@link Capable} is to be registered.
     * @throws java.lang.IllegalArgumentException if any argument is {@code null}
     */
    public void resolveCapabilities(DeclarationConstruct declaration, Class<?> capableType, HasCapabilities<? extends Construct> capableCallback)
    {
        checkArgument(declaration != null, "declaration construct cannot be null");
        checkArgument(capableType != null, "capable type cannot be null");
        checkArgument(capableCallback != null, "capable callback cannot be null");

        for (CapabilityExtractor extractor : getExtractors())
        {
            extractor.extractCapability(declaration, capableType, capableCallback);
        }
    }

    private List<CapabilityExtractor> getExtractors()
    {
        return ImmutableList.copyOf(ServiceLoader.load(CapabilityExtractor.class, getClass().getClassLoader()));
    }

}
