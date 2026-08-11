/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.classify.util;

import java.lang.reflect.Method;

/**
 * <code>MethodResolver</code>
 * <p>The method resolver interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface MethodResolver {

    /**
     * <code>findMethod</code>
     * <p>The find method method.</p>
     * @param candidate {@link java.lang.Object} <p>The candidate parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @see  java.lang.reflect.Method
     * @see  java.lang.IllegalArgumentException
     * @return  {@link java.lang.reflect.Method} <p>The find method return object is <code>Method</code> type.</p>
     * @throws IllegalArgumentException {@link java.lang.IllegalArgumentException} <p>The illegal argument exception is <code>IllegalArgumentException</code> type.</p>
     */
    Method findMethod(Object candidate) throws IllegalArgumentException;

    /**
     * <code>findMethod</code>
     * <p>The find method method.</p>
     * @param clazz {@link java.lang.Class} <p>The clazz parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.reflect.Method
     * @return  {@link java.lang.reflect.Method} <p>The find method return object is <code>Method</code> type.</p>
     */
    Method findMethod(Class<?> clazz);

}
