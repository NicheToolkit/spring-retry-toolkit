/*
 * Copyright 2019 the original author or authors.
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>DontRetryRecovererTests</code>
 * <p>The dont retry recoverer tests class.</p>
 * @see  org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SpringJUnitConfig
public class DontRetryRecovererTests {

    /**
     * <code>dontRetry</code>
     * <p>The dont retry method.</p>
     * @param service {@link org.springframework.retry.annotation.DontRetryRecovererTests.Service} <p>The service parameter is <code>Service</code> type.</p>
     * @see  org.springframework.retry.annotation.DontRetryRecovererTests.Service
     * @see  org.springframework.beans.factory.annotation.Autowired
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	void dontRetry(@Autowired Service service) {
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> service.foo("x")).withMessage("test");
		assertThat(service.getCallCount()).isEqualTo(3);
		assertThat(service.getRecoverCount()).isEqualTo(1);
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
         * <code>service</code>
         * <p>The service method.</p>
         * @return  {@link org.springframework.retry.annotation.DontRetryRecovererTests.Service} <p>The service return object is <code>Service</code> type.</p>
         * @see  org.springframework.retry.annotation.DontRetryRecovererTests.Service
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
     * @see  org.springframework.retry.annotation.Retryable
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Retryable
	public static class Service {

        /**
         * <code>callCount</code>
         * <p>The <code>callCount</code> field.</p>
         */
        int callCount;

        /**
         * <code>recoverCount</code>
         * <p>The <code>recoverCount</code> field.</p>
         */
        int recoverCount;

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param in {@link java.lang.String} <p>The in parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         */
        public void foo(String in) {
			callCount++;
			throw new RuntimeException();
		}

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @param ex {@link java.lang.Exception} <p>The ex parameter is <code>Exception</code> type.</p>
         * @param in {@link java.lang.String} <p>The in parameter is <code>String</code> type.</p>
         * @see  java.lang.Exception
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Recover
         */
        @Recover
		public void recover(Exception ex, String in) {
			this.recoverCount++;
			throw new RuntimeException("test");
		}

        /**
         * <code>getCallCount</code>
         * <p>The get call count getter method.</p>
         * @return  int <p>The get call count return object is <code>int</code> type.</p>
         */
        public int getCallCount() {
			return callCount;
		}

        /**
         * <code>getRecoverCount</code>
         * <p>The get recover count getter method.</p>
         * @return  int <p>The get recover count return object is <code>int</code> type.</p>
         */
        public int getRecoverCount() {
			return recoverCount;
		}

	}

}
