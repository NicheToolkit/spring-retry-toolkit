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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>DefaultRetryStateTests</code>
 * <p>The default retry state tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class DefaultRetryStateTests {

    /**
     * <code>testDefaultRetryStateObjectBooleanClassifierOfQsuperThrowableBoolean</code>
     * <p>The test default retry state object boolean classifier of qsuper throwable boolean method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testDefaultRetryStateObjectBooleanClassifierOfQsuperThrowableBoolean() {
		DefaultRetryState state = new DefaultRetryState("foo", true, classifiable -> false);
		assertThat(state.getKey()).isEqualTo("foo");
		assertThat(state.isForceRefresh()).isTrue();
		assertThat(state.rollbackFor(null)).isFalse();
	}

    /**
     * <code>testDefaultRetryStateObjectClassifierOfQsuperThrowableBoolean</code>
     * <p>The test default retry state object classifier of qsuper throwable boolean method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testDefaultRetryStateObjectClassifierOfQsuperThrowableBoolean() {
		DefaultRetryState state = new DefaultRetryState("foo", classifiable -> false);
		assertThat(state.getKey()).isEqualTo("foo");
		assertThat(state.isForceRefresh()).isFalse();
		assertThat(state.rollbackFor(null)).isFalse();
	}

    /**
     * <code>testDefaultRetryStateObjectBoolean</code>
     * <p>The test default retry state object boolean method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testDefaultRetryStateObjectBoolean() {
		DefaultRetryState state = new DefaultRetryState("foo", true);
		assertThat(state.getKey()).isEqualTo("foo");
		assertThat(state.isForceRefresh()).isTrue();
		assertThat(state.rollbackFor(null)).isTrue();
	}

    /**
     * <code>testDefaultRetryStateObject</code>
     * <p>The test default retry state object method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testDefaultRetryStateObject() {
		DefaultRetryState state = new DefaultRetryState("foo");
		assertThat(state.getKey()).isEqualTo("foo");
		assertThat(state.isForceRefresh()).isFalse();
		assertThat(state.rollbackFor(null)).isTrue();
	}

}
