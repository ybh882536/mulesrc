/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.resources.api;

import org.mule.api.config.ServiceRegistryAware;
import org.mule.extensions.introspection.api.Extension;

import java.util.List;

/**
 * A component capable of dinamically generating resources to back up
 * a set of {@link org.mule.extensions.introspection.api.Extension}s.
 * Although extensions resolve their functionality mainly in runtime,
 * some configuration resources such as XML schemas, service registration files,
 * spring bundles, or whatever resource the runtime requires need to be generated
 * in compile or run time.
 * <p/>
 * Additionally, those resources might reference more than one
 * {@link org.mule.extensions.introspection.api.Extension},
 * for example, if a module registers many extensions,
 * we need only one service registration file for all of them.
 * <p/>
 * This interface provides semantics to generate resources for many
 * {@link org.mule.extensions.introspection.api.Extension}s which might share the
 * generated resources and when finished, dump all of the generated content to some persistent store.
 * <p/>
 * To do so, implementations will work in tandem with instances of
 * {@link org.mule.extensions.resources.spi.GenerableResourceContributor} which will be
 * obtained via a {@link org.mule.api.config.ServiceRegistry} provided through
 * {@link #setServiceRegistry(org.mule.api.config.ServiceRegistry)}. If no service registry
 * is provided then the platform will automatically choose an implementation.
 * <p/>
 * Implementations are to be assumed not thread-safe and to be used in a
 * fire and forget fashion
 *
 * @since 1.0
 */
public interface ResourcesGenerator extends ServiceRegistryAware
{

    /**
     * Returns a {@link org.mule.extensions.resources.api.GenerableResource} that
     * will point to the given {@code filepath}. If another extension already contributed
     * to that resource, then the same instance is returned. If no resource has been
     * contributed too yet, then it's created and registered.
     *
     * @param filepath the path in which the resource is to be generated
     * @return a {@link org.mule.extensions.resources.api.GenerableResource}
     */
    GenerableResource getOrCreateResource(String filepath);

    /**
     * Generates resources for the given {@code extension}. This doesn't mean that
     * the resource will be generated and written to disk. It means that
     * the discovered instances of {@link org.mule.extensions.resources.spi.GenerableResourceContributor}
     * will be asked to contribute resources for the given extension. The contributors
     * will use {@link #getOrCreateResource(String)} to get a hold of the resource in generation
     * and will contribute their part of the content. Nothing gets written to disk
     * until {@link #dumpAll()} is invoked
     *
     * @param extension a {@link org.mule.extensions.introspection.api.Extension}
     */
    void generateFor(Extension extension);

    /**
     * Writes all the {@link org.mule.extensions.resources.api.GenerableResource}s that were
     * generated through {@link #getOrCreateResource(String)} and writes them to a persistent store.
     * The details of where and how those files are written are completely up to the implementation.
     *
     * @return the list of {@link org.mule.extensions.resources.api.GenerableResource} that was written
     */
    List<GenerableResource> dumpAll();
}
