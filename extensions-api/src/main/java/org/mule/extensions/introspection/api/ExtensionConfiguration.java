/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.api;

import java.util.List;

/**
 * A named configuration for an extension
 * <p/>
 * <p>
 * Configurations describe different ways to initialize a scope for operations.
 * Upon execution, each operation will be associated to a given configuration, so configurations define both
 * a set of shared properties used in operations, and a common context to relate operations.
 * </p>
 * <p>
 * For example, an extension that provides access to an external resource may provide
 * </p>
 * The configuration can also imply different implicit behaviors not strictly attached to the operations
 * (e.g.: A connector supporting both stateful connections an OAuth2 authentication. Depending on the
 * configuration used, the same connector will have different reconnection strategies).
 * <p/>
 * The configuration is also the place in which cross operation, extension level attributes are configured.
 * For each configuration attribute, a valid setter is expected to exist on the {@link #getDeclaringClass()}
 * <p/>
 * Every {@link Extension} is required to have at least one configuration.
 * That configuration is defined as the &quot;default configuration&quot;
 *
 * @since 1.0
 */
public interface ExtensionConfiguration extends Described
{

    /**
     * Returns the {@link ExtensionParameter}s
     * available for this configuration
     *
     * @return a immutable {@link java.util.List} with {@link ExtensionParameter}
     * instances. It might be empty but it will never be {@code null}
     */
    List<ExtensionParameter> getParameters();

    /**
     * The java which implements this configuration in runtime.
     * This class is expected to have a public, default constructor
     * and a valid setter for each parameter
     * in {@link #getParameters()}
     *
     * @return a not {@code null} {@link java.lang.Class}
     */
    Class<?> getDeclaringClass();

}
