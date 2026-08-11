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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * <code>CircuitBreakerRetryPolicy</code>
 * <p>The circuit breaker retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class CircuitBreakerRetryPolicy implements RetryPolicy {

    /**
     * <code>CIRCUIT_OPEN</code>
     * {@link java.lang.String} <p>The constant <code>CIRCUIT_OPEN</code> field.</p>
     * @see  java.lang.String
     */
    public static final String CIRCUIT_OPEN = "circuit.open";

    /**
     * <code>CIRCUIT_SHORT_COUNT</code>
     * {@link java.lang.String} <p>The constant <code>CIRCUIT_SHORT_COUNT</code> field.</p>
     * @see  java.lang.String
     */
    public static final String CIRCUIT_SHORT_COUNT = "circuit.shortCount";

	private static final Log logger = LogFactory.getLog(CircuitBreakerRetryPolicy.class);

	private final RetryPolicy delegate;

	private long resetTimeout = 20000;

	private long openTimeout = 5000;

	private Supplier<Long> resetTimeoutSupplier;

	private Supplier<Long> openTimeoutSupplier;

    /**
     * <code>CircuitBreakerRetryPolicy</code>
     * <p>Instantiates a new circuit breaker retry policy.</p>
     */
    public CircuitBreakerRetryPolicy() {
		this(new SimpleRetryPolicy());
	}

    /**
     * <code>CircuitBreakerRetryPolicy</code>
     * <p>Instantiates a new circuit breaker retry policy.</p>
     * @param delegate {@link org.springframework.retry.RetryPolicy} <p>The delegate parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     */
    public CircuitBreakerRetryPolicy(RetryPolicy delegate) {
		this.delegate = delegate;
	}

    /**
     * <code>setResetTimeout</code>
     * <p>The set reset timeout setter method.</p>
     * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
     */
    public void setResetTimeout(long timeout) {
		this.resetTimeout = timeout;
	}

    /**
     * <code>resetTimeoutSupplier</code>
     * <p>The reset timeout supplier method.</p>
     * @param timeoutSupplier {@link java.util.function.Supplier} <p>The timeout supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void resetTimeoutSupplier(Supplier<Long> timeoutSupplier) {
		this.resetTimeoutSupplier = timeoutSupplier;
	}

    /**
     * <code>setOpenTimeout</code>
     * <p>The set open timeout setter method.</p>
     * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
     */
    public void setOpenTimeout(long timeout) {
		this.openTimeout = timeout;
	}

    /**
     * <code>openTimeoutSupplier</code>
     * <p>The open timeout supplier method.</p>
     * @param timeoutSupplier {@link java.util.function.Supplier} <p>The timeout supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void openTimeoutSupplier(Supplier<Long> timeoutSupplier) {
		this.openTimeoutSupplier = timeoutSupplier;
	}

	@Override
	public boolean canRetry(RetryContext context) {
		CircuitBreakerRetryContext circuit = (CircuitBreakerRetryContext) context;
		if (circuit.isOpen()) {
			circuit.incrementShortCircuitCount();
			return false;
		}
		else {
			circuit.reset();
		}
		return this.delegate.canRetry(circuit.context);
	}

	@Override
	public RetryContext open(RetryContext parent) {
		long resetTimeout = this.resetTimeout;
		if (this.resetTimeoutSupplier != null) {
			resetTimeout = this.resetTimeoutSupplier.get();
		}
		long openTimeout = this.openTimeout;
		if (this.openTimeoutSupplier != null) {
			openTimeout = this.openTimeoutSupplier.get();
		}
		return new CircuitBreakerRetryContext(parent, this.delegate, resetTimeout, openTimeout);
	}

	@Override
	public void close(RetryContext context) {
		CircuitBreakerRetryContext circuit = (CircuitBreakerRetryContext) context;
		this.delegate.close(circuit.context);
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		CircuitBreakerRetryContext circuit = (CircuitBreakerRetryContext) context;
		circuit.registerThrowable(throwable);
		this.delegate.registerThrowable(circuit.context, throwable);
	}

    /**
     * <code>CircuitBreakerRetryContext</code>
     * <p>The circuit breaker retry context class.</p>
     * @see  org.springframework.retry.context.RetryContextSupport
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class CircuitBreakerRetryContext extends RetryContextSupport {

		private volatile RetryContext context;

		private final RetryPolicy policy;

		private volatile long start = System.currentTimeMillis();

		private final long timeout;

		private final long openWindow;

		private final AtomicInteger shortCircuitCount = new AtomicInteger();

        /**
         * <code>CircuitBreakerRetryContext</code>
         * <p>Instantiates a new circuit breaker retry context.</p>
         * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
         * @param policy {@link org.springframework.retry.RetryPolicy} <p>The policy parameter is <code>RetryPolicy</code> type.</p>
         * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
         * @param openWindow long <p>The open window parameter is <code>long</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         * @see  org.springframework.retry.RetryPolicy
         */
        public CircuitBreakerRetryContext(RetryContext parent, RetryPolicy policy, long timeout, long openWindow) {
			super(parent);
			this.policy = policy;
			this.timeout = timeout;
			this.openWindow = openWindow;
			this.context = createDelegateContext(policy, parent);
			setAttribute("state.global", true);
		}

        /**
         * <code>reset</code>
         * <p>The reset method.</p>
         */
        public void reset() {
			shortCircuitCount.set(0);
			setAttribute(CIRCUIT_SHORT_COUNT, shortCircuitCount.get());
		}

        /**
         * <code>incrementShortCircuitCount</code>
         * <p>The increment short circuit count method.</p>
         */
        public void incrementShortCircuitCount() {
			shortCircuitCount.incrementAndGet();
			setAttribute(CIRCUIT_SHORT_COUNT, shortCircuitCount.get());
		}

		private RetryContext createDelegateContext(RetryPolicy policy, RetryContext parent) {
			RetryContext context = policy.open(parent);
			reset();
			return context;
		}

        /**
         * <code>isOpen</code>
         * <p>The is open method.</p>
         * @return  boolean <p>The is open return object is <code>boolean</code> type.</p>
         */
        public boolean isOpen() {
			long time = System.currentTimeMillis() - this.start;
			boolean retryable = this.policy.canRetry(this.context);
			if (!retryable) {
				if (time > this.timeout) {
					logger.trace("Closing");
					this.context = createDelegateContext(policy, getParent());
					this.start = System.currentTimeMillis();
					retryable = this.policy.canRetry(this.context);
				}
				else if (time < this.openWindow) {
					if (!hasAttribute(CIRCUIT_OPEN) || (Boolean) getAttribute(CIRCUIT_OPEN) == false) {
						logger.trace("Opening circuit");
						setAttribute(CIRCUIT_OPEN, true);
						this.start = System.currentTimeMillis();
					}

					return true;
				}
			}
			else {
				if (time > this.openWindow) {
					logger.trace("Resetting context");
					this.start = System.currentTimeMillis();
					this.context = createDelegateContext(policy, getParent());
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Open: " + !retryable);
			}
			setAttribute(CIRCUIT_OPEN, !retryable);
			return !retryable;
		}

		@Override
		public int getRetryCount() {
			return this.context.getRetryCount();
		}

		@Override
		public String toString() {
			return this.context.toString();
		}

	}

}
