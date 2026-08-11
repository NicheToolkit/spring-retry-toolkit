/*
 * Copyright 2006-2019 the original author or authors.
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

package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.retry.support.RetryTemplate;

/**
 * <code>MaxAttemptsRetryPolicy</code>
 * <p>The max attempts retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class MaxAttemptsRetryPolicy implements RetryPolicy {

    /**
     * <code>DEFAULT_MAX_ATTEMPTS</code>
     * <p>The constant <code>DEFAULT_MAX_ATTEMPTS</code> field.</p>
     */
    public final static int DEFAULT_MAX_ATTEMPTS = 3;

	private volatile int maxAttempts;

    /**
     * <code>MaxAttemptsRetryPolicy</code>
     * <p>Instantiates a new max attempts retry policy.</p>
     */
    public MaxAttemptsRetryPolicy() {
		this.maxAttempts = DEFAULT_MAX_ATTEMPTS;
	}

    /**
     * <code>MaxAttemptsRetryPolicy</code>
     * <p>Instantiates a new max attempts retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     */
    public MaxAttemptsRetryPolicy(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

    /**
     * <code>setMaxAttempts</code>
     * <p>The set max attempts setter method.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     */
    public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	@Override
	public int getMaxAttempts() {
		return this.maxAttempts;
	}

	@Override
	public boolean canRetry(RetryContext context) {
		return context.getRetryCount() < this.maxAttempts;
	}

	@Override
	public void close(RetryContext status) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		((RetryContextSupport) context).registerThrowable(throwable);
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new RetryContextSupport(parent);
	}

}
