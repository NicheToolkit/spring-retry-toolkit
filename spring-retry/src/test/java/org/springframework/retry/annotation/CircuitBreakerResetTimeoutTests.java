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

package org.springframework.retry.annotation;

import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.support.RetrySynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>CircuitBreakerResetTimeoutTests</code>
 * <p>The circuit breaker reset timeout tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class CircuitBreakerResetTimeoutTests {

	private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
			TestConfiguration.class);

	private final TestService serviceInTest = context.getBean(TestService.class);

    /**
     * <code>circuitBreakerShouldBeClosedAfterResetTimeout</code>
     * <p>The circuit breaker should be closed after reset timeout method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.InterruptedException
     * @throws InterruptedException {@link java.lang.InterruptedException} <p>The interrupted exception is <code>InterruptedException</code> type.</p>
     */
    @Test
	public void circuitBreakerShouldBeClosedAfterResetTimeout() throws InterruptedException {
		incorrectStep();
		incorrectStep();
		incorrectStep();
		incorrectStep();

		final long timeOfLastFailure = System.currentTimeMillis();
		correctStep(timeOfLastFailure);
		correctStep(timeOfLastFailure);
		correctStep(timeOfLastFailure);
		assertThat((Boolean) serviceInTest.getContext().getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN)).isFalse();
	}

	private void incorrectStep() {
		doFailedUpload(serviceInTest);
		System.out.println();
	}

	private void correctStep(final long timeOfLastFailure) throws InterruptedException {
		Thread.sleep(6000L);
		printTime(timeOfLastFailure);
		doCorrectUpload(serviceInTest);
		System.out.println();
	}

	private void printTime(final long timeOfLastFailure) {
		System.out.println(String.format("%d ms after last failure", (System.currentTimeMillis() - timeOfLastFailure)));
	}

	private void doFailedUpload(TestService externalService) {
		externalService.service("FAIL");
	}

	private void doCorrectUpload(TestService externalService) {
		externalService.service("");
	}

    /**
     * <code>TestConfiguration</code>
     * <p>The test configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @see  org.springframework.retry.annotation.EnableRetry
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry
	protected static class TestConfiguration {

        /**
         * <code>externalService</code>
         * <p>The external service method.</p>
         * @return  {@link org.springframework.retry.annotation.CircuitBreakerResetTimeoutTests.TestService} <p>The external service return object is <code>TestService</code> type.</p>
         * @see  org.springframework.retry.annotation.CircuitBreakerResetTimeoutTests.TestService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public TestService externalService() {
			return new TestService();
		}

	}

    /**
     * <code>TestService</code>
     * <p>The test service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class TestService {

		private RetryContext context;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @param payload {@link java.lang.String} <p>The payload parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @return  {@link java.lang.String} <p>The service return object is <code>String</code> type.</p>
         */
        @CircuitBreaker(retryFor = { RuntimeException.class }, openTimeout = 10000, resetTimeout = 15000)
		String service(String payload) {
			this.context = RetrySynchronizationManager.getContext();
			System.out.println("real service called");
			if (payload.contentEquals("FAIL")) {
				throw new RuntimeException("");
			}
			return payload;
		}

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @return  {@link java.lang.String} <p>The recover return object is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Recover
         */
        @Recover
		public String recover() {
			System.out.println("recovery action");
			return "";
		}

        /**
         * <code>getContext</code>
         * <p>The get context getter method.</p>
         * @return  {@link org.springframework.retry.RetryContext} <p>The get context return object is <code>RetryContext</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         */
        public RetryContext getContext() {
			return this.context;
		}

	}

}
