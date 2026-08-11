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

import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <code>ExceptionClassifierRetryPolicyTests</code>
 * <p>The exception classifier retry policy tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class ExceptionClassifierRetryPolicyTests {

	private final ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();

    /**
     * <code>testDefaultPolicies</code>
     * <p>The test default policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testDefaultPolicies() {
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
	}

    /**
     * <code>testTrivialPolicies</code>
     * <p>The test trivial policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTrivialPolicies() {
		policy.setPolicyMap(Collections.<Class<? extends Throwable>, RetryPolicy>singletonMap(Exception.class,
				new MockRetryPolicySupport()));
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
		assertTrue(policy.canRetry(context));
	}

    /**
     * <code>testNullPolicies</code>
     * <p>The test null policies method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testNullPolicies() {
		policy.setPolicyMap(new HashMap<>());
		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();
	}

    /**
     * <code>testNullContext</code>
     * <p>The test null context method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testNullContext() {
		policy.setPolicyMap(Collections.<Class<? extends Throwable>, RetryPolicy>singletonMap(Exception.class,
				new NeverRetryPolicy()));

		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();

		assertTrue(policy.canRetry(context));
	}

    /**
     * <code>testClassifierOperates</code>
     * <p>The test classifier operates method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testClassifierOperates() {

		RetryContext context = policy.open(null);
		assertThat(context).isNotNull();

		assertTrue(policy.canRetry(context));
		policy.registerThrowable(context, new IllegalArgumentException());
		assertFalse(policy.canRetry(context)); // NeverRetryPolicy is the
		// default

		policy.setExceptionClassifier(throwable -> {
			if (throwable != null) {
				return new AlwaysRetryPolicy();
			}
			return new NeverRetryPolicy();
		});

		// The context saves the classifier, so changing it now has no effect
		assertFalse(policy.canRetry(context));
		policy.registerThrowable(context, new IllegalArgumentException());
		assertFalse(policy.canRetry(context));

		// But now the classifier will be active in the new context...
		context = policy.open(null);
		assertTrue(policy.canRetry(context));
		policy.registerThrowable(context, new IllegalArgumentException());
		assertTrue(policy.canRetry(context));

	}

    /**
     * <code>count</code>
     * <p>The <code>count</code> field.</p>
     */
    int count = 0;

    /**
     * <code>testClose</code>
     * <p>The test close method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testClose() {
		policy.setExceptionClassifier(throwable -> new MockRetryPolicySupport() {
			public void close(RetryContext context) {
				count++;
			}
		});
		RetryContext context = policy.open(null);

		// The mapped (child) policy hasn't been used yet, so if we close now
		// we don't incur the possible expense of creating the child context.
		policy.close(context);
		assertThat(count).isEqualTo(0); // not classified yet
		// This forces a child context to be created and the child policy is
		// then closed
		policy.registerThrowable(context, new IllegalStateException());
		policy.close(context);
		assertThat(count).isEqualTo(1); // now classified
	}

    /**
     * <code>testRetryCount</code>
     * <p>The test retry count method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testRetryCount() {
		ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
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
		ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
		RetryContext context = policy.open(null);
		RetryContext child = policy.open(context);
		assertThat(context).isNotSameAs(child);
		assertThat(child.getParent()).isSameAs(context);
	}

}
