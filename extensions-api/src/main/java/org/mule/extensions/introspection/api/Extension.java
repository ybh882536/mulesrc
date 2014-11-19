/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.api;

import java.util.List;

/**
 * An Extension that provides packaged functionality.
 * <p>
 * Extensions can augment a system by providing new features in the form of operations.
 * What makes an extension different from a class implementing a certain interface is the fact that extensions
 * provide enough information at runtime so that they can be used without prior knowledge, and can be both executed
 * or integrated into tooling seamlessly.
 * </p>
 * <p>
 * An extension is not a miscellaneous group of methods, but can be seen (and may be derived from) an object model.
 * As such, an extension will provide several ways to configure itself (akin to providing different constructors), and
 * will provide a set of operations that may be eventually executed.
 * </p>
 * <p>
 * The extension model doesn't just map a JVM object model. Extensions provide richer metadata, and a dynamic
 * execution model, but more importantly they restrict the way operations are defined and used to a
 * manageable subset that would deterministic data flow analysis.
 * </p>
 * <p>
 * An extension doesn't define any predefined syntax, evaluation order or execution paradigm. The operations provided
 * are expected to be used as individual building blocks in a bigger system, hence the name <code>Extension</code>
 * </p>
 *
 * @since 1.0
 */
public interface Extension extends Described, Capable
{

    /**
     * A simple name for this extension. Usually one or two simple words that describes
     * the functionality. For example, for an extension that performs validations it could be
     * something like &quot;validation&quot;. For an ftp connector it could be &quot;ftp&quot;.
     * For a connector that accesses the google contacts API it could be &quot;google-contacts&quot;.
     * <p/>
     * To follow the convention described above is important since the platform might use
     * this name to auto generate resources based on it. This attribute will be used in a
     * convention over configuration pattern. It cannot contain spaces
     */
    String getName();

    /**
     * Returns this extension's version.
     * <p>
     * The extension version is specified as a <a href=&quot;http://semver.org/&quot;>Semantic Versioning</a>.
     * </p>
     * <p>
     * Note that while an extension implements a specific version, nothing prevents several versions of the same
     * extension to coexists at runtime.
     * </p>
     *
     * @return the version associated with this extension
     */
    String getVersion();

    /**
     * Returns the minimum mule version for which this extension is valid
     *
     * @return the version associated with the eldest mule runtime capable of using this extension
     */
    String getMinMuleVersion();

    /**
     * Returns the {@link ExtensionConfiguration}s
     * available for this extension. Each configuration is guaranteed to have a unique name.
     * <p>
     * There is always at least one configuration. The first configuration is the preferred (default) one,
     * the rest of the configurations are ordered alphabetically.
     * </p>
     *
     * @return an immutable {@link java.util.List} with the available {@link ExtensionConfiguration}s.
     */
    List<ExtensionConfiguration> getConfigurations();

    /**
     * Returns the {@link ExtensionConfiguration}
     * that matches the given name.
     *
     * @param name case sensitive configuration name
     * @return a {@link ExtensionConfiguration}
     * @throws NoSuchConfigurationException if no configuration available for that name
     */
    ExtensionConfiguration getConfiguration(String name) throws NoSuchConfigurationException;

    /**
     * Returns the {@link ExtensionOperation}s
     * available for this extension. Each operation is guaranteed to have a unique name, and that name
     * cannot be already in use by one of the available {@link #getConfigurations()}
     * <p>
     * There is always at least one operation, and operations will be sorted alphabetically.
     * </p>
     *
     * @return an immutable {@link java.util.List} of {@link ExtensionOperation}
     */
    List<ExtensionOperation> getOperations();

    /**
     * Returns the {@link ExtensionOperation} that matches
     * the given name.
     *
     * @param name case sensitive operation name
     * @return a {@link ExtensionOperation}
     * @throws NoSuchOperationException if no operation matches the given name
     */
    ExtensionOperation getOperation(String name) throws NoSuchOperationException;

    /**
     * The class on which this extension is defined
     *
     * @return a not {@code null} {@link java.lang.Class}
     */
    Class<?> getDeclaringClass();

}
