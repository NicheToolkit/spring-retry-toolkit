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

package org.springframework.retry.backoff;

import org.springframework.retry.RetryContext;

/**
 * <code>BackOffPolicy</code>
 * <p>The back off policy interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface BackOffPolicy {

    /**
     * <code>start</code>
     * <p>The start method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.backoff.BackOffContext
     * @return  {@link org.springframework.retry.backoff.BackOffContext} <p>The start return object is <code>BackOffContext</code> type.</p>
     */
    BackOffContext start(RetryContext context);

    /**
     * <code>backOff</code>
     * <p>The back off method.</p>
     * @param backOffContext {@link org.springframework.retry.backoff.BackOffContext} <p>The back off context parameter is <code>BackOffContext</code> type.</p>
     * @see  org.springframework.retry.backoff.BackOffContext
     * @see  org.springframework.retry.backoff.BackOffInterruptedException
     * @throws BackOffInterruptedException {@link org.springframework.retry.backoff.BackOffInterruptedException} <p>The back off interrupted exception is <code>BackOffInterruptedException</code> type.</p>
     */
    void backOff(BackOffContext backOffContext) throws BackOffInterruptedException;

}
