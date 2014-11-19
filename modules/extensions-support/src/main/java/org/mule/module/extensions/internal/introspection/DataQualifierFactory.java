/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extensions.internal.introspection;

import org.mule.extensions.introspection.api.DataQualifier;
import org.mule.extensions.introspection.api.ExtensionOperation;
import org.mule.util.Preconditions;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Factory pattern implementation that provides a
 * {@link org.mule.extensions.introspection.api.DataQualifier} for a given
 * {@link java.lang.Class}
 *
 * @since 3.7.0
 */
final class DataQualifierFactory
{

    private interface DataTypeQualifierEvaluator
    {

        boolean isEvaluatable(Class<?> c);

        DataQualifier evaluate(Class<?> c);
    }

    private static class DefaultQualifierEvaluator implements DataTypeQualifierEvaluator
    {

        private final Class<?>[] parentClasses;
        private final DataQualifier qualifier;

        private DefaultQualifierEvaluator(Class<?> parentClass, DataQualifier qualifier)
        {
            this(new Class[] {parentClass}, qualifier);
        }

        private DefaultQualifierEvaluator(Class<?>[] parentClasses, DataQualifier qualifier)
        {
            this.parentClasses = parentClasses;
            this.qualifier = qualifier;
        }

        @Override
        public DataQualifier evaluate(Class<?> c)
        {
            if (isEvaluatable(c))
            {
                return qualifier;
            }
            return null;
        }

        @Override
        public boolean isEvaluatable(Class<?> c)
        {
            for (Class<?> parentClass : parentClasses)
            {
                if (parentClass.isAssignableFrom(c))
                {
                    return true;
                }
            }
            return false;
        }
    }

    private static class EnumDataTypeQualifierEvaluator extends DefaultQualifierEvaluator
    {

        private EnumDataTypeQualifierEvaluator()
        {
            super(new Class<?>[] {Enumeration.class, Enum.class}, DataQualifier.ENUM);
        }

        @Override
        public boolean isEvaluatable(Class<?> c)
        {
            return c.isEnum() || super.isEvaluatable(c);
        }
    }

    private static class PojoTypeQualifierEvaluator extends DefaultQualifierEvaluator
    {

        private PojoTypeQualifierEvaluator()
        {
            super(Object.class, DataQualifier.POJO);
        }

        @Override
        public boolean isEvaluatable(Class<?> c)
        {
            return !c.isPrimitive();
        }
    }

    private static final DataTypeQualifierEvaluator VOID_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {void.class, Void.class}, DataQualifier.VOID);

    private static final DataTypeQualifierEvaluator BOOLEAN_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {boolean.class, Boolean.class}, DataQualifier.BOOLEAN);

    private static final DataTypeQualifierEvaluator STRING_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {String.class, char.class, Character.class}, DataQualifier.STRING);

    private static final DataTypeQualifierEvaluator INTEGER_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {int.class, short.class, Integer.class, Short.class}, DataQualifier.INTEGER);

    private static final DataTypeQualifierEvaluator DOUBLE_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {double.class, float.class, Double.class, Float.class}, DataQualifier.DOUBLE);

    private static final DataTypeQualifierEvaluator LONG_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {long.class, Long.class}, DataQualifier.LONG);

    private static final DataTypeQualifierEvaluator DECIMAL_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {BigDecimal.class, BigInteger.class}, DataQualifier.DECIMAL);

    private static final DataTypeQualifierEvaluator BYTE_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {byte.class, Byte.class}, DataQualifier.BYTE);

    private static final DataTypeQualifierEvaluator DATE_TIME_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {Calendar.class, XMLGregorianCalendar.class, java.sql.Time.class,
                    java.sql.Timestamp.class}, DataQualifier.DATE_TIME);

    private static final DataTypeQualifierEvaluator DATE_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {Date.class, java.sql.Date.class}, DataQualifier.DATE);

    private static final DataTypeQualifierEvaluator STREAM_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {InputStream.class, OutputStream.class, Reader.class, Writer.class}, DataQualifier.STREAM);

    private static final DataTypeQualifierEvaluator ENUM_EVALUATOR = new EnumDataTypeQualifierEvaluator();

    private static final DataTypeQualifierEvaluator LIST_EVALUATOR = new DefaultQualifierEvaluator(
            new Class[] {List.class, Set.class, Object[].class}, DataQualifier.LIST);

    private static final DataTypeQualifierEvaluator MAP_EVALUATOR = new DefaultQualifierEvaluator(
            Map.class, DataQualifier.MAP);

    private static final DataTypeQualifierEvaluator OPERATION_EVALUATOR = new DefaultQualifierEvaluator(
            ExtensionOperation.class, DataQualifier.OPERATION);

    private static final DataTypeQualifierEvaluator POJO_EVALUATOR = new PojoTypeQualifierEvaluator();

    private static final DataTypeQualifierEvaluator[] evaluators = new DataTypeQualifierEvaluator[] {
            VOID_EVALUATOR,
            BOOLEAN_EVALUATOR,
            STRING_EVALUATOR,
            INTEGER_EVALUATOR,
            DOUBLE_EVALUATOR,
            LONG_EVALUATOR,
            DECIMAL_EVALUATOR,
            BYTE_EVALUATOR,
            DATE_TIME_EVALUATOR,
            DATE_EVALUATOR,
            STREAM_EVALUATOR,
            ENUM_EVALUATOR,
            LIST_EVALUATOR,
            MAP_EVALUATOR,
            OPERATION_EVALUATOR,
            POJO_EVALUATOR
    };

    /**
     * Returns a {@link org.mule.extensions.introspection.api.DataQualifier}
     * that corresponds to the given {@link java.lang.Class}
     *
     * @param clazz a not {@code null} {@link java.lang.Class}
     * @return a not {@code null} {@link org.mule.extensions.introspection.api.DataQualifier}
     * @throws java.lang.IllegalArgumentException if the argument is {@code null}
     * @throws java.lang.IllegalArgumentException If no qualifier can be assigned to the given type
     */
    public static DataQualifier getQualifier(Class<?> clazz)
    {
        Preconditions.checkArgument(clazz != null, "Can't get qualifier for a null class");

        for (DataTypeQualifierEvaluator evaluator : evaluators)
        {
            DataQualifier qualifier = evaluator.evaluate(clazz);
            if (qualifier != null)
            {
                return qualifier;
            }
        }

        throw new IllegalArgumentException(String.format("Data Qualifier for class %s could not be found.", clazz.getName()));
    }
}
