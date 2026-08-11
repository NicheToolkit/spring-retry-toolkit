/*
 * Copyright 2006-2023 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy.CircuitBreakerRetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>CircuitBreakerRetryTemplateTests</code>
 * <p>The circuit breaker retry template tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class CircuitBreakerRetryTemplateTests {

	private static final String RECOVERED = "RECOVERED";

	private static final String RESULT = "RESULT";

	private RetryTemplate retryTemplate;

	private RecoveryCallback<Object> recovery;

	private MockRetryCallback callback;

	private DefaultRetryState state;

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
		assertThat(this.callback.getAttempts()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		// circuit is now open so no more attempts
		assertThat(this.callback.getAttempts()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
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
		assertThatExceptionOfType(Exception.class)
			.isThrownBy(() -> this.retryTemplate.execute(this.callback, this.state))
			.isEqualTo(this.callback.exceptionToThrow);
		assertThat(this.callback.getAttempts()).isEqualTo(1);
		assertThatExceptionOfType(Exception.class)
			.isThrownBy(() -> this.retryTemplate.execute(this.callback, this.state))
			.isEqualTo(this.callback.exceptionToThrow);
		assertThat(this.callback.getAttempts()).isEqualTo(1);
	}

    /**
     * <code>testCircuitOpensWhenDelegateNotRetryable</code>
     * <p>The test circuit opens when delegate not retryable method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testCircuitOpensWhenDelegateNotRetryable() throws Throwable {
		this.retryTemplate.setRetryPolicy(new CircuitBreakerRetryPolicy(new SimpleRetryPolicy()));
		this.callback.setAttemptsBeforeSuccess(10);
		Object result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		assertThat(this.callback.getAttempts()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(this.callback.status.isOpen()).isFalse();
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		// circuit is now open so no more attempts
		assertThat(this.callback.getAttempts()).isEqualTo(3);
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(this.callback.status.isOpen()).isTrue();
	}

    /**
     * <code>testWindowResetsAfterTimeout</code>
     * <p>The test window resets after timeout method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testWindowResetsAfterTimeout() throws Throwable {
		CircuitBreakerRetryPolicy retryPolicy = new CircuitBreakerRetryPolicy(new SimpleRetryPolicy());
		this.retryTemplate.setRetryPolicy(retryPolicy);
		retryPolicy.setOpenTimeout(100);
		this.callback.setAttemptsBeforeSuccess(10);
		Object result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		assertThat(this.callback.getAttempts()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(this.callback.status.isOpen()).isFalse();
		Thread.sleep(200L);
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		// circuit is reset after sleep window
		assertThat(this.callback.getAttempts()).isEqualTo(2);
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(this.callback.status.isOpen()).isFalse();
	}

    /**
     * <code>testCircuitClosesAfterTimeout</code>
     * <p>The test circuit closes after timeout method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testCircuitClosesAfterTimeout() throws Throwable {
		CircuitBreakerRetryPolicy retryPolicy = new CircuitBreakerRetryPolicy(new NeverRetryPolicy());
		this.retryTemplate.setRetryPolicy(retryPolicy);
		retryPolicy.setResetTimeout(100);
		Object result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		assertThat(this.callback.getAttempts()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(this.callback.status.isOpen()).isTrue();
		// Sleep longer than the timeout
		Thread.sleep(200L);
		assertThat(this.callback.status.isOpen()).isFalse();
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		// circuit closed again now
		assertThat(result).isEqualTo(RESULT);
	}

    /**
     * <code>testCircuitOpensWhenRetryPolicyFirstTimeAttributeCircuitOpenNull</code>
     * <p>The test circuit opens when retry policy first time attribute circuit open null method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testCircuitOpensWhenRetryPolicyFirstTimeAttributeCircuitOpenNull() throws Throwable {
		MockNeverRetryPolicy mockNeverRetryPolicy = new MockNeverRetryPolicy();
		this.retryTemplate.setRetryPolicy(new CircuitBreakerRetryPolicy(mockNeverRetryPolicy));
		this.callback.setAttemptsBeforeSuccess(10);
		Object result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		assertThat(result).isEqualTo(RECOVERED);
		result = this.retryTemplate.execute(this.callback, this.recovery, this.state);
		assertThat(result).isEqualTo(RECOVERED);
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

		private CircuitBreakerRetryContext status;

		@Override
		public Object doWithRetry(RetryContext status) throws Exception {
			this.status = (CircuitBreakerRetryContext) status;
			int attempts = getAttempts();
			attempts++;
			status.setAttribute("attempts", attempts);
			if (attempts <= this.attemptsBeforeSuccess) {
				throw this.exceptionToThrow;
			}
			return RESULT;
		}

        /**
         * <code>getAttempts</code>
         * <p>The get attempts getter method.</p>
         * @return  int <p>The get attempts return object is <code>int</code> type.</p>
         */
        public int getAttempts() {
			if (!this.status.hasAttribute("attempts")) {
				this.status.setAttribute("attempts", 0);
			}
			int attempts = (Integer) this.status.getAttribute("attempts");
			return attempts;
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

    /**
     * <code>MockNeverRetryPolicy</code>
     * <p>The mock never retry policy class.</p>
     * @see  org.springframework.retry.policy.NeverRetryPolicy
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected class MockNeverRetryPolicy extends NeverRetryPolicy {

		public boolean canRetry(RetryContext context) {
			return false;
		}

	}

}
