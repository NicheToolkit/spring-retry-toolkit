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

package org.springframework.retry.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.BinaryExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.policy.PredicateRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.springframework.retry.util.test.TestUtils.getPropertyValue;

/**
 * <code>RetryTemplateBuilderTests</code>
 * <p>The retry template builder tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetryTemplateBuilderTests {

	/* ---------------- Mixed tests -------------- */

    /**
     * <code>testDefaultBehavior</code>
     * <p>The test default behavior method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testDefaultBehavior() {
		RetryTemplate template = RetryTemplate.builder().build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertDefaultClassifier(policyTuple);
		assertThat(policyTuple.baseRetryPolicy).isInstanceOf(MaxAttemptsRetryPolicy.class);
		assertDefaultClassifier(policyTuple);

		assertThat(getPropertyValue(template, "throwLastExceptionOnExhausted", Boolean.class)).isFalse();
		assertThat(getPropertyValue(template, "retryContextCache")).isNotNull();
		assertThat(getPropertyValue(template, "listeners", RetryListener[].class).length).isEqualTo(0);

		assertThat(getPropertyValue(template, "backOffPolicy")).isInstanceOf(NoBackOffPolicy.class);
	}

    /**
     * <code>testBasicCustomization</code>
     * <p>The test basic customization method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testBasicCustomization() {
		RetryListener listener1 = mock(RetryListener.class);
		RetryListener listener2 = mock(RetryListener.class);

		RetryTemplate template = RetryTemplate.builder()
			.maxAttempts(10)
			.exponentialBackoff(99, 1.5, 1717)
			.retryOn(IOException.class)
			.retryOn(Collections.<Class<? extends Throwable>>singletonList(IllegalArgumentException.class))
			.traversingCauses()
			.withListener(listener1)
			.withListeners(Collections.singletonList(listener2))
			.build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertThat(policyTuple.exceptionClassifierRetryPolicy).isInstanceOf(BinaryExceptionClassifierRetryPolicy.class);
		BinaryExceptionClassifierRetryPolicy retryPolicy = (BinaryExceptionClassifierRetryPolicy) policyTuple.exceptionClassifierRetryPolicy;
		BinaryExceptionClassifier classifier = retryPolicy.getExceptionClassifier();
		assertThat(classifier.classify(new FileNotFoundException())).isTrue();
		assertThat(classifier.classify(new IllegalArgumentException())).isTrue();
		assertThat(classifier.classify(new RuntimeException())).isFalse();
		assertThat(classifier.classify(new OutOfMemoryError())).isFalse();

		assertThat(policyTuple.baseRetryPolicy instanceof MaxAttemptsRetryPolicy).isTrue();
		assertThat(((MaxAttemptsRetryPolicy) policyTuple.baseRetryPolicy).getMaxAttempts()).isEqualTo(10);

		List<RetryListener> listeners = Arrays.asList(getPropertyValue(template, "listeners", RetryListener[].class));
		assertThat(listeners).hasSize(2);
		assertThat(listeners.contains(listener1)).isTrue();
		assertThat(listeners.contains(listener2)).isTrue();

		assertThat(getPropertyValue(template, "backOffPolicy")).isInstanceOf(ExponentialBackOffPolicy.class);
	}

	/* ---------------- Retry policy -------------- */

    /**
     * <code>testFailOnRetryPoliciesConflict</code>
     * <p>The test fail on retry policies conflict method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnRetryPoliciesConflict() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RetryTemplate.builder().maxAttempts(3).withTimeout(1000).build());
	}

    /**
     * <code>testTimeoutPolicy</code>
     * <p>The test timeout policy method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.SuppressWarnings
     */
    @Test
	@SuppressWarnings("removal")
	public void testTimeoutPolicy() {
		RetryTemplate template = RetryTemplate.builder().withTimeout(10000).build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertDefaultClassifier(policyTuple);

		assertThat(policyTuple.baseRetryPolicy).isInstanceOf(TimeoutRetryPolicy.class);
		assertThat(((TimeoutRetryPolicy) policyTuple.baseRetryPolicy).getTimeout()).isEqualTo(10000);
	}

    /**
     * <code>testTimeoutMillis</code>
     * <p>The test timeout millis method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTimeoutMillis() {
		RetryTemplate template = RetryTemplate.builder().withTimeout(10000).build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertDefaultClassifier(policyTuple);

		assertThat(policyTuple.baseRetryPolicy).isInstanceOf(TimeoutRetryPolicy.class);
		assertThat(((TimeoutRetryPolicy) policyTuple.baseRetryPolicy).getTimeout()).isEqualTo(10000);
	}

    /**
     * <code>testTimeoutDuration</code>
     * <p>The test timeout duration method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTimeoutDuration() {
		RetryTemplate template = RetryTemplate.builder().withTimeout(Duration.ofSeconds(3)).build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertDefaultClassifier(policyTuple);

		assertThat(policyTuple.baseRetryPolicy).isInstanceOf(TimeoutRetryPolicy.class);
		assertThat(((TimeoutRetryPolicy) policyTuple.baseRetryPolicy).getTimeout()).isEqualTo(3000);
	}

    /**
     * <code>testInfiniteRetry</code>
     * <p>The test infinite retry method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testInfiniteRetry() {
		RetryTemplate template = RetryTemplate.builder().infiniteRetry().build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertDefaultClassifier(policyTuple);

		assertThat(policyTuple.baseRetryPolicy).isInstanceOf(AlwaysRetryPolicy.class);
	}

    /**
     * <code>testCustomPolicy</code>
     * <p>The test custom policy method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCustomPolicy() {
		RetryPolicy customPolicy = mock(RetryPolicy.class);

		RetryTemplate template = RetryTemplate.builder().customPolicy(customPolicy).build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);

		assertDefaultClassifier(policyTuple);
		assertThat(policyTuple.baseRetryPolicy).isEqualTo(customPolicy);
	}

	private void assertDefaultClassifier(PolicyTuple policyTuple) {
		assertThat(policyTuple.exceptionClassifierRetryPolicy).isInstanceOf(BinaryExceptionClassifierRetryPolicy.class);
		BinaryExceptionClassifierRetryPolicy retryPolicy = (BinaryExceptionClassifierRetryPolicy) policyTuple.exceptionClassifierRetryPolicy;
		BinaryExceptionClassifier classifier = retryPolicy.getExceptionClassifier();
		assertThat(classifier.classify(new Exception())).isTrue();
		assertThat(classifier.classify(new Exception(new Error()))).isTrue();
		assertThat(classifier.classify(new Error())).isFalse();
		assertThat(classifier.classify(new Error(new Exception()))).isFalse();
	}

	/* ---------------- Exception classification -------------- */

    /**
     * <code>testFailOnEmptyExceptionClassifierRules</code>
     * <p>The test fail on empty exception classifier rules method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnEmptyExceptionClassifierRules() {
		assertThatIllegalArgumentException().isThrownBy(() -> RetryTemplate.builder().traversingCauses().build());
	}

    /**
     * <code>testFailOnNotationMix</code>
     * <p>The test fail on notation mix method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnNotationMix() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RetryTemplate.builder().retryOn(IOException.class).notRetryOn(OutOfMemoryError.class));
	}

    /**
     * <code>testFailOnNotationsMix</code>
     * <p>The test fail on notations mix method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnNotationsMix() {
		assertThatIllegalArgumentException().isThrownBy(() -> RetryTemplate.builder()
			.retryOn(Collections.<Class<? extends Throwable>>singletonList(IOException.class))
			.notRetryOn(Collections.<Class<? extends Throwable>>singletonList(OutOfMemoryError.class)));
	}

    /**
     * <code>testFailOnPredicateWithOtherMix</code>
     * <p>The test fail on predicate with other mix method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnPredicateWithOtherMix() {
		assertThatIllegalArgumentException().isThrownBy(() -> RetryTemplate.builder()
			.retryOn(Collections.<Class<? extends Throwable>>singletonList(IOException.class))
			.retryOn(classifiable -> true));
	}

    /**
     * <code>testRetryOnPredicate</code>
     * <p>The test retry on predicate method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testRetryOnPredicate() {
		Predicate<Throwable> predicate = classifiable -> classifiable instanceof IllegalAccessError;
		RetryTemplate template = RetryTemplate.builder().maxAttempts(10).retryOn(predicate).build();

		PolicyTuple policyTuple = PolicyTuple.extractWithAsserts(template);
		assertThat(policyTuple.exceptionClassifierRetryPolicy).isInstanceOf(PredicateRetryPolicy.class);
		RetryPolicy retryPolicy = policyTuple.exceptionClassifierRetryPolicy;
		assertThat(retryPolicy).isInstanceOf(PredicateRetryPolicy.class);
		assertThat(policyTuple.baseRetryPolicy).isInstanceOf(MaxAttemptsRetryPolicy.class);
		assertThat(policyTuple.baseRetryPolicy.getMaxAttempts()).isEqualTo(10);
		assertThat(getPropertyValue(template, "listeners", RetryListener[].class)).isEmpty();
		assertThat(getPropertyValue(template, "backOffPolicy")).isInstanceOf(NoBackOffPolicy.class);
	}

	/* ---------------- BackOff -------------- */

    /**
     * <code>testFailOnBackOffPolicyNull</code>
     * <p>The test fail on back off policy null method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnBackOffPolicyNull() {
		assertThatIllegalArgumentException().isThrownBy(() -> RetryTemplate.builder().customBackoff(null).build());
	}

    /**
     * <code>testFailOnBackOffPolicyConflict</code>
     * <p>The test fail on back off policy conflict method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFailOnBackOffPolicyConflict() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RetryTemplate.builder().noBackoff().fixedBackoff(1000).build());
	}

    /**
     * <code>testFixedBackoff</code>
     * <p>The test fixed backoff method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFixedBackoff() {
		RetryTemplate template = RetryTemplate.builder().fixedBackoff(200).build();
		FixedBackOffPolicy policy = getPropertyValue(template, "backOffPolicy", FixedBackOffPolicy.class);

		assertThat(policy.getBackOffPeriod()).isEqualTo(200);
	}

    /**
     * <code>testFixedBackoffDuration</code>
     * <p>The test fixed backoff duration method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFixedBackoffDuration() {
		RetryTemplate template = RetryTemplate.builder().fixedBackoff(Duration.ofSeconds(1)).build();
		FixedBackOffPolicy policy = getPropertyValue(template, "backOffPolicy", FixedBackOffPolicy.class);

		assertThat(policy.getBackOffPeriod()).isEqualTo(1000);
	}

    /**
     * <code>testUniformRandomBackOff</code>
     * <p>The test uniform random back off method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testUniformRandomBackOff() {
		RetryTemplate template = RetryTemplate.builder().uniformRandomBackoff(10, 100).build();
		assertThat(getPropertyValue(template, "backOffPolicy")).isInstanceOf(UniformRandomBackOffPolicy.class);
	}

    /**
     * <code>testUniformRandomBackOffDuration</code>
     * <p>The test uniform random back off duration method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testUniformRandomBackOffDuration() {
		RetryTemplate template = RetryTemplate.builder()
			.uniformRandomBackoff(Duration.ofSeconds(1), Duration.ofSeconds(2))
			.build();

		UniformRandomBackOffPolicy policy = getPropertyValue(template, "backOffPolicy",
				UniformRandomBackOffPolicy.class);

		assertThat(policy.getMinBackOffPeriod()).isEqualTo(1000);
		assertThat(policy.getMaxBackOffPeriod()).isEqualTo(2000);
	}

    /**
     * <code>testNoBackOff</code>
     * <p>The test no back off method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testNoBackOff() {
		RetryTemplate template = RetryTemplate.builder().noBackoff().build();
		assertThat(getPropertyValue(template, "backOffPolicy")).isInstanceOf(NoBackOffPolicy.class);
	}

    /**
     * <code>testExponentialBackoff</code>
     * <p>The test exponential backoff method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testExponentialBackoff() {
		RetryTemplate template = RetryTemplate.builder().exponentialBackoff(10, 2, 500).build();
		ExponentialBackOffPolicy policy = getPropertyValue(template, "backOffPolicy", ExponentialBackOffPolicy.class);

		assertThat(policy.getInitialInterval()).isEqualTo(10);
		assertThat(policy.getMultiplier()).isEqualTo(2);
		assertThat(policy.getMaxInterval()).isEqualTo(500);
	}

    /**
     * <code>testExponentialBackoffDuration</code>
     * <p>The test exponential backoff duration method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testExponentialBackoffDuration() {
		RetryTemplate template = RetryTemplate.builder()
			.exponentialBackoff(Duration.ofSeconds(2), 2, Duration.ofSeconds(3))
			.build();

		ExponentialBackOffPolicy policy = getPropertyValue(template, "backOffPolicy", ExponentialBackOffPolicy.class);

		assertThat(policy.getInitialInterval()).isEqualTo(2000);
		assertThat(policy.getMultiplier()).isEqualTo(2);
		assertThat(policy.getMaxInterval()).isEqualTo(3000);
		assertThat(policy.getMaxInterval()).isEqualTo(3000);
	}

    /**
     * <code>testExpBackOffWithRandom</code>
     * <p>The test exp back off with random method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testExpBackOffWithRandom() {
		RetryTemplate template = RetryTemplate.builder().exponentialBackoff(10, 2, 500, true).build();
		assertThat(getPropertyValue(template, "backOffPolicy")).isInstanceOf(ExponentialRandomBackOffPolicy.class);
	}

    /**
     * <code>testExponentialRandomBackoffDuration</code>
     * <p>The test exponential random backoff duration method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testExponentialRandomBackoffDuration() {
		RetryTemplate template = RetryTemplate.builder()
			.exponentialBackoff(Duration.ofSeconds(2), 2, Duration.ofSeconds(3), true)
			.build();

		ExponentialRandomBackOffPolicy policy = getPropertyValue(template, "backOffPolicy",
				ExponentialRandomBackOffPolicy.class);

		assertThat(policy.getInitialInterval()).isEqualTo(2000);
		assertThat(policy.getMultiplier()).isEqualTo(2);
		assertThat(policy.getMaxInterval()).isEqualTo(3000);
	}

    /**
     * <code>testValidateInitAndMax</code>
     * <p>The test validate init and max method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testValidateInitAndMax() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RetryTemplate.builder().exponentialBackoff(100, 2, 100).build());
	}

    /**
     * <code>testValidateMeaninglessMultiplier</code>
     * <p>The test validate meaningless multiplier method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testValidateMeaninglessMultiplier() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RetryTemplate.builder().exponentialBackoff(100, 1, 200).build());
	}

    /**
     * <code>testValidateZeroInitInterval</code>
     * <p>The test validate zero init interval method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testValidateZeroInitInterval() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> RetryTemplate.builder().exponentialBackoff(0, 2, 200).build());
	}

    /**
     * <code>testBuilderWithLogger</code>
     * <p>The test builder with logger method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testBuilderWithLogger() {
		Log logMock = mock(Log.class);
		RetryTemplate retryTemplate = RetryTemplate.builder().withLogger(logMock).build();
		Log logger = getPropertyValue(retryTemplate, "logger", Log.class);
		assertThat(logger).isEqualTo(logMock);
	}

    /**
     * <code>testBuilderWithDefaultLogger</code>
     * <p>The test builder with default logger method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testBuilderWithDefaultLogger() {
		RetryTemplate retryTemplate = RetryTemplate.builder().build();
		Log logger = getPropertyValue(retryTemplate, "logger", Log.class);
		assertThat(logger).isNotNull();
	}

	/* ---------------- Utils -------------- */

	private static class PolicyTuple {

        /**
         * <code>baseRetryPolicy</code>
         * {@link org.springframework.retry.RetryPolicy} <p>The <code>baseRetryPolicy</code> field.</p>
         * @see  org.springframework.retry.RetryPolicy
         */
        RetryPolicy baseRetryPolicy;

        /**
         * <code>exceptionClassifierRetryPolicy</code>
         * {@link org.springframework.retry.RetryPolicy} <p>The <code>exceptionClassifierRetryPolicy</code> field.</p>
         * @see  org.springframework.retry.RetryPolicy
         */
        RetryPolicy exceptionClassifierRetryPolicy;

        /**
         * <code>extractWithAsserts</code>
         * <p>The extract with asserts method.</p>
         * @param template {@link org.springframework.retry.support.RetryTemplate} <p>The template parameter is <code>RetryTemplate</code> type.</p>
         * @return  {@link org.springframework.retry.support.RetryTemplateBuilderTests.PolicyTuple} <p>The extract with asserts return object is <code>PolicyTuple</code> type.</p>
         */
        static PolicyTuple extractWithAsserts(RetryTemplate template) {
			CompositeRetryPolicy compositeRetryPolicy = getPropertyValue(template, "retryPolicy",
					CompositeRetryPolicy.class);
			PolicyTuple res = new PolicyTuple();

			assertThat(getPropertyValue(compositeRetryPolicy, "optimistic", Boolean.class)).isFalse();

			for (final RetryPolicy policy : getPropertyValue(compositeRetryPolicy, "policies", RetryPolicy[].class)) {
				if (policy instanceof BinaryExceptionClassifierRetryPolicy || policy instanceof PredicateRetryPolicy) {
					res.exceptionClassifierRetryPolicy = policy;
				}
				else {
					res.baseRetryPolicy = policy;
				}
			}
			assertThat(res.exceptionClassifierRetryPolicy).isNotNull();
			assertThat(res.baseRetryPolicy).isNotNull();
			return res;
		}

	}

}
