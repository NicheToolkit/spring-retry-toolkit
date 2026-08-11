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

/**
 * <code>SleepingBackOffPolicy</code>
 * <p>The sleeping back off policy interface.</p>
 * @param <T>  {@link org.springframework.retry.backoff.SleepingBackOffPolicy} <p>The generic parameter is <code>SleepingBackOffPolicy</code> type.</p>
 * @see  org.springframework.retry.backoff.BackOffPolicy
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface SleepingBackOffPolicy<T extends SleepingBackOffPolicy<T>> extends BackOffPolicy {

    /**
     * <code>withSleeper</code>
     * <p>The with sleeper method.</p>
     * @param sleeper {@link org.springframework.retry.backoff.Sleeper} <p>The sleeper parameter is <code>Sleeper</code> type.</p>
     * @see  org.springframework.retry.backoff.Sleeper
     * @return  T <p>The with sleeper return object is <code>T</code> type.</p>
     */
    T withSleeper(Sleeper sleeper);

}
