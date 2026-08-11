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

package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * <code>TimeoutRetryPolicy</code>
 * <p>The timeout retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class TimeoutRetryPolicy implements RetryPolicy {

    /**
     * <code>DEFAULT_TIMEOUT</code>
     * <p>The constant <code>DEFAULT_TIMEOUT</code> field.</p>
     */
    public static final long DEFAULT_TIMEOUT = 1000;

	private long timeout;

    /**
     * <code>TimeoutRetryPolicy</code>
     * <p>Instantiates a new timeout retry policy.</p>
     */
    public TimeoutRetryPolicy() {
		this(DEFAULT_TIMEOUT);
	}

    /**
     * <code>TimeoutRetryPolicy</code>
     * <p>Instantiates a new timeout retry policy.</p>
     * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
     */
    public TimeoutRetryPolicy(long timeout) {
		this.timeout = timeout;
	}

    /**
     * <code>setTimeout</code>
     * <p>The set timeout setter method.</p>
     * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
     */
    public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

    /**
     * <code>getTimeout</code>
     * <p>The get timeout getter method.</p>
     * @return  long <p>The get timeout return object is <code>long</code> type.</p>
     */
    public long getTimeout() {
		return timeout;
	}

	public boolean canRetry(RetryContext context) {
		return ((TimeoutRetryContext) context).isAlive();
	}

	public void close(RetryContext context) {
	}

	public RetryContext open(RetryContext parent) {
		return new TimeoutRetryContext(parent, timeout);
	}

	public void registerThrowable(RetryContext context, Throwable throwable) {
		((RetryContextSupport) context).registerThrowable(throwable);
		// otherwise no-op - we only time out, otherwise retry everything...
	}

	private static class TimeoutRetryContext extends RetryContextSupport {

		private final long timeout;

		private final long start;

        /**
         * <code>TimeoutRetryContext</code>
         * <p>Instantiates a new timeout retry context.</p>
         * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
         * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         */
        public TimeoutRetryContext(RetryContext parent, long timeout) {
			super(parent);
			this.start = System.currentTimeMillis();
			this.timeout = timeout;
		}

        /**
         * <code>isAlive</code>
         * <p>The is alive method.</p>
         * @return  boolean <p>The is alive return object is <code>boolean</code> type.</p>
         */
        public boolean isAlive() {
			return (System.currentTimeMillis() - start) <= timeout;
		}

	}

}
