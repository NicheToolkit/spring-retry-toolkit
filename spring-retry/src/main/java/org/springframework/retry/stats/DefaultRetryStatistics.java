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

package org.springframework.retry.stats;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.AttributeAccessorSupport;
import org.springframework.retry.RetryStatistics;

/**
 * <code>DefaultRetryStatistics</code>
 * <p>The default retry statistics class.</p>
 * @see  org.springframework.core.AttributeAccessorSupport
 * @see  org.springframework.retry.RetryStatistics
 * @see  org.springframework.retry.stats.MutableRetryStatistics
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class DefaultRetryStatistics extends AttributeAccessorSupport
		implements RetryStatistics, MutableRetryStatistics {

	private String name;

	private final AtomicInteger startedCount = new AtomicInteger();

	private final AtomicInteger completeCount = new AtomicInteger();

	private final AtomicInteger recoveryCount = new AtomicInteger();

	private final AtomicInteger errorCount = new AtomicInteger();

	private final AtomicInteger abortCount = new AtomicInteger();

    /**
     * <code>DefaultRetryStatistics</code>
     * <p>Instantiates a new default retry statistics.</p>
     */
    DefaultRetryStatistics() {
	}

    /**
     * <code>DefaultRetryStatistics</code>
     * <p>Instantiates a new default retry statistics.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public DefaultRetryStatistics(String name) {
		this.name = name;
	}

	@Override
	public int getCompleteCount() {
		return completeCount.get();
	}

	@Override
	public int getStartedCount() {
		return startedCount.get();
	}

	@Override
	public int getErrorCount() {
		return errorCount.get();
	}

	@Override
	public int getAbortCount() {
		return abortCount.get();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getRecoveryCount() {
		return recoveryCount.get();
	}

    /**
     * <code>setName</code>
     * <p>The set name setter method.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public void setName(String name) {
		this.name = name;
	}

	@Override
	public void incrementStartedCount() {
		this.startedCount.incrementAndGet();
	}

	@Override
	public void incrementCompleteCount() {
		this.completeCount.incrementAndGet();
	}

	@Override
	public void incrementRecoveryCount() {
		this.recoveryCount.incrementAndGet();
	}

	@Override
	public void incrementErrorCount() {
		this.errorCount.incrementAndGet();
	}

	@Override
	public void incrementAbortCount() {
		this.abortCount.incrementAndGet();
	}

	@Override
	public String toString() {
		return "DefaultRetryStatistics [name=" + name + ", startedCount=" + startedCount + ", completeCount="
				+ completeCount + ", recoveryCount=" + recoveryCount + ", errorCount=" + errorCount + ", abortCount="
				+ abortCount + "]";
	}

}
