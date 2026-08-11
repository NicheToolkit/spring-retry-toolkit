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

package org.springframework.retry.annotation;

import java.util.Map;
import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.Test;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.retry.util.test.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>CircuitBreakerTests</code>
 * <p>The circuit breaker tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class CircuitBreakerTests {

    /**
     * <code>vanilla</code>
     * <p>The vanilla method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Exception
     * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
     */
    @Test
	public void vanilla() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		Service service = context.getBean(Service.class);
		assertThat(AopUtils.isAopProxy(service)).isTrue();
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> service.service());
		assertThat((Boolean) service.getContext().getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN)).isFalse();
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> service.service());
		assertThat((Boolean) service.getContext().getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN)).isFalse();
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> service.service());
		assertThat((Boolean) service.getContext().getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN)).isTrue();
		assertThat(service.getCount()).isEqualTo(3);
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> service.service());
		// Not called again once circuit is open
		assertThat(service.getCount()).isEqualTo(3);
		service.expressionService();
		assertThat(service.getCount()).isEqualTo(4);
		service.expressionService2();
		assertThat(service.getCount()).isEqualTo(5);
		Advised advised = (Advised) service;
		Advisor advisor = advised.getAdvisors()[0];
		Map<?, ?> delegates = (Map<?, ?>) new DirectFieldAccessor(advisor).getPropertyValue("advice.delegates");
		assertThat(delegates).hasSize(1);
		Map<?, ?> methodMap = (Map<?, ?>) delegates.values().iterator().next();
		MethodInterceptor interceptor = (MethodInterceptor) methodMap
			.get(Service.class.getDeclaredMethod("expressionService"));
		DirectFieldAccessor accessor = new DirectFieldAccessor(interceptor);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.delegate.maxAttempts")).isEqualTo(8);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.openTimeout")).isEqualTo(19000L);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.resetTimeout")).isEqualTo(20000L);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.delegate.expression.expression"))
			.isEqualTo("#root instanceof RuntimeExpression");

		interceptor = (MethodInterceptor) methodMap.get(Service.class.getDeclaredMethod("expressionService2"));
		accessor = new DirectFieldAccessor(interceptor);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.delegate.maxAttempts")).isEqualTo(10);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.openTimeout")).isEqualTo(10000L);
		assertThat(accessor.getPropertyValue("retryOperations.retryPolicy.resetTimeout")).isEqualTo(20000L);
		context.close();
	}

    /**
     * <code>runtimeExpressions</code>
     * <p>The runtime expressions method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Exception
     * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
     */
    @Test
	void runtimeExpressions() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		Service service = context.getBean(Service.class);
		assertThat(AopUtils.isAopProxy(service)).isTrue();
		service.expressionService3();
		assertThat(service.getCount()).isEqualTo(1);
		Advised advised = (Advised) service;
		Advisor advisor = advised.getAdvisors()[0];
		Map<?, ?> delegates = (Map<?, ?>) new DirectFieldAccessor(advisor).getPropertyValue("advice.delegates");
		assertThat(delegates).hasSize(1);
		Map<?, ?> methodMap = (Map<?, ?>) delegates.values().iterator().next();
		MethodInterceptor interceptor = (MethodInterceptor) methodMap
			.get(Service.class.getDeclaredMethod("expressionService3"));
		Supplier<?> maxAttempts = TestUtils.getPropertyValue(interceptor,
				"retryOperations.retryPolicy.delegate.maxAttemptsSupplier", Supplier.class);
		assertThat(maxAttempts).isNotNull();
		assertThat(maxAttempts.get()).isEqualTo(10);
		CircuitBreakerRetryPolicy policy = TestUtils.getPropertyValue(interceptor, "retryOperations.retryPolicy",
				CircuitBreakerRetryPolicy.class);
		Supplier<?> openTO = TestUtils.getPropertyValue(policy, "openTimeoutSupplier", Supplier.class);
		assertThat(openTO).isNotNull();
		assertThat(openTO.get()).isEqualTo(10000L);
		Supplier<?> resetTO = TestUtils.getPropertyValue(policy, "resetTimeoutSupplier", Supplier.class);
		assertThat(resetTO).isNotNull();
		assertThat(resetTO.get()).isEqualTo(20000L);
		RetryContext ctx = service.getContext();
		assertThat(TestUtils.getPropertyValue(ctx, "openWindow")).isEqualTo(10000L);
		assertThat(TestUtils.getPropertyValue(ctx, "timeout")).isEqualTo(20000L);

		assertThatExceptionOfType(ExhaustedRetryException.class).isThrownBy(service::exhaustedRetryService);

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(service::noWrapExhaustedRetryService)
			.withMessage("Planned");

		context.close();
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
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.CircuitBreakerTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.annotation.CircuitBreakerTests.Service
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Service service() {
			return new ServiceImpl();
		}

        /**
         * <code>configs</code>
         * <p>The configs method.</p>
         * @return  {@link org.springframework.retry.annotation.CircuitBreakerTests.Configs} <p>The configs return object is <code>Configs</code> type.</p>
         * @see  org.springframework.retry.annotation.CircuitBreakerTests.Configs
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		Configs configs() {
			return new Configs();
		}

	}

    /**
     * <code>Configs</code>
     * <p>The configs class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class Configs {

        /**
         * <code>maxAttempts</code>
         * <p>The <code>maxAttempts</code> field.</p>
         */
        public int maxAttempts = 10;

        /**
         * <code>openTimeout</code>
         * <p>The <code>openTimeout</code> field.</p>
         */
        public long openTimeout = 10000;

        /**
         * <code>resetTimeout</code>
         * <p>The <code>resetTimeout</code> field.</p>
         */
        public long resetTimeout = 20000;

	}

    /**
     * <code>Service</code>
     * <p>The service interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    interface Service {

        /**
         * <code>service</code>
         * <p>The service method.</p>
         */
        void service();

        /**
         * <code>expressionService</code>
         * <p>The expression service method.</p>
         */
        void expressionService();

        /**
         * <code>expressionService2</code>
         * <p>The expression service 2 method.</p>
         */
        void expressionService2();

        /**
         * <code>expressionService3</code>
         * <p>The expression service 3 method.</p>
         */
        void expressionService3();

        /**
         * <code>exhaustedRetryService</code>
         * <p>The exhausted retry service method.</p>
         */
        void exhaustedRetryService();

        /**
         * <code>noWrapExhaustedRetryService</code>
         * <p>The no wrap exhausted retry service method.</p>
         */
        void noWrapExhaustedRetryService();

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        int getCount();

        /**
         * <code>getContext</code>
         * <p>The get context getter method.</p>
         * @return  {@link org.springframework.retry.RetryContext} <p>The get context return object is <code>RetryContext</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         */
        RetryContext getContext();

	}

    /**
     * <code>ServiceImpl</code>
     * <p>The service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class ServiceImpl implements Service {

        /**
         * <code>count</code>
         * <p>The <code>count</code> field.</p>
         */
        int count = 0;

        /**
         * <code>context</code>
         * {@link org.springframework.retry.RetryContext} <p>The <code>context</code> field.</p>
         * @see  org.springframework.retry.RetryContext
         */
        RetryContext context;

		@Override
		@CircuitBreaker(retryFor = RuntimeException.class)
		public void service() {
			this.context = RetrySynchronizationManager.getContext();
			if (this.count++ < 5) {
				throw new RuntimeException("Planned");
			}
		}

		@Override
		@CircuitBreaker(maxAttemptsExpression = "#{2 * ${foo:4}}", openTimeoutExpression = "#{${bar:19}000}",
				resetTimeoutExpression = "#{${baz:20}000}", exceptionExpression = "#root instanceof RuntimeExpression")
		public void expressionService() {
			this.count++;
		}

		@Override
		@CircuitBreaker(maxAttemptsExpression = "#{@configs.maxAttempts}",
				openTimeoutExpression = "#{@configs.openTimeout}", resetTimeoutExpression = "#{@configs.resetTimeout}")
		public void expressionService2() {
			this.count++;
		}

		@Override
		@CircuitBreaker(maxAttemptsExpression = "@configs.maxAttempts", openTimeoutExpression = "@configs.openTimeout",
				resetTimeoutExpression = "@configs.resetTimeout")
		public void expressionService3() {
			this.context = RetrySynchronizationManager.getContext();
			this.count++;
		}

		@Override
		@CircuitBreaker
		public void exhaustedRetryService() {
			throw new RuntimeException("Planned");
		}

		@Override
		@CircuitBreaker(throwLastExceptionOnExhausted = true)
		public void noWrapExhaustedRetryService() {
			throw new RuntimeException("Planned");
		}

		@Override
		public RetryContext getContext() {
			return this.context;
		}

		@Override
		public int getCount() {
			return this.count;
		}

	}

}
