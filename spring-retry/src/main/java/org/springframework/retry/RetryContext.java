/*
 * Copyright 2006-2022 the original author or authors.
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

import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 * <code>RetryContext</code>
 * <p>The retry context interface.</p>
 * @see  org.springframework.core.AttributeAccessor
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryContext extends AttributeAccessor {

    /**
     * <code>NAME</code>
     * {@link java.lang.String} <p>The constant <code>NAME</code> field.</p>
     * @see  java.lang.String
     */
    String NAME = "context.name";

    /**
     * <code>STATE_KEY</code>
     * {@link java.lang.String} <p>The constant <code>STATE_KEY</code> field.</p>
     * @see  java.lang.String
     */
    String STATE_KEY = "context.state";

    /**
     * <code>CLOSED</code>
     * {@link java.lang.String} <p>The constant <code>CLOSED</code> field.</p>
     * @see  java.lang.String
     */
    String CLOSED = "context.closed";

    /**
     * <code>RECOVERED</code>
     * {@link java.lang.String} <p>The constant <code>RECOVERED</code> field.</p>
     * @see  java.lang.String
     */
    String RECOVERED = "context.recovered";

    /**
     * <code>EXHAUSTED</code>
     * {@link java.lang.String} <p>The constant <code>EXHAUSTED</code> field.</p>
     * @see  java.lang.String
     */
    String EXHAUSTED = "context.exhausted";

    /**
     * <code>NO_RECOVERY</code>
     * {@link java.lang.String} <p>The constant <code>NO_RECOVERY</code> field.</p>
     * @see  java.lang.String
     */
    String NO_RECOVERY = "context.no-recovery";

    /**
     * <code>MAX_ATTEMPTS</code>
     * {@link java.lang.String} <p>The constant <code>MAX_ATTEMPTS</code> field.</p>
     * @see  java.lang.String
     */
    String MAX_ATTEMPTS = "context.max-attempts";

    /**
     * <code>setExhaustedOnly</code>
     * <p>The set exhausted only setter method.</p>
     */
    void setExhaustedOnly();

    /**
     * <code>isExhaustedOnly</code>
     * <p>The is exhausted only method.</p>
     * @return  boolean <p>The is exhausted only return object is <code>boolean</code> type.</p>
     */
    boolean isExhaustedOnly();

    /**
     * <code>getParent</code>
     * <p>The get parent getter method.</p>
     * @return  {@link org.springframework.retry.RetryContext} <p>The get parent return object is <code>RetryContext</code> type.</p>
     * @see  org.springframework.lang.Nullable
     */
    @Nullable RetryContext getParent();

    /**
     * <code>getRetryCount</code>
     * <p>The get retry count getter method.</p>
     * @return  int <p>The get retry count return object is <code>int</code> type.</p>
     */
    int getRetryCount();

    /**
     * <code>getLastThrowable</code>
     * <p>The get last throwable getter method.</p>
     * @return  {@link java.lang.Throwable} <p>The get last throwable return object is <code>Throwable</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.lang.Nullable
     */
    @Nullable Throwable getLastThrowable();

}
