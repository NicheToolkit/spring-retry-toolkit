/*
 * Copyright 2012-2015 the original author or authors.
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

package org.springframework.retry.stats;

import org.springframework.retry.RetryStatistics;

/**
 * <code>StatisticsRepository</code>
 * <p>The statistics repository interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface StatisticsRepository {

    /**
     * <code>findOne</code>
     * <p>The find one method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  org.springframework.retry.RetryStatistics
     * @return  {@link org.springframework.retry.RetryStatistics} <p>The find one return object is <code>RetryStatistics</code> type.</p>
     */
    RetryStatistics findOne(String name);

    /**
     * <code>findAll</code>
     * <p>The find all method.</p>
     * @return  {@link java.lang.Iterable} <p>The find all return object is <code>Iterable</code> type.</p>
     * @see  java.lang.Iterable
     */
    Iterable<RetryStatistics> findAll();

    /**
     * <code>addStarted</code>
     * <p>The add started method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    void addStarted(String name);

    /**
     * <code>addError</code>
     * <p>The add error method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    void addError(String name);

    /**
     * <code>addRecovery</code>
     * <p>The add recovery method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    void addRecovery(String name);

    /**
     * <code>addComplete</code>
     * <p>The add complete method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    void addComplete(String name);

    /**
     * <code>addAbort</code>
     * <p>The add abort method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    void addAbort(String name);

}
