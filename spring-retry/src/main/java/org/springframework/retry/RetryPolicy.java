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

import java.io.Serializable;

/**
 * <code>RetryPolicy</code>
 * <p>The retry policy interface.</p>
 * @see  java.io.Serializable
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryPolicy extends Serializable {

    /**
     * <code>NO_MAXIMUM_ATTEMPTS_SET</code>
     * <p>The constant <code>NO_MAXIMUM_ATTEMPTS_SET</code> field.</p>
     */
    int NO_MAXIMUM_ATTEMPTS_SET = -1;

    /**
     * <code>canRetry</code>
     * <p>The can retry method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @return  boolean <p>The can retry return object is <code>boolean</code> type.</p>
     */
    boolean canRetry(RetryContext context);

    /**
     * <code>open</code>
     * <p>The open method.</p>
     * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @return  {@link org.springframework.retry.RetryContext} <p>The open return object is <code>RetryContext</code> type.</p>
     */
    RetryContext open(RetryContext parent);

    /**
     * <code>close</code>
     * <p>The close method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     */
    void close(RetryContext context);

    /**
     * <code>registerThrowable</code>
     * <p>The register throwable method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  java.lang.Throwable
     */
    void registerThrowable(RetryContext context, Throwable throwable);

    /**
     * <code>getMaxAttempts</code>
     * <p>The get max attempts getter method.</p>
     * @return  int <p>The get max attempts return object is <code>int</code> type.</p>
     */
    default int getMaxAttempts() {
		return NO_MAXIMUM_ATTEMPTS_SET;
	}

}
