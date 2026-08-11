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

import org.springframework.core.AttributeAccessor;
import org.springframework.retry.RetryStatistics;

/**
 * <code>MutableRetryStatistics</code>
 * <p>The mutable retry statistics interface.</p>
 * @see  org.springframework.retry.RetryStatistics
 * @see  org.springframework.core.AttributeAccessor
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface MutableRetryStatistics extends RetryStatistics, AttributeAccessor {

    /**
     * <code>incrementStartedCount</code>
     * <p>The increment started count method.</p>
     */
    void incrementStartedCount();

    /**
     * <code>incrementCompleteCount</code>
     * <p>The increment complete count method.</p>
     */
    void incrementCompleteCount();

    /**
     * <code>incrementRecoveryCount</code>
     * <p>The increment recovery count method.</p>
     */
    void incrementRecoveryCount();

    /**
     * <code>incrementErrorCount</code>
     * <p>The increment error count method.</p>
     */
    void incrementErrorCount();

    /**
     * <code>incrementAbortCount</code>
     * <p>The increment abort count method.</p>
     */
    void incrementAbortCount();

}