/*
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.retry.interceptor;

/**
 * <code>MethodArgumentsKeyGenerator</code>
 * <p>The method arguments key generator interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface MethodArgumentsKeyGenerator {

    /**
     * <code>getKey</code>
     * <p>The get key getter method.</p>
     * @param item {@link java.lang.Object} <p>The item parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @return  {@link java.lang.Object} <p>The get key return object is <code>Object</code> type.</p>
     */
    Object getKey(Object[] item);

}
