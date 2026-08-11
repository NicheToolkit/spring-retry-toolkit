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

package org.springframework.retry.annotation;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.Test;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import java.util.function.BiConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.MapRetryContextCache;
import org.springframework.retry.policy.RetryContextCache;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.setMaxStackTraceElementsDisplayed;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * <code>EnableRetryTests</code>
 * <p>The enable retry tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class EnableRetryTests {

    /**
     * <code>vanilla</code>
     * <p>The vanilla method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void vanilla() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		Service service = context.getBean(Service.class);
		Foo foo = context.getBean(Foo.class);
		assertThat(AopUtils.isAopProxy(foo)).isFalse();
		assertThat(AopUtils.isAopProxy(service)).isTrue();
		service.service();
		assertThat(service.getCount()).isEqualTo(3);
		TestConfiguration config = context.getBean(TestConfiguration.class);
		assertThat(config.listener1).isTrue();
		assertThat(config.listener2).isTrue();
		assertThat(config.twoFirst).isTrue();
		context.close();
	}

    /**
     * <code>multipleMethods</code>
     * <p>The multiple methods method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void multipleMethods() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		MultiService service = context.getBean(MultiService.class);
		service.service();
		assertThat(service.getCount()).isEqualTo(3);
		service.other();
		assertThat(service.getCount()).isEqualTo(4);
		setMaxStackTraceElementsDisplayed(100);
		assertThatIllegalArgumentException().isThrownBy(() -> service.conditional("foo"));
		assertThat(service.getCount()).isEqualTo(7);
		assertThatIllegalArgumentException().isThrownBy(() -> service.conditional("bar"));
		assertThat(service.getCount()).isEqualTo(8);
		context.close();
	}

    /**
     * <code>proxyTargetClass</code>
     * <p>The proxy target class method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void proxyTargetClass() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestProxyConfiguration.class);
		Service service = context.getBean(Service.class);
		assertThat(AopUtils.isCglibProxy(service)).isTrue();
		RecoverableService recoverable = context.getBean(RecoverableService.class);
		recoverable.service();
		assertThat(recoverable.isOtherAdviceCalled()).isTrue();
		context.close();
	}

    /**
     * <code>order</code>
     * <p>The order method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void order() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestOrderConfiguration.class);
		RetryConfiguration config = context.getBean(RetryConfiguration.class);
		assertThat(config.getOrder()).isEqualTo(1);
		context.close();
	}

    /**
     * <code>marker</code>
     * <p>The marker method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void marker() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		Service service = context.getBean(Service.class);
		assertThat(AopUtils.isCglibProxy(service)).isTrue();
		assertThat(service instanceof org.springframework.retry.interceptor.Retryable).isTrue();
		context.close();
	}

    /**
     * <code>recovery</code>
     * <p>The recovery method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void recovery() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		RecoverableService service = context.getBean(RecoverableService.class);
		service.service();
		assertThat(service.getCount()).isEqualTo(3);
		assertThat(service.getCause()).isExactlyInstanceOf(RuntimeException.class);
		assertThatIllegalArgumentException().isThrownBy(() -> service.service());
		assertThat(service.getCount()).isEqualTo(6);
		assertThat(service.getCause()).isExactlyInstanceOf(RuntimeException.class);
		assertThatIllegalStateException().isThrownBy(() -> service.service());
		assertThat(service.getCount()).isEqualTo(7);
		assertThat(service.getCause()).isExactlyInstanceOf(RuntimeException.class);
		context.close();
	}

    /**
     * <code>recoveryWithoutParam</code>
     * <p>The recovery without param method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void recoveryWithoutParam() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestConfiguration.class)) {
			RecoverableService service = context.getBean(RecoverableService.class);
			assertThat(service.serviceWithoutParam()).isEqualTo("test");
		}
	}

    /**
     * <code>recoveryWithParam</code>
     * <p>The recovery with param method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void recoveryWithParam() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				TestConfiguration.class)) {
			RecoverableService service = context.getBean(RecoverableService.class);
			assertThat(service.serviceWithParam("test")).isEqualTo("test");
		}
	}

    /**
     * <code>type</code>
     * <p>The type method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void type() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		RetryableService service = context.getBean(RetryableService.class);
		service.service();
		assertThat(service.getCount()).isEqualTo(3);
		context.close();
	}

    /**
     * <code>excludes</code>
     * <p>The excludes method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void excludes() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		ExcludesService service = context.getBean(ExcludesService.class);
		assertThatIllegalStateException().isThrownBy(() -> service.service());
		assertThat(service.getCount()).isEqualTo(1);
		context.close();
	}

    /**
     * <code>excludesOnly</code>
     * <p>The excludes only method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void excludesOnly() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		ExcludesOnlyService service = context.getBean(ExcludesOnlyService.class);
		service.setExceptionToThrow(new IllegalStateException());
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> service.service());
		assertThat(service.getCount()).isEqualTo(1);

		service.setExceptionToThrow(new IllegalArgumentException());
		service.service();
		assertThat(service.getCount()).isEqualTo(3);

		assertThatExceptionOfType(InstantiationException.class).isThrownBy(service::reThrowAsIs).withMessage("noRetry");

		context.close();
	}

    /**
     * <code>stateful</code>
     * <p>The stateful method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void stateful() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		StatefulService service = context.getBean(StatefulService.class);
		for (int i = 0; i < 3; i++) {
			try {
				service.service(1);
			}
			catch (Exception e) {
				assertThat(e.getMessage()).isEqualTo("Planned");
			}
		}
		assertThat(service.getCount()).isEqualTo(3);
		context.close();
	}

    /**
     * <code>testExternalInterceptor</code>
     * <p>The test external interceptor method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testExternalInterceptor() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		InterceptableService service = context.getBean(InterceptableService.class);
		service.service();
		assertThat(service.getCount()).isEqualTo(5);
		context.close();
	}

    /**
     * <code>testInterface</code>
     * <p>The test interface method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testInterface() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		TheInterface service = context.getBean(TheInterface.class);
		service.service1();
		service.service2();
		assertThat(service.getCount()).isEqualTo(4);
		service.service3();
		assertThat(service.isRecovered()).isTrue();
		context.close();
	}

    /**
     * <code>testInterfaceWithNoRecover</code>
     * <p>The test interface with no recover method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testInterfaceWithNoRecover() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		NoRecoverInterface service = context.getBean(NoRecoverInterface.class);
		service.service();
		assertThat(service.isRecovered()).isTrue();
	}

    /**
     * <code>testImplementation</code>
     * <p>The test implementation method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testImplementation() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		NotAnnotatedInterface service = context.getBean(NotAnnotatedInterface.class);
		service.service1();
		service.service2();
		assertThat(service.getCount()).isEqualTo(5);
		context.close();
	}

    /**
     * <code>testExpression</code>
     * <p>The test expression method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Exception
     * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
     */
    @Test
	public void testExpression() throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
		ExpressionService service = context.getBean(ExpressionService.class);
		service.service1();
		assertThat(service.getCount()).isEqualTo(3);
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> service.service2());
		assertThat(service.getCount()).isEqualTo(4);
		service.service3();
		assertThat(service.getCount()).isEqualTo(9);
		RetryConfiguration config = context.getBean(RetryConfiguration.class);
		AnnotationAwareRetryOperationsInterceptor advice = (AnnotationAwareRetryOperationsInterceptor) new DirectFieldAccessor(
				config)
			.getPropertyValue("advice");
		@SuppressWarnings("unchecked")
		Map<Object, Map<Method, MethodInterceptor>> delegates = (Map<Object, Map<Method, MethodInterceptor>>) new DirectFieldAccessor(
				advice)
			.getPropertyValue("delegates");
		MethodInterceptor interceptor = delegates.get(target(service))
			.get(ExpressionService.class.getDeclaredMethod("service3"));
		RetryTemplate template = (RetryTemplate) new DirectFieldAccessor(interceptor)
			.getPropertyValue("retryOperations");
		DirectFieldAccessor templateAccessor = new DirectFieldAccessor(template);
		ExponentialBackOffPolicy backOff = (ExponentialBackOffPolicy) templateAccessor
			.getPropertyValue("backOffPolicy");
		assertThat(backOff.getInitialInterval()).isEqualTo(1);
		assertThat(backOff.getMaxInterval()).isEqualTo(5);
		assertThat(backOff.getMultiplier()).isEqualTo(1.1);
		SimpleRetryPolicy retryPolicy = (SimpleRetryPolicy) templateAccessor.getPropertyValue("retryPolicy");
		assertThat(retryPolicy.getMaxAttempts()).isEqualTo(5);
		service.service4();
		assertThat(service.getCount()).isEqualTo(11);
		interceptor = delegates.get(target(service)).get(ExpressionService.class.getDeclaredMethod("service4"));
		template = (RetryTemplate) new DirectFieldAccessor(interceptor).getPropertyValue("retryOperations");
		templateAccessor = new DirectFieldAccessor(template);
		FixedBackOffPolicy fbp = (FixedBackOffPolicy) templateAccessor.getPropertyValue("backOffPolicy");
		assertThat(fbp.getBackOffPeriod()).isEqualTo(5000L);
		service.service5();
		assertThat(service.getCount()).isEqualTo(12);
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
		ExpressionService service = context.getBean(ExpressionService.class);
		service.service6();
		RuntimeConfigs runtime = context.getBean(RuntimeConfigs.class);
		verify(runtime, times(6)).getMaxAttempts();
		verify(runtime, times(1)).getInitial();
		verify(runtime, times(2)).getMax();
		verify(runtime, times(2)).getMult();

		RetryConfiguration config = context.getBean(RetryConfiguration.class);
		AnnotationAwareRetryOperationsInterceptor advice = (AnnotationAwareRetryOperationsInterceptor) new DirectFieldAccessor(
				config)
			.getPropertyValue("advice");
		@SuppressWarnings("unchecked")
		Map<Object, Map<Method, MethodInterceptor>> delegates = (Map<Object, Map<Method, MethodInterceptor>>) new DirectFieldAccessor(
				advice)
			.getPropertyValue("delegates");
		MethodInterceptor interceptor = delegates.get(target(service))
			.get(ExpressionService.class.getDeclaredMethod("service6"));
		RetryTemplate template = (RetryTemplate) new DirectFieldAccessor(interceptor)
			.getPropertyValue("retryOperations");
		DirectFieldAccessor templateAccessor = new DirectFieldAccessor(template);
		ExponentialBackOffPolicy backOff = (ExponentialBackOffPolicy) templateAccessor
			.getPropertyValue("backOffPolicy");
		assertThat(backOff.getInitialInterval()).isEqualTo(1000);
		assertThat(backOff.getMaxInterval()).isEqualTo(2000);
		assertThat(backOff.getMultiplier()).isEqualTo(1.2);
		SimpleRetryPolicy retryPolicy = (SimpleRetryPolicy) templateAccessor.getPropertyValue("retryPolicy");
		assertThat(retryPolicy.getMaxAttempts()).isEqualTo(3);
		context.close();
	}

    /**
     * <code>testAdviceUsesQualifiedRetryContextCaches</code>
     * <p>The test advice uses qualified retry context caches method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testAdviceUsesQualifiedRetryContextCaches() {
		testAdvice(QualifiedRetryContextCachesConfiguration.class, (context, advice) -> {
			DirectFieldAccessor directFieldAccessor = new DirectFieldAccessor(advice);
			assertThat(directFieldAccessor.getPropertyValue("retryContextCache"))
				.isEqualTo(context.getBean("retryContextCache"));
			assertThat(directFieldAccessor.getPropertyValue("circuitBreakerRetryContextCache"))
				.isEqualTo(context.getBean("circuitBreakerRetryContextCache"));
		});
	}

    /**
     * <code>testAdviceUsesRetryContextCacheWhenSingleInstance</code>
     * <p>The test advice uses retry context cache when single instance method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testAdviceUsesRetryContextCacheWhenSingleInstance() {
		testAdvice(SingleRetryContextCacheConfiguration.class, (context, advice) -> {
			DirectFieldAccessor directFieldAccessor = new DirectFieldAccessor(advice);
			assertThat(directFieldAccessor.getPropertyValue("retryContextCache"))
				.isEqualTo(context.getBean("customRetryContextCache"));
			assertThat(directFieldAccessor.getPropertyValue("circuitBreakerRetryContextCache"))
				.isNotEqualTo(context.getBean("customRetryContextCache"));
		});
	}

	private void testAdvice(Class<?> configuration,
			BiConsumer<ApplicationContext, AnnotationAwareRetryOperationsInterceptor> assertions) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(configuration);
		try {
			RetryConfiguration config = context.getBean(RetryConfiguration.class);
			AnnotationAwareRetryOperationsInterceptor advice = (AnnotationAwareRetryOperationsInterceptor) new DirectFieldAccessor(
					config)
				.getPropertyValue("advice");
			assertions.accept(context, advice);
		}
		finally {
			context.close();
		}
	}

	private Object target(Object target) {
		if (!AopUtils.isAopProxy(target)) {
			return target;
		}
		try {
			return target(((Advised) target).getTargetSource().getTarget());
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

    /**
     * <code>TestProxyConfiguration</code>
     * <p>The test proxy configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry(proxyTargetClass = true)
	protected static class TestProxyConfiguration {

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.Service
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Service service() {
			return new Service();
		}

        /**
         * <code>recoverable</code>
         * <p>The recoverable method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.RecoverableService} <p>The recoverable return object is <code>RecoverableService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.RecoverableService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RecoverableService recoverable() {
			return new RecoverableService();
		}

        /**
         * <code>bpp</code>
         * <p>The bpp method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.TestProxyConfiguration.AdviceBPP} <p>The bpp return object is <code>AdviceBPP</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.TestProxyConfiguration.AdviceBPP
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public static AdviceBPP bpp() {
			return new AdviceBPP();
		}

        /**
         * <code>AdviceBPP</code>
         * <p>The advice bpp class.</p>
         * @see  org.springframework.beans.factory.config.BeanPostProcessor
         * @see  org.springframework.core.Ordered
         * @author  Cyan (snow22314@outlook.com)
         * @since Jdk1.8
         */
        static class AdviceBPP implements BeanPostProcessor, Ordered {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

				return bean;
			}

			@Override
			public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {

				if (bean instanceof RecoverableService) {
					Advised advised = (Advised) bean;
					advised.addAdvice((MethodInterceptor) invocation -> {
						if (invocation.getMethod().getName().equals("recover")) {
							((RecoverableService) bean).setOtherAdviceCalled();
						}
						return invocation.proceed();
					});
					return bean;
				}
				return bean;
			}

			@Override
			public int getOrder() {
				return Integer.MAX_VALUE;
			}

		}

	}

    /**
     * <code>TestOrderConfiguration</code>
     * <p>The test order configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry(order = 1)
	protected static class TestOrderConfiguration {

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.Service
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Service service() {
			return new Service();
		}

	}

    /**
     * <code>TestConfiguration</code>
     * <p>The test configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry
	protected static class TestConfiguration {

        /**
         * <code>pspc</code>
         * <p>The pspc method.</p>
         * @return  {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer} <p>The pspc return object is <code>PropertySourcesPlaceholderConfigurer</code> type.</p>
         * @see  org.springframework.context.support.PropertySourcesPlaceholderConfigurer
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public static PropertySourcesPlaceholderConfigurer pspc() {
			PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
			Properties properties = new Properties();
			properties.setProperty("one", "1");
			properties.setProperty("five", "5");
			properties.setProperty("onePointOne", "1.1");
			properties.setProperty("retryMethod", "shouldRetry");
			pspc.setProperties(properties);
			return pspc;
		}

        /**
         * <code>listener1</code>
         * <p>The <code>listener1</code> field.</p>
         */
        boolean listener1;

        /**
         * <code>listener2</code>
         * <p>The <code>listener2</code> field.</p>
         */
        boolean listener2;

        /**
         * <code>twoFirst</code>
         * <p>The <code>twoFirst</code> field.</p>
         */
        protected boolean twoFirst;

        /**
         * <code>sleeper</code>
         * <p>The sleeper method.</p>
         * @return  {@link org.springframework.retry.backoff.Sleeper} <p>The sleeper return object is <code>Sleeper</code> type.</p>
         * @see  org.springframework.retry.backoff.Sleeper
         * @see  java.lang.SuppressWarnings
         * @see  org.springframework.context.annotation.Bean
         */
        @SuppressWarnings("serial")
		@Bean
		public Sleeper sleeper() {
			return period -> {
			};
		}

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.Service
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Service service() {
			return new Service();
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
			return new OrderedListener() {

				@Override
				public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {

					TestConfiguration.this.listener1 = true;
					TestConfiguration.this.twoFirst = true;
					return super.open(context, callback);
				}

				@Override
				public int getOrder() {
					return Integer.MAX_VALUE;
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
			return new OrderedListener() {

				private boolean listener1;

				@Override
				public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {

					TestConfiguration.this.listener2 = true;
					TestConfiguration.this.twoFirst = false;
					return super.open(context, callback);
				}

				@Override
				public int getOrder() {
					return Integer.MIN_VALUE;
				}

			};
		}

        /**
         * <code>multiService</code>
         * <p>The multi service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.MultiService} <p>The multi service return object is <code>MultiService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.MultiService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public MultiService multiService() {
			return new MultiService();
		}

        /**
         * <code>recoverable</code>
         * <p>The recoverable method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.RecoverableService} <p>The recoverable return object is <code>RecoverableService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.RecoverableService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RecoverableService recoverable() {
			return new RecoverableService();
		}

        /**
         * <code>retryable</code>
         * <p>The retryable method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.RetryableService} <p>The retryable return object is <code>RetryableService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.RetryableService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public RetryableService retryable() {
			return new RetryableService();
		}

        /**
         * <code>stateful</code>
         * <p>The stateful method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.StatefulService} <p>The stateful return object is <code>StatefulService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.StatefulService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public StatefulService stateful() {
			return new StatefulService();
		}

        /**
         * <code>excludes</code>
         * <p>The excludes method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.ExcludesService} <p>The excludes return object is <code>ExcludesService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.ExcludesService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public ExcludesService excludes() {
			return new ExcludesService();
		}

        /**
         * <code>excludesOnly</code>
         * <p>The excludes only method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.ExcludesOnlyService} <p>The excludes only return object is <code>ExcludesOnlyService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.ExcludesOnlyService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public ExcludesOnlyService excludesOnly() {
			return new ExcludesOnlyService();
		}

        /**
         * <code>retryInterceptor</code>
         * <p>The retry interceptor method.</p>
         * @return  {@link org.aopalliance.intercept.MethodInterceptor} <p>The retry interceptor return object is <code>MethodInterceptor</code> type.</p>
         * @see  org.aopalliance.intercept.MethodInterceptor
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public MethodInterceptor retryInterceptor() {
			return RetryInterceptorBuilder.stateless().maxAttempts(5).build();
		}

        /**
         * <code>serviceWithExternalInterceptor</code>
         * <p>The service with external interceptor method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.InterceptableService} <p>The service with external interceptor return object is <code>InterceptableService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.InterceptableService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public InterceptableService serviceWithExternalInterceptor() {
			return new InterceptableService();
		}

        /**
         * <code>expressionService</code>
         * <p>The expression service method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.ExpressionService} <p>The expression service return object is <code>ExpressionService</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.ExpressionService
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public ExpressionService expressionService() {
			return new ExpressionService();
		}

        /**
         * <code>exceptionChecker</code>
         * <p>The exception checker method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.ExceptionChecker} <p>The exception checker return object is <code>ExceptionChecker</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.ExceptionChecker
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public ExceptionChecker exceptionChecker() {
			return new ExceptionChecker();
		}

        /**
         * <code>integerFiveBean</code>
         * <p>The integer five bean method.</p>
         * @return  {@link java.lang.Integer} <p>The integer five bean return object is <code>Integer</code> type.</p>
         * @see  java.lang.Integer
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Integer integerFiveBean() {
			return 5;
		}

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.Foo} <p>The foo return object is <code>Foo</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.Foo
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Foo foo() {
			return new Foo();
		}

        /**
         * <code>anInterface</code>
         * <p>The an interface method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.TheInterface} <p>The an interface return object is <code>TheInterface</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.TheInterface
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public TheInterface anInterface() {
			return new TheClass();
		}

        /**
         * <code>anInterfaceWithNoRecover</code>
         * <p>The an interface with no recover method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.NoRecoverInterface} <p>The an interface with no recover return object is <code>NoRecoverInterface</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.NoRecoverInterface
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public NoRecoverInterface anInterfaceWithNoRecover() {
			return new NoRecoverClass();
		}

        /**
         * <code>notAnnotatedInterface</code>
         * <p>The not annotated interface method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.NotAnnotatedInterface} <p>The not annotated interface return object is <code>NotAnnotatedInterface</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.NotAnnotatedInterface
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public NotAnnotatedInterface notAnnotatedInterface() {
			return new RetryableImplementation();
		}

        /**
         * <code>runtimeConfigs</code>
         * <p>The runtime configs method.</p>
         * @return  {@link org.springframework.retry.annotation.EnableRetryTests.RuntimeConfigs} <p>The runtime configs return object is <code>RuntimeConfigs</code> type.</p>
         * @see  org.springframework.retry.annotation.EnableRetryTests.RuntimeConfigs
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		RuntimeConfigs runtimeConfigs() {
			return spy(new RuntimeConfigs());
		}

	}

    /**
     * <code>RuntimeConfigs</code>
     * <p>The runtime configs class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class RuntimeConfigs {

        /**
         * <code>count</code>
         * <p>The <code>count</code> field.</p>
         */
        int count = 0;

        /**
         * <code>getMaxAttempts</code>
         * <p>The get max attempts getter method.</p>
         * @return  int <p>The get max attempts return object is <code>int</code> type.</p>
         */
        public int getMaxAttempts() {
			this.count++;
			return 3;
		}

        /**
         * <code>getInitial</code>
         * <p>The get initial getter method.</p>
         * @return  long <p>The get initial return object is <code>long</code> type.</p>
         */
        public long getInitial() {
			this.count++;
			return 1000;
		}

        /**
         * <code>getMax</code>
         * <p>The get max getter method.</p>
         * @return  long <p>The get max return object is <code>long</code> type.</p>
         */
        public long getMax() {
			this.count++;
			return 2000;
		}

        /**
         * <code>getMult</code>
         * <p>The get mult getter method.</p>
         * @return  double <p>The get mult return object is <code>double</code> type.</p>
         */
        public double getMult() {
			this.count++;
			return 1.2;
		}

	}

    /**
     * <code>QualifiedRetryContextCachesConfiguration</code>
     * <p>The qualified retry context caches configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry
	protected static class QualifiedRetryContextCachesConfiguration {

        /**
         * <code>retryContextCache</code>
         * <p>The retry context cache method.</p>
         * @return  {@link org.springframework.retry.policy.RetryContextCache} <p>The retry context cache return object is <code>RetryContextCache</code> type.</p>
         * @see  org.springframework.retry.policy.RetryContextCache
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		RetryContextCache retryContextCache() {
			return new MapRetryContextCache();
		}

        /**
         * <code>circuitBreakerRetryContextCache</code>
         * <p>The circuit breaker retry context cache method.</p>
         * @return  {@link org.springframework.retry.policy.RetryContextCache} <p>The circuit breaker retry context cache return object is <code>RetryContextCache</code> type.</p>
         * @see  org.springframework.retry.policy.RetryContextCache
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		RetryContextCache circuitBreakerRetryContextCache() {
			return new MapRetryContextCache();
		}

	}

    /**
     * <code>SingleRetryContextCacheConfiguration</code>
     * <p>The single retry context cache configuration class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry
	protected static class SingleRetryContextCacheConfiguration {

        /**
         * <code>customRetryContextCache</code>
         * <p>The custom retry context cache method.</p>
         * @return  {@link org.springframework.retry.policy.RetryContextCache} <p>The custom retry context cache return object is <code>RetryContextCache</code> type.</p>
         * @see  org.springframework.retry.policy.RetryContextCache
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		RetryContextCache customRetryContextCache() {
			return new MapRetryContextCache(1024, true);
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
        @Retryable(RuntimeException.class)
		public void service() {
			if (this.count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>MultiService</code>
     * <p>The multi service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class MultiService {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(retryFor = RuntimeException.class)
		public void service() {
			if (this.count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>other</code>
         * <p>The other method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(retryFor = RuntimeException.class)
		public void other() {
			if (this.count++ < 3) {
				throw new RuntimeException("Other");
			}
		}

        /**
         * <code>conditional</code>
         * <p>The conditional method.</p>
         * @param string {@link java.lang.String} <p>The string parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(maxAttemptsExpression = "args[0] == 'foo' ? 3 : 1")
		public void conditional(String string) {
			this.count++;
			throw new IllegalArgumentException("conditional");
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>RecoverableService</code>
     * <p>The recoverable service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class RecoverableService {

		private int count = 0;

		private Throwable cause;

        /**
         * <code>otherAdviceCalled</code>
         * <p>The <code>otherAdviceCalled</code> field.</p>
         */
        boolean otherAdviceCalled;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(retryFor = RuntimeException.class, noRetryFor = IllegalStateException.class,
				notRecoverable = { IllegalArgumentException.class, IllegalStateException.class })
		public void service() {
			if (this.count++ >= 3 && this.count < 7) {
				throw new IllegalArgumentException("Planned");
			}
			else if (this.count > 6) {
				throw new IllegalStateException("Planned");
			}
			else {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @param cause {@link java.lang.Throwable} <p>The cause parameter is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         * @see  org.springframework.retry.annotation.Recover
         */
        @Recover
		public void recover(Throwable cause) {
			this.cause = cause;
		}

        /**
         * <code>serviceWithoutParam</code>
         * <p>The service without param method.</p>
         * @return  {@link java.lang.String} <p>The service without param return object is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(retryFor = RuntimeException.class, recover = "recoverWithoutParam")
		public String serviceWithoutParam() {
			throw new RuntimeException("Planned");
		}

        /**
         * <code>recoverWithoutParam</code>
         * <p>The recover without param method.</p>
         * @return  {@link java.lang.String} <p>The recover without param return object is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Recover
         */
        @Recover
		public String recoverWithoutParam() {
			return "test";
		}

        /**
         * <code>serviceWithParam</code>
         * <p>The service with param method.</p>
         * @param param {@link java.lang.String} <p>The param parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.lang.String} <p>The service with param return object is <code>String</code> type.</p>
         */
        @Retryable(retryFor = RuntimeException.class, recover = "recoverWithParam")
		public String serviceWithParam(String param) {
			throw new RuntimeException("Planned");
		}

        /**
         * <code>recoverWithParam</code>
         * <p>The recover with param method.</p>
         * @param param {@link java.lang.String} <p>The param parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Recover
         * @return  {@link java.lang.String} <p>The recover with param return object is <code>String</code> type.</p>
         */
        @Recover
		public String recoverWithParam(String param) {
			return param;
		}

        /**
         * <code>getCause</code>
         * <p>The get cause getter method.</p>
         * @return  {@link java.lang.Throwable} <p>The get cause return object is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         */
        public Throwable getCause() {
			return this.cause;
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

        /**
         * <code>setOtherAdviceCalled</code>
         * <p>The set other advice called setter method.</p>
         */
        public void setOtherAdviceCalled() {
			this.otherAdviceCalled = true;
		}

        /**
         * <code>isOtherAdviceCalled</code>
         * <p>The is other advice called method.</p>
         * @return  boolean <p>The is other advice called return object is <code>boolean</code> type.</p>
         */
        public boolean isOtherAdviceCalled() {
			return this.otherAdviceCalled;
		}

	}

    /**
     * <code>RetryableService</code>
     * <p>The retryable service class.</p>
     * @see  org.springframework.retry.annotation.Retryable
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Retryable(retryFor = RuntimeException.class)
	protected static class RetryableService {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         */
        public void service() {
			if (this.count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>ExcludesService</code>
     * <p>The excludes service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class ExcludesService {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(retryFor = RuntimeException.class, noRetryFor = IllegalStateException.class)
		public void service() {
			if (this.count++ < 2) {
				throw new IllegalStateException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>ExcludesOnlyService</code>
     * <p>The excludes only service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class ExcludesOnlyService {

		private int count = 0;

		private RuntimeException exceptionToThrow;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(noRetryFor = IllegalStateException.class)
		public void service() {
			if (this.count++ < 2) {
				throw this.exceptionToThrow;
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

        /**
         * <code>setExceptionToThrow</code>
         * <p>The set exception to throw setter method.</p>
         * @param exceptionToThrow {@link java.lang.RuntimeException} <p>The exception to throw parameter is <code>RuntimeException</code> type.</p>
         * @see  java.lang.RuntimeException
         */
        public void setExceptionToThrow(RuntimeException exceptionToThrow) {
			this.exceptionToThrow = exceptionToThrow;
		}

        /**
         * <code>reThrowAsIs</code>
         * <p>The re throw as is method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         * @see  java.lang.InstantiationException
         * @throws InstantiationException {@link java.lang.InstantiationException} <p>The instantiation exception is <code>InstantiationException</code> type.</p>
         */
        @Retryable(noRetryFor = InstantiationException.class, recover = "noRetryRecovery")
		public void reThrowAsIs() throws InstantiationException {
			throw new InstantiationException("noRetry");
		}

        /**
         * <code>noRetryRecovery</code>
         * <p>The no retry recovery method.</p>
         * @param ex {@link java.lang.Throwable} <p>The ex parameter is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         * @see  org.springframework.retry.annotation.Recover
         * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
         */
        @Recover
		public void noRetryRecovery(Throwable ex) throws Throwable {
			throw ex;
		}

	}

    /**
     * <code>StatefulService</code>
     * <p>The stateful service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class StatefulService {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @param value int <p>The value parameter is <code>int</code> type.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(stateful = true)
		public void service(int value) {
			if (this.count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>InterceptableService</code>
     * <p>The interceptable service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class InterceptableService {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(interceptor = "retryInterceptor")
		public void service() {
			if (this.count++ < 4) {
				throw new RuntimeException("Planned");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>ExpressionService</code>
     * <p>The expression service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class ExpressionService {

		private int count = 0;

        /**
         * <code>service1</code>
         * <p>The service 1 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(exceptionExpression = "message.contains('this can be retried')")
		public void service1() {
			if (this.count++ < 2) {
				throw new RuntimeException("this can be retried");
			}
		}

        /**
         * <code>service2</code>
         * <p>The service 2 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(exceptionExpression = "message.contains('this can be retried')")
		public void service2() {
			this.count++;
			throw new RuntimeException("this cannot be retried");
		}

        /**
         * <code>service3</code>
         * <p>The service 3 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(exceptionExpression = "@exceptionChecker.${retryMethod}(#root)", retryFor = RuntimeException.class,
				maxAttemptsExpression = "@integerFiveBean", backoff = @Backoff(delayExpression = "${one}",
						maxDelayExpression = "@integerFiveBean", multiplierExpression = "${onePointOne}"))
		public void service3() {
			if (this.count++ < 8) {
				throw new RuntimeException();
			}
		}

        /**
         * <code>service4</code>
         * <p>The service 4 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(exceptionExpression = "message.contains('this can be retried')",
				backoff = @Backoff(delayExpression = "5000"))
		public void service4() {
			if (this.count++ < 10) {
				throw new RuntimeException("this can be retried");
			}
		}

        /**
         * <code>service5</code>
         * <p>The service 5 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(exceptionExpression = "message.contains('this can be retried')", include = RuntimeException.class)
		public void service5() {
			if (this.count++ < 11) {
				throw new RuntimeException("this can be retried");
			}
		}

        /**
         * <code>service6</code>
         * <p>The service 6 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(maxAttemptsExpression = "@runtimeConfigs.maxAttempts",
				backoff = @Backoff(delayExpression = "@runtimeConfigs.initial",
						maxDelayExpression = "@runtimeConfigs.max", multiplierExpression = "@runtimeConfigs.mult"))
		public void service6() {
			if (this.count++ < 2) {
				throw new RuntimeException("retry");
			}
		}

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>ExceptionChecker</code>
     * <p>The exception checker class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class ExceptionChecker {

        /**
         * <code>shouldRetry</code>
         * <p>The should retry method.</p>
         * @param t {@link java.lang.Throwable} <p>The t parameter is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         * @return  boolean <p>The should retry return object is <code>boolean</code> type.</p>
         */
        public boolean shouldRetry(Throwable t) {
			return true;
		}

	}

	private static class Foo {

	}

    /**
     * <code>TheInterface</code>
     * <p>The the interface interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static interface TheInterface {

        /**
         * <code>service1</code>
         * <p>The service 1 method.</p>
         */
        void service1();

        /**
         * <code>service2</code>
         * <p>The service 2 method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable
		void service2();

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        int getCount();

        /**
         * <code>service3</code>
         * <p>The service 3 method.</p>
         */
        void service3();

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @param e {@link java.lang.Exception} <p>The e parameter is <code>Exception</code> type.</p>
         * @see  java.lang.Exception
         */
        void recover(Exception e);

        /**
         * <code>isRecovered</code>
         * <p>The is recovered method.</p>
         * @return  boolean <p>The is recovered return object is <code>boolean</code> type.</p>
         */
        boolean isRecovered();

	}

    /**
     * <code>TheClass</code>
     * <p>The the class class.</p>
     * @see  org.springframework.retry.annotation.EnableRetryTests.TheInterface
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class TheClass implements TheInterface {

		private int count = 0;

		private boolean recovered;

		@Override
		@Retryable
		public void service1() {
			if (this.count++ < 1) {
				throw new RuntimeException("Planned");
			}
		}

		@Override
		public void service2() {
			if (this.count++ < 3) {
				throw new RuntimeException("Planned");
			}
		}

		@Override
		public int getCount() {
			return this.count;
		}

		@Override
		@Retryable
		public void service3() {
			throw new RuntimeException("planned");
		}

		@Override
		@Recover
		public void recover(Exception e) {
			this.recovered = true;
		}

		@Override
		public boolean isRecovered() {
			return this.recovered;
		}

	}

    /**
     * <code>NoRecoverInterface</code>
     * <p>The no recover interface interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static interface NoRecoverInterface {

        /**
         * <code>service</code>
         * <p>The service method.</p>
         */
        void service();

        /**
         * <code>isRecovered</code>
         * <p>The is recovered method.</p>
         * @return  boolean <p>The is recovered return object is <code>boolean</code> type.</p>
         */
        boolean isRecovered();

	}

    /**
     * <code>NoRecoverClass</code>
     * <p>The no recover class class.</p>
     * @see  org.springframework.retry.annotation.EnableRetryTests.NoRecoverInterface
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class NoRecoverClass implements NoRecoverInterface {

		private boolean recovered;

		@Override
		@Retryable
		public void service() {
			throw new RuntimeException("Planned");
		}

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @param e {@link java.lang.Exception} <p>The e parameter is <code>Exception</code> type.</p>
         * @see  java.lang.Exception
         * @see  org.springframework.retry.annotation.Recover
         */
        @Recover
		public void recover(Exception e) {
			this.recovered = true;
		}

		@Override
		public boolean isRecovered() {
			return this.recovered;
		}

	}

    /**
     * <code>NotAnnotatedInterface</code>
     * <p>The not annotated interface interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static interface NotAnnotatedInterface {

        /**
         * <code>service1</code>
         * <p>The service 1 method.</p>
         */
        void service1();

        /**
         * <code>service2</code>
         * <p>The service 2 method.</p>
         */
        void service2();

        /**
         * <code>getCount</code>
         * <p>The get count getter method.</p>
         * @return  int <p>The get count return object is <code>int</code> type.</p>
         */
        int getCount();

	}

    /**
     * <code>RetryableImplementation</code>
     * <p>The retryable implementation class.</p>
     * @see  org.springframework.retry.annotation.EnableRetryTests.NotAnnotatedInterface
     * @see  org.springframework.retry.annotation.Retryable
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Retryable
	public static class RetryableImplementation implements NotAnnotatedInterface {

		private int count = 0;

		@Override
		public void service1() {
			if (this.count++ < 2) {
				throw new RuntimeException("Planned");
			}
		}

		@Override
		public void service2() {
			if (this.count++ < 4) {
				throw new RuntimeException("Planned");
			}
		}

		@Override
		public int getCount() {
			return this.count;
		}

	}

    /**
     * <code>OrderedListener</code>
     * <p>The ordered listener class.</p>
     * @see  org.springframework.retry.RetryListener
     * @see  org.springframework.core.Ordered
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public abstract static class OrderedListener implements RetryListener, Ordered {

	}

}
