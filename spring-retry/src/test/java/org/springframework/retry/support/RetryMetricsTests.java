/*
 * Copyright 2024-2025 the original author or authors.
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

package org.springframework.retry.support;

import java.util.concurrent.CompletableFuture;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * <code>RetryMetricsTests</code>
 * <p>The retry metrics tests class.</p>
 * @see  org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SpringJUnitConfig
public class RetryMetricsTests {

	/**
	 * <code>meterRegistry</code>
	 * {@link io.micrometer.core.instrument.MeterRegistry} <p>The <code>meterRegistry</code> field.</p>
	 * @see  io.micrometer.core.instrument.MeterRegistry
	 * @see  org.springframework.beans.factory.annotation.Autowired
	 */
	@Autowired
	MeterRegistry meterRegistry;

	/**
	 * <code>service</code>
	 * {@link org.springframework.retry.support.RetryMetricsTests.Service} <p>The <code>service</code> field.</p>
	 * @see  org.springframework.retry.support.RetryMetricsTests.Service
	 * @see  org.springframework.beans.factory.annotation.Autowired
	 */
	@Autowired
	Service service;

	/**
	 * <code>metricsRetryListener</code>
	 * {@link org.springframework.retry.support.MetricsRetryListener} <p>The <code>metricsRetryListener</code> field.</p>
	 * @see  org.springframework.retry.support.MetricsRetryListener
	 * @see  org.springframework.beans.factory.annotation.Autowired
	 */
	@Autowired
	MetricsRetryListener metricsRetryListener;

	/**
	 * <code>metricsAreCollectedForRetryable</code>
	 * <p>The metrics are collected for retryable method.</p>
	 * @see  org.junit.jupiter.api.Test
	 */
	@Test
	void metricsAreCollectedForRetryable() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.afterPropertiesSet();

		CompletableFuture<?> future1 = CompletableFuture.runAsync(
				() -> assertThatNoException().isThrownBy(this.service::service1),
				executor
		);
		CompletableFuture<?> future2 = CompletableFuture.runAsync(
				() -> assertThatNoException().isThrownBy(this.service::service1),
				executor
		);
		CompletableFuture<?> future3 = CompletableFuture.runAsync(
				() -> assertThatNoException().isThrownBy(this.service::service2),
				executor
		);
		CompletableFuture<?> future4 = CompletableFuture.runAsync(
				() -> assertThatExceptionOfType(RetryException.class).isThrownBy(this.service::service3),
				executor
		);

		CompletableFuture.allOf(future1, future2, future3, future4).join();

		assertThat(this.meterRegistry.get(MetricsRetryListener.TIMER_NAME)
			.tags(Tags.of("name", "org.springframework.retry.support.RetryMetricsTests$Service.service1", "retry.count",
					"0", "exception", "none"))
			.timer()
			.count()).isEqualTo(2);

		assertThat(this.meterRegistry.get(MetricsRetryListener.TIMER_NAME)
			.tags(Tags.of("name", "org.springframework.retry.support.RetryMetricsTests$Service.service2", "retry.count",
					"2", "exception", "none"))
			.timer()
			.count()).isEqualTo(1);

		assertThat(this.meterRegistry.get(MetricsRetryListener.TIMER_NAME)
			.tags(Tags.of("name", "org.springframework.retry.support.RetryMetricsTests$Service.service3", "retry.count",
					"3", "exception", "RetryException"))
			.timer()
			.count()).isEqualTo(1);

		executor.destroy();
	}

	/**
	 * <code>labelFallbackToClassName</code>
	 * <p>The label fallback to class name method.</p>
	 * @see  org.junit.jupiter.api.Test
	 */
	@Test
	void labelFallbackToClassName() {
		SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
		RetryContext retryContext = simpleRetryPolicy.open(null);
		RetryCallback<Object, Throwable> retryCallback = context -> null;
		this.metricsRetryListener.open(retryContext, retryCallback);
		this.metricsRetryListener.close(retryContext, retryCallback, null);

		assertThat(this.meterRegistry.get(MetricsRetryListener.TIMER_NAME)
			.tags(Tags.of("name", retryCallback.getClass().getName(), "retry.count", "0", "exception", "none"))
			.timer()
			.count()).isEqualTo(1);

	}

	/**
	 * <code>TestConfiguration</code>
	 * <p>The test configuration class.</p>
	 * @see  org.springframework.context.annotation.Configuration
	 * @see  org.springframework.retry.annotation.EnableRetry
	 * @author  Cyan (snow22314@outlook.com)
	 * @since Jdk1.8
	 */
	@Configuration(proxyBeanMethods = false)
	@EnableRetry
	public static class TestConfiguration {

		/**
		 * <code>meterRegistry</code>
		 * <p>The meter registry method.</p>
		 * @return  {@link io.micrometer.core.instrument.MeterRegistry} <p>The meter registry return object is <code>MeterRegistry</code> type.</p>
		 * @see  io.micrometer.core.instrument.MeterRegistry
		 * @see  org.springframework.context.annotation.Bean
		 */
		@Bean
		MeterRegistry meterRegistry() {
			return new SimpleMeterRegistry();
		}

		/**
		 * <code>metricsRetryListener</code>
		 * <p>The metrics retry listener method.</p>
		 * @param meterRegistry {@link io.micrometer.core.instrument.MeterRegistry} <p>The meter registry parameter is <code>MeterRegistry</code> type.</p>
		 * @see  io.micrometer.core.instrument.MeterRegistry
		 * @see  org.springframework.retry.support.MetricsRetryListener
		 * @see  org.springframework.context.annotation.Bean
		 * @return  {@link org.springframework.retry.support.MetricsRetryListener} <p>The metrics retry listener return object is <code>MetricsRetryListener</code> type.</p>
		 */
		@Bean
		MetricsRetryListener metricsRetryListener(MeterRegistry meterRegistry) {
			return new MetricsRetryListener(meterRegistry);
		}

		/**
		 * <code>service</code>
		 * <p>The service method.</p>
		 * @return  {@link org.springframework.retry.support.RetryMetricsTests.Service} <p>The service return object is <code>Service</code> type.</p>
		 * @see  org.springframework.retry.support.RetryMetricsTests.Service
		 * @see  org.springframework.context.annotation.Bean
		 */
		@Bean
		Service service() {
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

		private int count = 0;

		/**
		 * <code>service1</code>
		 * <p>The service 1 method.</p>
		 * @see  org.springframework.retry.annotation.Retryable
		 */
		@Retryable
		public void service1() {

		}

		/**
		 * <code>service2</code>
		 * <p>The service 2 method.</p>
		 * @see  org.springframework.retry.annotation.Retryable
		 */
		@Retryable
		public void service2() {
			if (count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

		/**
		 * <code>service3</code>
		 * <p>The service 3 method.</p>
		 * @see  org.springframework.retry.annotation.Retryable
		 */
		@Retryable
		public void service3() {
			throw new RetryException("Planned");
		}

	}

}
