/*
 * Copyright 2012-2023 the original author or authors.
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
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>EnableRetryWithListenersTests</code>
 * <p>The enable retry with listeners tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class EnableRetryWithListenersTests {

    /**
     * <code>vanilla</code>
     * <p>The vanilla method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void vanilla() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		Service service = context.getBean(Service.class);
		service.service();
		assertThat(context.getBean(TestConfiguration.class).count).isEqualTo(1);
		context.close();
	}

    /**
     * <code>overrideListener</code>
     * <p>The override listener method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void overrideListener() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestConfigurationMultipleListeners.class);
		ServiceWithOverriddenListener service = context.getBean(ServiceWithOverriddenListener.class);
		service.service();
		assertThat(context.getBean(TestConfigurationMultipleListeners.class).count1).isEqualTo(1);
		assertThat(context.getBean(TestConfigurationMultipleListeners.class).count2).isEqualTo(0);
		context.close();
	}

    /**
     * <code>excludedListeners</code>
     * <p>The excluded listeners method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void excludedListeners() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestConfigurationExcludedListeners.class);
		ServiceWithExcludedListeners service = context.getBean(ServiceWithExcludedListeners.class);
		service.service();
		assertThat(context.getBean(TestConfigurationExcludedListeners.class).count).isEqualTo(0);
		context.close();
	}

    /**
     * <code>TestConfiguration</code>
     * <p>The test configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry(proxyTargetClass = true)
	protected static class TestConfiguration {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryWithListenersTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryWithListenersTests.Service
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Service service() {
			return new Service();
		}

        /**
         * <code>listener</code>
         * <p>The listener method.</p>
         * @return  {@link org.springframework.retry.RetryListener} <p>The listener return object is <code>RetryListener</code> type.</p>
         * @see  org.springframework.retry.RetryListener
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RetryListener listener() {
			return new RetryListener() {
				@Override
				public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					count++;
				}
			};
		}

	}

    /**
     * <code>TestConfigurationMultipleListeners</code>
     * <p>The test configuration multiple listeners class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry(proxyTargetClass = true)
	protected static class TestConfigurationMultipleListeners {

		private int count1 = 0;

		private int count2 = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryWithListenersTests.ServiceWithOverriddenListener} <p>The service return object is <code>ServiceWithOverriddenListener</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryWithListenersTests.ServiceWithOverriddenListener
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public ServiceWithOverriddenListener service() {
			return new ServiceWithOverriddenListener();
		}

        /**
         * <code>listener1</code>
         * <p>The listener 1 method.</p>
         * @return  {@link org.springframework.retry.RetryListener} <p>The listener 1 return object is <code>RetryListener</code> type.</p>
         * @see  org.springframework.retry.RetryListener
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RetryListener listener1() {
			return new RetryListener() {
				@Override
				public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					count1++;
				}
			};
		}

        /**
         * <code>listener2</code>
         * <p>The listener 2 method.</p>
         * @return  {@link org.springframework.retry.RetryListener} <p>The listener 2 return object is <code>RetryListener</code> type.</p>
         * @see  org.springframework.retry.RetryListener
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RetryListener listener2() {
			return new RetryListener() {
				@Override
				public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					count2++;
				}
			};
		}

	}

    /**
     * <code>TestConfigurationExcludedListeners</code>
     * <p>The test configuration excluded listeners class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry(proxyTargetClass = true)
	protected static class TestConfigurationExcludedListeners {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryWithListenersTests.ServiceWithExcludedListeners} <p>The service return object is <code>ServiceWithExcludedListeners</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryWithListenersTests.ServiceWithExcludedListeners
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public ServiceWithExcludedListeners service() {
			return new ServiceWithExcludedListeners();
		}

        /**
         * <code>listener1</code>
         * <p>The listener 1 method.</p>
         * @return  {@link org.springframework.retry.RetryListener} <p>The listener 1 return object is <code>RetryListener</code> type.</p>
         * @see  org.springframework.retry.RetryListener
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RetryListener listener1() {
			return new RetryListener() {
				@Override
				public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					count++;
				}
			};
		}

        /**
         * <code>listener2</code>
         * <p>The listener 2 method.</p>
         * @return  {@link org.springframework.retry.RetryListener} <p>The listener 2 return object is <code>RetryListener</code> type.</p>
         * @see  org.springframework.retry.RetryListener
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RetryListener listener2() {
			return new RetryListener() {
				@Override
				public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					count++;
				}
			};
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
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(backoff = @Backoff(delay = 1000))
		public void service() {
			if (count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return count;
		}

	}

    /**
     * <code>ServiceWithOverriddenListener</code>
     * <p>The service with overridden listener class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class ServiceWithOverriddenListener {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(backoff = @Backoff(delay = 1000), listeners = "listener1")
		public void service() {
			if (count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return count;
		}

	}

    /**
     * <code>ServiceWithExcludedListeners</code>
     * <p>The service with excluded listeners class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class ServiceWithExcludedListeners {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(backoff = @Backoff(delay = 1000), listeners = "")
		public void service() {
			if (count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return count;
		}

	}

}
