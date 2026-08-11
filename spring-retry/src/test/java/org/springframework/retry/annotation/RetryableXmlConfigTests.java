/*
 * Copyright 2024-2024 the original author or authors.
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
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>RetryableXmlConfigTests</code>
 * <p>The retryable xml config tests class.</p>
 * @see  org.springframework.test.context.junit.jupiter.SpringJUnitConfig
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SpringJUnitConfig
public class RetryableXmlConfigTests {

    /**
     * <code>service</code>
     * {@link org.springframework.retry.annotation.RetryableXmlConfigTests.Service} <p>The <code>service</code> field.</p>
     * @see  org.springframework.retry.annotation.RetryableXmlConfigTests.Service
     * @see  org.springframework.beans.factory.annotation.Autowired
     */
    @Autowired
	Service service;

    /**
     * <code>serviceCallIsRetied</code>
     * <p>The service call is retied method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	void serviceCallIsRetied() {
		this.service.service();
		assertThat(service.getCount()).isEqualTo(3);
	}

    /**
     * <code>Service</code>
     * <p>The service class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class Service {

		private int count = 0;

        /**
         * <code>service</code>
         * <p>The service method.</p>
         */
        @Retryable
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

}
