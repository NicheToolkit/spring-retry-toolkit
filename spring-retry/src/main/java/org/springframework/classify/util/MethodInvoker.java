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

/**
 * <code>MethodInvoker</code>
 * <p>The method invoker interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface MethodInvoker {

    /**
     * <code>invokeMethod</code>
     * <p>The invoke method method.</p>
     * @param args {@link java.lang.Object} <p>The args parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @return  {@link java.lang.Object} <p>The invoke method return object is <code>Object</code> type.</p>
     */
    Object invokeMethod(Object... args);

}
