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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>CompositeRetryPolicyTests</code>
 * <p>The composite retry policy tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class CompositeRetryPolicyTests {

    /**
     * <code>testEmptyPolicies</code>
     * <p>The test empty policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testEmptyPolicies() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThat(policy.canRetry(context)).isTrue();
	}

    /**
     * <code>testTrivialPolicies</code>
     * <p>The test trivial policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTrivialPolicies() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport(), new MockRetryPolicySupport() });
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThat(policy.canRetry(context)).isTrue();
	}

    /**
     * <code>testNonTrivialPolicies</code>
     * <p>The test non trivial policies method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testNonTrivialPolicies() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport(), new MockRetryPolicySupport() {
			public boolean canRetry(RetryContext context) {
				return false;
			}
		} });
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThat(policy.canRetry(context)).isFalse();
	}

    /**
     * <code>testNonTrivialPoliciesWithThrowable</code>
     * <p>The test non trivial policies with throwable method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testNonTrivialPoliciesWithThrowable() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport(), new MockRetryPolicySupport() {
			boolean errorRegistered = false;

			public boolean canRetry(RetryContext context) {
				return !errorRegistered;
			}

			public void registerThrowable(RetryContext context, Throwable throwable) {
				errorRegistered = true;
			}
		} });
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThat(policy.canRetry(context)).isTrue();
		policy.registerThrowable(context, null);
		assertThat(policy.canRetry(context)).describedAs("Should be still able to retry").isFalse();
	}

    /**
     * <code>testNonTrivialPoliciesClose</code>
     * <p>The test non trivial policies close method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testNonTrivialPoliciesClose() {
		final List<String> list = new ArrayList<>();
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport() {
			public void close(RetryContext context) {
				list.add("1");
			}
		}, new MockRetryPolicySupport() {
			public void close(RetryContext context) {
				list.add("2");
			}
		} });
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		policy.close(context);
		assertThat(list).hasSize(2);
	}

    /**
     * <code>testExceptionOnPoliciesClose</code>
     * <p>The test exception on policies close method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testExceptionOnPoliciesClose() {
		final List<String> list = new ArrayList<>();
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport() {
			public void close(RetryContext context) {
				list.add("1");
				throw new RuntimeException("Pah!");
			}
		}, new MockRetryPolicySupport() {
			public void close(RetryContext context) {
				list.add("2");
			}
		} });
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> policy.close(context)).withMessage("Pah!");
		assertThat(list).hasSize(2);
	}

    /**
     * <code>testRetryCount</code>
     * <p>The test retry count method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testRetryCount() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport(), new MockRetryPolicySupport() });
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
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		RetryContext context = policy.open(null);
		RetryContext child = policy.open(context);
		assertThat(context).isNotSameAs(child);
		assertThat(child.getParent()).isSameAs(context);
	}

    /**
     * <code>testOptimistic</code>
     * <p>The test optimistic method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testOptimistic() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setOptimistic(true);
		policy.setPolicies(new RetryPolicy[] { new MockRetryPolicySupport() {
			public boolean canRetry(RetryContext context) {
				return false;
			}
		}, new MockRetryPolicySupport() });
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertThat(policy.canRetry(context)).isTrue();
	}

    /**
     * <code>testMaximumAttemptsForNonSuitablePolicies</code>
     * <p>The test maximum attempts for non suitable policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testMaximumAttemptsForNonSuitablePolicies() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setOptimistic(true);
		policy.setPolicies(new RetryPolicy[] { new NeverRetryPolicy(), new NeverRetryPolicy() });

		assertThat(policy.getMaxAttempts()).isEqualTo(RetryPolicy.NO_MAXIMUM_ATTEMPTS_SET);
	}

    /**
     * <code>testMaximumAttemptsForSuitablePolicies</code>
     * <p>The test maximum attempts for suitable policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testMaximumAttemptsForSuitablePolicies() {
		CompositeRetryPolicy policy = new CompositeRetryPolicy();
		policy.setOptimistic(true);
		policy.setPolicies(
				new RetryPolicy[] { new SimpleRetryPolicy(6), new SimpleRetryPolicy(3), new SimpleRetryPolicy(4) });

		assertThat(policy.getMaxAttempts()).isEqualTo(3);
	}

}
