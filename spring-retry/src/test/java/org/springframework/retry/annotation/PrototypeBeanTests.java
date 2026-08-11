/*
 * Copyright 2017-2022 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>PrototypeBeanTests</code>
 * <p>The prototype bean tests class.</p>
 * @see  org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SpringJUnitConfig
public class PrototypeBeanTests {

	@Autowired
	private Bar bar1;

	@Autowired
	private Bar bar2;

	@Autowired
	private Foo foo;

    /**
     * <code>testProtoBean</code>
     * <p>The test proto bean method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testProtoBean() {
		this.bar1.foo("one");
		this.bar2.foo("two");
		assertThat(this.foo.recovered).isEqualTo("two");
	}

    /**
     * <code>Config</code>
     * <p>The config class.</p>
     * @see  org.springframework.context.annotation.Configuration
     * @see  org.springframework.retry.annotation.EnableRetry
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Configuration
	@EnableRetry
	public static class Config {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @return  {@link org.springframework.retry.annotation.PrototypeBeanTests.Foo} <p>The foo return object is <code>Foo</code> type.</p>
         * @see  org.springframework.retry.annotation.PrototypeBeanTests.Foo
         * @see  org.springframework.context.annotation.Bean
         */
        @Bean
		public Foo foo() {
			return new Foo();
		}

        /**
         * <code>baz</code>
         * <p>The baz method.</p>
         * @return  {@link org.springframework.retry.annotation.PrototypeBeanTests.Baz} <p>The baz return object is <code>Baz</code> type.</p>
         * @see  org.springframework.retry.annotation.PrototypeBeanTests.Baz
         * @see  org.springframework.context.annotation.Bean
         * @see  org.springframework.context.annotation.Scope
         */
        @Bean
		@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
		public Baz baz() {
			return new Baz();
		}

	}

    /**
     * <code>Foo</code>
     * <p>The foo class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class Foo {

		private String recovered;

        /**
         * <code>demoRun</code>
         * <p>The demo run method.</p>
         * @param bar {@link org.springframework.retry.annotation.PrototypeBeanTests.Bar} <p>The bar parameter is <code>Bar</code> type.</p>
         * @see  org.springframework.retry.annotation.PrototypeBeanTests.Bar
         */
        void demoRun(Bar bar) {
			throw new RuntimeException();
		}

        /**
         * <code>demoRecover</code>
         * <p>The demo recover method.</p>
         * @param instance {@link java.lang.String} <p>The instance parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         */
        void demoRecover(String instance) {
			this.recovered = instance;
		}

	}

    /**
     * <code>Bar</code>
     * <p>The bar interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public interface Bar {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param instance {@link java.lang.String} <p>The instance parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(backoff = @Backoff(0))
		void foo(String instance);

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @see  org.springframework.retry.annotation.Recover
         */
        @Recover
		void bar();

	}

    /**
     * <code>Baz</code>
     * <p>The baz class.</p>
     * @see  org.springframework.retry.annotation.PrototypeBeanTests.Bar
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class Baz implements Bar {

		private String instance;

		@Autowired
		private Foo foo;

		@Override
		public void foo(String instance) {
			this.instance = instance;
			foo.demoRun(this);
		}

		@Override
		public void bar() {
			foo.demoRecover(this.instance);
		}

		@Override
		public String toString() {
			return "Baz [instance=" + this.instance + "]";
		}

	}

}
