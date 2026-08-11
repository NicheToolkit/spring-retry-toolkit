/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.retry.util.test;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.util.Assert;

/**
 * <code>TestUtils</code>
 * <p>The test utils class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class TestUtils {

    /**
     * <code>getPropertyValue</code>
     * <p>The get property value getter method.</p>
     * @param root {@link java.lang.Object} <p>The root parameter is <code>Object</code> type.</p>
     * @param propertyPath {@link java.lang.String} <p>The property path parameter is <code>String</code> type.</p>
     * @see  java.lang.Object
     * @see  java.lang.String
     * @return  {@link java.lang.Object} <p>The get property value return object is <code>Object</code> type.</p>
     */
    public static Object getPropertyValue(Object root, String propertyPath) {
		Object value = null;
		DirectFieldAccessor accessor = new DirectFieldAccessor(root);
		String[] tokens = propertyPath.split("\\.");
		for (int i = 0; i < tokens.length; i++) {
			value = accessor.getPropertyValue(tokens[i]);
			if (value != null) {
				accessor = new DirectFieldAccessor(value);
			}
			else if (i == tokens.length - 1) {
				return null;
			}
			else {
				throw new IllegalArgumentException("intermediate property '" + tokens[i] + "' is null");
			}
		}
		return value;
	}

    /**
     * <code>getPropertyValue</code>
     * <p>The get property value getter method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param root {@link java.lang.Object} <p>The root parameter is <code>Object</code> type.</p>
     * @param propertyPath {@link java.lang.String} <p>The property path parameter is <code>String</code> type.</p>
     * @param type {@link java.lang.Class} <p>The type parameter is <code>Class</code> type.</p>
     * @see  java.lang.Object
     * @see  java.lang.String
     * @see  java.lang.Class
     * @see  java.lang.SuppressWarnings
     * @return  T <p>The get property value return object is <code>T</code> type.</p>
     */
    @SuppressWarnings("unchecked")
	public static <T> T getPropertyValue(Object root, String propertyPath, Class<T> type) {
		Object value = getPropertyValue(root, propertyPath);
		if (value != null) {
			Assert.isAssignable(type, value.getClass());
		}
		return (T) value;
	}

}
