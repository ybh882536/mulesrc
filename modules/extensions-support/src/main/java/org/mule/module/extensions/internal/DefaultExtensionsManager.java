/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal;

import org.mule.common.MuleVersion;
import org.mule.api.registry.SPIServiceRegistry;
import org.mule.api.registry.ServiceRegistry;
import org.mule.extensions.ExtensionsManager;
import org.mule.extensions.introspection.Extension;
import org.mule.module.extensions.internal.introspection.DefaultExtensionFactory;
import org.mule.module.extensions.internal.introspection.ExtensionDiscoverer;
import org.mule.util.Preconditions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ExtensionsManager}
 *
 * @since 3.7.0
 */
public final class DefaultExtensionsManager implements ExtensionsManager
{

    private static final Logger logger = LoggerFactory.getLogger(DefaultExtensionsManager.class);

    private final Map<String, Extension> extensions = Collections.synchronizedMap(new LinkedHashMap<String, Extension>());
    private final ServiceRegistry serviceRegistry = new SPIServiceRegistry();
    private ExtensionDiscoverer extensionDiscoverer = new DefaultExtensionDiscoverer(new DefaultExtensionFactory(serviceRegistry), serviceRegistry);

    @Override
    public List<Extension> discoverExtensions(ClassLoader classLoader)
    {
        logger.info("Starting discovery of extensions");

        List<Extension> discovered = extensionDiscoverer.discover(classLoader);
        logger.info("Discovered {} extensions", discovered.size());

        ImmutableList.Builder<Extension> accepted = ImmutableList.builder();

        for (Extension extension : discovered)
        {
            final String extensionName = extension.getName();

            if (extensions.containsKey(extensionName))
            {
                if (maybeUpdateExtension(extension, extensionName))
                {
                    accepted.add(extension);
                }
            }
            else
            {
                registerExtension(extension);
                accepted.add(extension);
            }
        }

        return accepted.build();
    }

    private void registerExtension(Extension extension)
    {
        logger.info("Registering extension (version {})", extension.getName(), extension.getVersion());
        extensions.put(extension.getName(), extension);
    }

    private boolean maybeUpdateExtension(Extension extension, String extensionName)
    {
        Extension actual = extensions.get(extensionName);
        MuleVersion newVersion;
        try
        {
            newVersion = new MuleVersion(extension.getVersion());
        }
        catch (IllegalArgumentException e)
        {
            logger.warn(
                    String.format("Found extensions %s with invalid version %s. Skipping registration",
                                  extension.getName(), extension.getVersion()), e);

            return false;
        }

        if (newVersion.newerThan(actual.getVersion()))
        {
            logExtensionHotUpdate(extension, actual);
            registerExtension(extension);

            return true;
        }
        else
        {
            logger.info("Found extension {} but version {} was already registered. Keeping existing definition",
                        extension.getName(),
                        extension.getVersion());

            return false;
        }
    }

    private void logExtensionHotUpdate(Extension extension, Extension actual)
    {
        if (logger.isInfoEnabled())
        {
            logger.info(String.format(
                    "Found extension %s which was already registered with version %s. New version %s " +
                    "was found. Hot updating extension definition",
                    extension.getName(),
                    actual.getVersion(),
                    extension.getVersion()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Extension> getExtensions()
    {
        return ImmutableSet.copyOf(extensions.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Extension> getExtensionsCapableOf(Class<?> capabilityType)
    {
        Preconditions.checkArgument(capabilityType != null, "capability type cannot be null");
        ImmutableSet.Builder<Extension> capables = ImmutableSet.builder();
        for (Extension extension : getExtensions())
        {
            if (extension.isCapableOf(capabilityType))
            {
                capables.add(extension);
            }
        }

        return capables.build();
    }

    protected void setExtensionsDiscoverer(ExtensionDiscoverer discoverer)
    {
        extensionDiscoverer = discoverer;
    }
}
