/*
 * Copyright 2023 the original author or authors.
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.retry.RetryContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>RetrySynchronizationManagerNoThreadLocalTests</code>
 * <p>The retry synchronization manager no thread local tests class.</p>
 * @see  org.springframework.retry.support.RetrySynchronizationManagerTests
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetrySynchronizationManagerNoThreadLocalTests extends RetrySynchronizationManagerTests {

    /**
     * <code>before</code>
     * <p>The before method.</p>
     * @see  org.junit.jupiter.api.BeforeAll
     */
    @BeforeAll
	static void before() {
		RetrySynchronizationManager.setUseThreadLocal(false);
	}

    /**
     * <code>after</code>
     * <p>The after method.</p>
     * @see  org.junit.jupiter.api.AfterAll
     */
    @AfterAll
	static void after() {
		RetrySynchronizationManager.setUseThreadLocal(true);
	}

	@Override
	@BeforeEach
	public void setUp() {
		RetrySynchronizationManagerTests.clearAll();
		RetryContext status = RetrySynchronizationManager.getContext();
		assertThat(status).isNull();
	}

}
