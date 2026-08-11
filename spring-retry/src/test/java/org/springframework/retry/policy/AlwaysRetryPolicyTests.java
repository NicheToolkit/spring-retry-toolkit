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

package org.springframework.retry.policy;

import org.junit.jupiter.api.Test;

import org.springframework.retry.RetryContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>AlwaysRetryPolicyTests</code>
 * <p>The always retry policy tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class AlwaysRetryPolicyTests {

    /**
     * <code>testSimpleOperations</code>
     * <p>The test simple operations method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSimpleOperations() {
		AlwaysRetryPolicy policy = new AlwaysRetryPolicy();
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThat(policy.canRetry(context)).isTrue();
		policy.registerThrowable(context, null);
		assertThat(policy.canRetry(context)).isTrue();
		policy.close(context);
		assertThat(policy.canRetry(context)).isTrue();
	}

    /**
     * <code>testRetryCount</code>
     * <p>The test retry count method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testRetryCount() {
		AlwaysRetryPolicy policy = new AlwaysRetryPolicy();
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		policy.registerThrowable(context, null);
		assertThat(context.getRetryCount()).isEqualTo(0);
		policy.registerThrowable(context, new RuntimeException("foo"));
		assertThat(context.getRetryCount()).isEqualTo(1);
		assertThat(context.getLastThrowable().getMessage()).isEqualTo("foo");
	}

    /**
     * <code>testParent</code>
     * <p>The test parent method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testParent() {
		AlwaysRetryPolicy policy = new AlwaysRetryPolicy();
		RetryContext context = policy.open(null);
		RetryContext child = policy.open(context);
		assertThat(context).isNotSameAs(child);
		assertThat(child.getParent()).isSameAs(context);
	}

}
