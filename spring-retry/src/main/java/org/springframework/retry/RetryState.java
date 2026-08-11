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
package org.springframework.retry;

/**
 * <code>RetryState</code>
 * <p>The retry state interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryState {

    /**
     * <code>getKey</code>
     * <p>The get key getter method.</p>
     * @return  {@link java.lang.Object} <p>The get key return object is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    Object getKey();

    /**
     * <code>isForceRefresh</code>
     * <p>The is force refresh method.</p>
     * @return  boolean <p>The is force refresh return object is <code>boolean</code> type.</p>
     */
    boolean isForceRefresh();

    /**
     * <code>rollbackFor</code>
     * <p>The rollback for method.</p>
     * @param exception {@link java.lang.Throwable} <p>The exception parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.Throwable
     * @return  boolean <p>The rollback for return object is <code>boolean</code> type.</p>
     */
    boolean rollbackFor(Throwable exception);

}