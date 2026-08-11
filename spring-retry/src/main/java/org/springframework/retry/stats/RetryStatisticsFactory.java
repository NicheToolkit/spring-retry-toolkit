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

/**
 * <code>RetryStatisticsFactory</code>
 * <p>The retry statistics factory interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryStatisticsFactory {

    /**
     * <code>create</code>
     * <p>The create method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  org.springframework.retry.stats.MutableRetryStatistics
     * @return  {@link org.springframework.retry.stats.MutableRetryStatistics} <p>The create return object is <code>MutableRetryStatistics</code> type.</p>
     */
    MutableRetryStatistics create(String name);

}
