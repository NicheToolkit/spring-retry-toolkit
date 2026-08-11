/*
 * Copyright 2006-2026 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.policy.MapRetryContextCache;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.RetryContextCache;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>CircuitBreakerStatisticsTests</code>
 * <p>The circuit breaker statistics tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class CircuitBreakerStatisticsTests {

	private static final String RECOVERED = "RECOVERED";

	private static final String RESULT = "RESULT";

	private RetryTemplate retryTemplate;

	private RecoveryCallback<Object> recovery;

	private MockRetryCallback callback;

	private DefaultRetryState state;

	private final StatisticsRepository repository = new DefaultStatisticsRepository();

	private final StatisticsListener listener = new StatisticsListener(repository);

	private RetryContextCache cache;

    /**
     * <code>init</code>
     * <p>The init method.</p>
     * @see  org.junit.jupiter.api.BeforeEach
     */
    @BeforeEach
	public void init() {
		this.callback = new MockRetryCallback();
		this.recovery = context -> RECOVERED;
		this.retryTemplate = new RetryTemplate();
		this.cache = new MapRetryContextCache();
		this.retryTemplate.setCircuitBreakerRetryContextCache(this.cache);
		retryTemplate.setListeners(new RetryListener[] { listener });
		this.callback.setAttemptsBeforeSuccess(1);
		// No rollback by default (so exceptions are not rethrown)
		this.state = new DefaultRetryState("retry", new BinaryExceptionClassifier(false));
	}

    /**
     * <code>testCircuitOpenWhenNotRetryable</code>
     * <p>The test circuit open when not retryable method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testCircuitOpenWhenNotRetryable() throws Throwable {
		this.retryTemplate.setRetryPolicy(new CircuitBreakerRetryPolicy(new NeverRetryPolicy()));
		Object result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		MutableRetryStatistics stats = (MutableRetryStatistics) repository.findOne("test");
		assertThat(stats.getStartedCount()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(stats.getRecoveryCount()).describedAs("There should be two recoveries").isEqualTo(2);
		assertThat(stats.getErrorCount()).describedAs("There should only be one error because the circuit is now open")
			.isEqualTo(1);
		assertThat(stats.getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN)).isEqualTo(Boolean.TRUE);
		// Both recoveries are through a short circuit because we used NeverRetryPolicy
		assertThat(stats.getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_SHORT_COUNT)).isEqualTo(2);
		resetAndAssert(this.cache, stats);
	}

    /**
     * <code>testFailedRecoveryCountsAsAbort</code>
     * <p>The test failed recovery counts as abort method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailedRecoveryCountsAsAbort() {
		this.retryTemplate.setRetryPolicy(new CircuitBreakerRetryPolicy(new NeverRetryPolicy()));
		this.recovery = context -> {
			throw new ExhaustedRetryException("Planned exhausted");
		};
		assertThatExceptionOfType(ExhaustedRetryException.class)
			.isThrownBy(() -> this.retryTemplate.execute(this.callback, this.recovery, this.state));
		MutableRetryStatistics stats = (MutableRetryStatistics) repository.findOne("test");
		assertThat(stats.getStartedCount()).isEqualTo(1);
		assertThat(stats.getAbortCount()).isEqualTo(1);
		assertThat(stats.getRecoveryCount()).isEqualTo(0);
	}

    /**
     * <code>testCircuitOpenWithNoRecovery</code>
     * <p>The test circuit open with no recovery method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCircuitOpenWithNoRecovery() {
		this.retryTemplate.setRetryPolicy(new CircuitBreakerRetryPolicy(new NeverRetryPolicy()));
		this.retryTemplate.setThrowLastExceptionOnExhausted(true);
		try {
			this.retryTemplate.execute(this.callback, this.state);
		}
		catch (Exception e) {
		}
		try {
			this.retryTemplate.execute(this.callback, this.state);
		}
		catch (Exception e) {
		}
		MutableRetryStatistics stats = (MutableRetryStatistics) repository.findOne("test");
		assertThat(stats.getAbortCount()).describedAs("There should be two aborts").isEqualTo(2);
		assertThat(stats.getErrorCount()).describedAs("There should only be one error because the circuit is now open")
			.isEqualTo(1);
		assertThat(stats.getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN)).isEqualTo(true);
		resetAndAssert(this.cache, stats);
	}

	private void resetAndAssert(RetryContextCache cache, MutableRetryStatistics stats) {
		reset(cache.get("retry"));
		listener.close(cache.get("retry"), callback, null);
		assertThat(stats.getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_SHORT_COUNT)).isEqualTo(0);
	}

	private void reset(RetryContext retryContext) {
		ReflectionTestUtils.invokeMethod(retryContext, "reset");
	}

    /**
     * <code>MockRetryCallback</code>
     * <p>The mock retry callback class.</p>
     * @see  org.springframework.retry.RetryCallback
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class MockRetryCallback implements RetryCallback<Object, Exception> {

		private int attemptsBeforeSuccess;

		private Exception exceptionToThrow = new Exception();

		private RetryContext status;

		@Override
		public Object doWithRetry(RetryContext status) throws Exception {
			status.setAttribute(RetryContext.NAME, "test");
			this.status = status;
			int attempts = (Integer) status.getAttribute("attempts");
			attempts++;
			status.setAttribute("attempts", attempts);
			if (attempts <= this.attemptsBeforeSuccess) {
				throw this.exceptionToThrow;
			}
			return RESULT;
		}

        /**
         * <code>isOpen</code>
         * <p>The is open method.</p>
         * @return  boolean <p>The is open return object is <code>boolean</code> type.</p>
         */
        public boolean isOpen() {
			return status != null && status.getAttribute("open") == Boolean.TRUE;
		}

        /**
         * <code>setAttemptsBeforeSuccess</code>
         * <p>The set attempts before success setter method.</p>
         * @param attemptsBeforeSuccess int <p>The attempts before success parameter is <code>int</code> type.</p>
         */
        public void setAttemptsBeforeSuccess(int attemptsBeforeSuccess) {
			this.attemptsBeforeSuccess = attemptsBeforeSuccess;
		}

        /**
         * <code>setExceptionToThrow</code>
         * <p>The set exception to throw setter method.</p>
         * @param exceptionToThrow {@link java.lang.Exception} <p>The exception to throw parameter is <code>Exception</code> type.</p>
         * @see  java.lang.Exception
         */
        public void setExceptionToThrow(Exception exceptionToThrow) {
			this.exceptionToThrow = exceptionToThrow;
		}

	}

}
