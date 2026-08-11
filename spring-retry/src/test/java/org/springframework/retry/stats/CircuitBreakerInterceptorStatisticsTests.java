/*
 * Copyright 2006-2024 the original author or authors.
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryStatistics;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.support.RetrySynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>CircuitBreakerInterceptorStatisticsTests</code>
 * <p>The circuit breaker interceptor statistics tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class CircuitBreakerInterceptorStatisticsTests {

	private static final String RECOVERED = "RECOVERED";

	private static final String RESULT = "RESULT";

	private Service callback;

	private StatisticsRepository repository;

	private AnnotationConfigApplicationContext context;

    /**
     * <code>init</code>
     * <p>The init method.</p>
     * @see  org.junit.jupiter.api.BeforeEach
     */
    @BeforeEach
	public void init() {
		context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		this.callback = context.getBean(Service.class);
		this.repository = context.getBean(StatisticsRepository.class);
		this.callback.setAttemptsBeforeSuccess(1);
	}

    /**
     * <code>close</code>
     * <p>The close method.</p>
     * @see  org.junit.jupiter.api.AfterEach
     */
    @AfterEach
	public void close() {
		if (context != null) {
			context.close();
		}
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
		Object result = callback.service("one");
		RetryStatistics stats = repository.findOne("test");
		// System.err.println(stats);
		assertThat(stats.getStartedCount()).isEqualTo(1);
		assertThat(result).isEqualTo(RECOVERED);
		result = callback.service("two");
		assertThat(result).isEqualTo(RECOVERED);
		assertThat(stats.getRecoveryCount()).describedAs("There should be two recoveries").isEqualTo(2);
		assertThat(stats.getErrorCount()).describedAs("There should only be one error because the circuit is now open")
			.isEqualTo(1);
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
         * <code>repository</code>
         * <p>The repository method.</p>
         * @return  {@link org.springframework.retry.stats.StatisticsRepository} <p>The repository return object is <code>StatisticsRepository</code> type.</p>
         * @see  org.springframework.retry.stats.StatisticsRepository
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public StatisticsRepository repository() {
			return new DefaultStatisticsRepository();
		}

        /**
         * <code>listener</code>
         * <p>The listener method.</p>
         * @param repository {@link org.springframework.retry.stats.StatisticsRepository} <p>The repository parameter is <code>StatisticsRepository</code> type.</p>
         * @see  org.springframework.retry.stats.StatisticsRepository
         * @see  org.springframework.retry.stats.StatisticsListener
         * @see  org.springframework.context.annotation.Bean
         * @return  {@link org.springframework.retry.stats.StatisticsListener} <p>The listener return object is <code>StatisticsListener</code> type.</p>
         */
        @Bean
		public StatisticsListener listener(StatisticsRepository repository) {
			return new StatisticsListener(repository);
		}

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.stats.CircuitBreakerInterceptorStatisticsTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.stats.CircuitBreakerInterceptorStatisticsTests.Service
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Service service() {
			return new Service();
		}

	}

    /**
     * <code>Service</code>
     * <p>The service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class Service {

		private int attemptsBeforeSuccess;

		private Exception exceptionToThrow = new Exception();

		private RetryContext status;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @param input {@link java.lang.String} <p>The input parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.lang.Object
         * @see  org.springframework.retry.annotation.CircuitBreaker
         * @see  java.lang.Exception
         * @return  {@link java.lang.Object} <p>The service return object is <code>Object</code> type.</p>
         * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
         */
        @CircuitBreaker(label = "test", maxAttempts = 1, recover = "recover")
		public Object service(String input) throws Exception {
			this.status = RetrySynchronizationManager.getContext();
			Integer attempts = (Integer) status.getAttribute("attempts");
			if (attempts == null) {
				attempts = 0;
			}
			attempts++;
			this.status.setAttribute("attempts", attempts);
			if (attempts <= this.attemptsBeforeSuccess) {
				throw this.exceptionToThrow;
			}
			return RESULT;
		}

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @param input {@link java.lang.String} <p>The input parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.lang.Object
         * @see  org.springframework.retry.annotation.Recover
         * @return  {@link java.lang.Object} <p>The recover return object is <code>Object</code> type.</p>
         */
        @Recover
		public Object recover(String input) {
			this.status.setAttribute(RECOVERED, true);
			return RECOVERED;
		}

        /**
         * <code>anotherRecover</code>
         * <p>The another recover method.</p>
         * @param input {@link java.lang.Object} <p>The input parameter is <code>Object</code> type.</p>
         * @see  java.lang.Object
         * @see  org.springframework.retry.annotation.Recover
         * @return  {@link java.lang.Object} <p>The another recover return object is <code>Object</code> type.</p>
         */
        @Recover
		public Object anotherRecover(Object input) {
			return null;
		}

        /**
         * <code>isOpen</code>
         * <p>The is open method.</p>
         * @return  boolean <p>The is open return object is <code>boolean</code> type.</p>
         */
        public boolean isOpen() {
			return this.status != null && this.status.getAttribute("open") == Boolean.TRUE;
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
