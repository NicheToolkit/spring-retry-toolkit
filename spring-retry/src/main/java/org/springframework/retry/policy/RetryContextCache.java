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

package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;

/**
 * <code>RetryContextCache</code>
 * <p>The retry context cache interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryContextCache {

    /**
     * <code>get</code>
     * <p>The get method.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @see  org.springframework.retry.RetryContext
     * @return  {@link org.springframework.retry.RetryContext} <p>The get return object is <code>RetryContext</code> type.</p>
     */
    RetryContext get(Object key);

    /**
     * <code>put</code>
     * <p>The put method.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  java.lang.Object
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.policy.RetryCacheCapacityExceededException
     * @throws RetryCacheCapacityExceededException {@link org.springframework.retry.policy.RetryCacheCapacityExceededException} <p>The retry cache capacity exceeded exception is <code>RetryCacheCapacityExceededException</code> type.</p>
     */
    void put(Object key, RetryContext context) throws RetryCacheCapacityExceededException;

    /**
     * <code>remove</code>
     * <p>The remove method.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    void remove(Object key);

    /**
     * <code>containsKey</code>
     * <p>The contains key method.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @return  boolean <p>The contains key return object is <code>boolean</code> type.</p>
     */
    boolean containsKey(Object key);

}
