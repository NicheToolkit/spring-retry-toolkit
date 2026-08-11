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
 * <code>RetryStatistics</code>
 * <p>The retry statistics interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryStatistics {

    /**
     * <code>getCompleteCount</code>
     * <p>The get complete count getter method.</p>
     * @return  int <p>The get complete count return object is <code>int</code> type.</p>
     */
    int getCompleteCount();

    /**
     * <code>getStartedCount</code>
     * <p>The get started count getter method.</p>
     * @return  int <p>The get started count return object is <code>int</code> type.</p>
     */
    int getStartedCount();

    /**
     * <code>getErrorCount</code>
     * <p>The get error count getter method.</p>
     * @return  int <p>The get error count return object is <code>int</code> type.</p>
     */
    int getErrorCount();

    /**
     * <code>getAbortCount</code>
     * <p>The get abort count getter method.</p>
     * @return  int <p>The get abort count return object is <code>int</code> type.</p>
     */
    int getAbortCount();

    /**
     * <code>getRecoveryCount</code>
     * <p>The get recovery count getter method.</p>
     * @return  int <p>The get recovery count return object is <code>int</code> type.</p>
     */
    int getRecoveryCount();

    /**
     * <code>getName</code>
     * <p>The get name getter method.</p>
     * @return  {@link java.lang.String} <p>The get name return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String getName();

}
