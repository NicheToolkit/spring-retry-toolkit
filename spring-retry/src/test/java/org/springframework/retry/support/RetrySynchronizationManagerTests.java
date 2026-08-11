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

package org.springframework.retry.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.retry.RetryContext;
import org.springframework.retry.context.RetryContextSupport;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>RetrySynchronizationManagerTests</code>
 * <p>The retry synchronization manager tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetrySynchronizationManagerTests {

    /**
     * <code>template</code>
     * {@link org.springframework.retry.support.RetryTemplate} <p>The <code>template</code> field.</p>
     * @see  org.springframework.retry.support.RetryTemplate
     */
    RetryTemplate template = new RetryTemplate();

    /**
     * <code>setUp</code>
     * <p>The set up setter method.</p>
     * @see  org.junit.jupiter.api.BeforeEach
     */
    @BeforeEach
	public void setUp() {
		RetrySynchronizationManagerTests.clearAll();
		RetryContext status = RetrySynchronizationManager.getContext();
		assertThat(status).isNull();
	}

    /**
     * <code>testStatusIsStoredByTemplate</code>
     * <p>The test status is stored by template method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testStatusIsStoredByTemplate() {

		RetryContext status = RetrySynchronizationManager.getContext();
		assertThat(status).isNull();

		this.template.execute(retryContext -> {
			RetryContext global = RetrySynchronizationManager.getContext();
			assertThat(retryContext).isNotNull();
			assertThat(retryContext).isEqualTo(global);
			return null;
		});

		status = RetrySynchronizationManager.getContext();
		assertThat(status).isNull();
	}

    /**
     * <code>testStatusRegistration</code>
     * <p>The test status registration method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testStatusRegistration() {
		RetryContext status = new RetryContextSupport(null);
		RetryContext value = RetrySynchronizationManager.register(status);
		assertThat(value).isNull();
		value = RetrySynchronizationManager.register(status);
		assertThat(value).isEqualTo(status);
	}

    /**
     * <code>testClear</code>
     * <p>The test clear method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClear() {
		RetryContext status = new RetryContextSupport(null);
		RetryContext value = RetrySynchronizationManager.register(status);
		assertThat(value).isNull();
		RetrySynchronizationManager.clear();
		value = RetrySynchronizationManager.register(status);
		assertThat(value).isNull();
	}

    /**
     * <code>testParent</code>
     * <p>The test parent method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testParent() {
		RetryContext parent = new RetryContextSupport(null);
		RetryContext child = new RetryContextSupport(parent);
		assertThat(child.getParent()).isSameAs(parent);
	}

    /**
     * <code>clearAll</code>
     * <p>The clear all method.</p>
     * @return  {@link org.springframework.retry.RetryContext} <p>The clear all return object is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     */
    public static RetryContext clearAll() {
		RetryContext result = null;
		RetryContext context = RetrySynchronizationManager.clear();
		while (context != null) {
			result = context;
			context = RetrySynchronizationManager.clear();
		}
		return result;
	}

}
