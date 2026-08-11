/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.retry.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/**
 * <code>RetryInterceptorBuilder</code>
 * <p>The retry interceptor builder class.</p>
 * @param <T>  {@link org.aopalliance.intercept.MethodInterceptor} <p>The generic parameter is <code>MethodInterceptor</code> type.</p>
 * @see  org.aopalliance.intercept.MethodInterceptor
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public abstract class RetryInterceptorBuilder<T extends MethodInterceptor> {

    /**
     * <code>retryTemplate</code>
     * {@link org.springframework.retry.support.RetryTemplate} <p>The <code>retryTemplate</code> field.</p>
     * @see  org.springframework.retry.support.RetryTemplate
     */
    protected final RetryTemplate retryTemplate = new RetryTemplate();

    /**
     * <code>simpleRetryPolicy</code>
     * {@link org.springframework.retry.policy.SimpleRetryPolicy} <p>The <code>simpleRetryPolicy</code> field.</p>
     * @see  org.springframework.retry.policy.SimpleRetryPolicy
     */
    protected final SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();

    /**
     * <code>retryOperations</code>
     * {@link org.springframework.retry.RetryOperations} <p>The <code>retryOperations</code> field.</p>
     * @see  org.springframework.retry.RetryOperations
     */
    protected RetryOperations retryOperations;

    /**
     * <code>recoverer</code>
     * {@link org.springframework.retry.interceptor.MethodInvocationRecoverer} <p>The <code>recoverer</code> field.</p>
     * @see  org.springframework.retry.interceptor.MethodInvocationRecoverer
     */
    protected MethodInvocationRecoverer<?> recoverer;

	private boolean templateAltered;

	private boolean backOffPolicySet;

	private boolean retryPolicySet;

	private boolean backOffOptionsSet;

    /**
     * <code>label</code>
     * {@link java.lang.String} <p>The <code>label</code> field.</p>
     * @see  java.lang.String
     */
    protected String label;

    /**
     * <code>stateful</code>
     * <p>The stateful method.</p>
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.StatefulRetryInterceptorBuilder} <p>The stateful return object is <code>StatefulRetryInterceptorBuilder</code> type.</p>
     * @see  org.springframework.retry.interceptor.RetryInterceptorBuilder.StatefulRetryInterceptorBuilder
     */
    public static StatefulRetryInterceptorBuilder stateful() {
		return new StatefulRetryInterceptorBuilder();
	}

    /**
     * <code>circuitBreaker</code>
     * <p>The circuit breaker method.</p>
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.CircuitBreakerInterceptorBuilder} <p>The circuit breaker return object is <code>CircuitBreakerInterceptorBuilder</code> type.</p>
     * @see  org.springframework.retry.interceptor.RetryInterceptorBuilder.CircuitBreakerInterceptorBuilder
     */
    public static CircuitBreakerInterceptorBuilder circuitBreaker() {
		return new CircuitBreakerInterceptorBuilder();
	}

    /**
     * <code>stateless</code>
     * <p>The stateless method.</p>
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.StatelessRetryInterceptorBuilder} <p>The stateless return object is <code>StatelessRetryInterceptorBuilder</code> type.</p>
     * @see  org.springframework.retry.interceptor.RetryInterceptorBuilder.StatelessRetryInterceptorBuilder
     */
    public static StatelessRetryInterceptorBuilder stateless() {
		return new StatelessRetryInterceptorBuilder();
	}

    /**
     * <code>retryOperations</code>
     * <p>The retry operations method.</p>
     * @param retryOperations {@link org.springframework.retry.RetryOperations} <p>The retry operations parameter is <code>RetryOperations</code> type.</p>
     * @see  org.springframework.retry.RetryOperations
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The retry operations return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> retryOperations(RetryOperations retryOperations) {
		Assert.isTrue(!this.templateAltered, "Cannot set retryOperations when the default has been modified");
		this.retryOperations = retryOperations;
		return this;
	}

    /**
     * <code>maxAttempts</code>
     * <p>The max attempts method.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The max attempts return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> maxAttempts(int maxAttempts) {
		Assert.isNull(this.retryOperations, "cannot alter the retry policy when a custom retryOperations has been set");
		Assert.isTrue(!this.retryPolicySet, "cannot alter the retry policy when a custom retryPolicy has been set");
		this.simpleRetryPolicy.setMaxAttempts(maxAttempts);
		this.retryTemplate.setRetryPolicy(this.simpleRetryPolicy);
		this.templateAltered = true;
		return this;
	}

    /**
     * <code>backOffOptions</code>
     * <p>The back off options method.</p>
     * @param initialInterval long <p>The initial interval parameter is <code>long</code> type.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The back off options return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> backOffOptions(long initialInterval, double multiplier, long maxInterval) {
		Assert.isNull(this.retryOperations,
				"cannot set the back off policy when a custom retryOperations has been set");
		Assert.isTrue(!this.backOffPolicySet, "cannot set the back off options when a back off policy has been set");
		ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
		policy.setInitialInterval(initialInterval);
		policy.setMultiplier(multiplier);
		policy.setMaxInterval(maxInterval);
		this.retryTemplate.setBackOffPolicy(policy);
		this.backOffOptionsSet = true;
		this.templateAltered = true;
		return this;
	}

    /**
     * <code>retryPolicy</code>
     * <p>The retry policy method.</p>
     * @param policy {@link org.springframework.retry.RetryPolicy} <p>The policy parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The retry policy return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> retryPolicy(RetryPolicy policy) {
		Assert.isNull(this.retryOperations, "cannot set the retry policy when a custom retryOperations has been set");
		Assert.isTrue(!this.templateAltered,
				"cannot set the retry policy if max attempts or back off policy or options changed");
		this.retryTemplate.setRetryPolicy(policy);
		this.retryPolicySet = true;
		this.templateAltered = true;
		return this;
	}

    /**
     * <code>backOffPolicy</code>
     * <p>The back off policy method.</p>
     * @param policy {@link org.springframework.retry.backoff.BackOffPolicy} <p>The policy parameter is <code>BackOffPolicy</code> type.</p>
     * @see  org.springframework.retry.backoff.BackOffPolicy
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The back off policy return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> backOffPolicy(BackOffPolicy policy) {
		Assert.isNull(this.retryOperations,
				"cannot set the back off policy when a custom retryOperations has been set");
		Assert.isTrue(!this.backOffOptionsSet,
				"cannot set the back off policy when the back off policy options have been set");
		this.retryTemplate.setBackOffPolicy(policy);
		this.templateAltered = true;
		this.backOffPolicySet = true;
		return this;
	}

    /**
     * <code>recoverer</code>
     * <p>The recoverer method.</p>
     * @param recoverer {@link org.springframework.retry.interceptor.MethodInvocationRecoverer} <p>The recoverer parameter is <code>MethodInvocationRecoverer</code> type.</p>
     * @see  org.springframework.retry.interceptor.MethodInvocationRecoverer
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The recoverer return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> recoverer(MethodInvocationRecoverer<?> recoverer) {
		this.recoverer = recoverer;
		return this;
	}

    /**
     * <code>label</code>
     * <p>The label method.</p>
     * @param label {@link java.lang.String} <p>The label parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder} <p>The label return object is <code>RetryInterceptorBuilder</code> type.</p>
     */
    public RetryInterceptorBuilder<T> label(String label) {
		this.label = label;
		return this;
	}

    /**
     * <code>build</code>
     * <p>The build method.</p>
     * @return  T <p>The build return object is <code>T</code> type.</p>
     */
    public abstract T build();

	private RetryInterceptorBuilder() {
	}

    /**
     * <code>StatefulRetryInterceptorBuilder</code>
     * <p>The stateful retry interceptor builder class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class StatefulRetryInterceptorBuilder
			extends RetryInterceptorBuilder<StatefulRetryOperationsInterceptor> {

		private final StatefulRetryOperationsInterceptor interceptor = new StatefulRetryOperationsInterceptor();

		private MethodArgumentsKeyGenerator keyGenerator;

		private NewMethodArgumentsIdentifier newMethodArgumentsIdentifier;

		private Classifier<? super Throwable, Boolean> rollbackClassifier;

        /**
         * <code>keyGenerator</code>
         * <p>The key generator method.</p>
         * @param keyGenerator {@link org.springframework.retry.interceptor.MethodArgumentsKeyGenerator} <p>The key generator parameter is <code>MethodArgumentsKeyGenerator</code> type.</p>
         * @see  org.springframework.retry.interceptor.MethodArgumentsKeyGenerator
         * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.StatefulRetryInterceptorBuilder} <p>The key generator return object is <code>StatefulRetryInterceptorBuilder</code> type.</p>
         */
        public StatefulRetryInterceptorBuilder keyGenerator(MethodArgumentsKeyGenerator keyGenerator) {
			this.keyGenerator = keyGenerator;
			return this;
		}

        /**
         * <code>newMethodArgumentsIdentifier</code>
         * <p>The new method arguments identifier method.</p>
         * @param newMethodArgumentsIdentifier {@link org.springframework.retry.interceptor.NewMethodArgumentsIdentifier} <p>The new method arguments identifier parameter is <code>NewMethodArgumentsIdentifier</code> type.</p>
         * @see  org.springframework.retry.interceptor.NewMethodArgumentsIdentifier
         * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.StatefulRetryInterceptorBuilder} <p>The new method arguments identifier return object is <code>StatefulRetryInterceptorBuilder</code> type.</p>
         */
        public StatefulRetryInterceptorBuilder newMethodArgumentsIdentifier(
				NewMethodArgumentsIdentifier newMethodArgumentsIdentifier) {
			this.newMethodArgumentsIdentifier = newMethodArgumentsIdentifier;
			return this;
		}

        /**
         * <code>rollbackFor</code>
         * <p>The rollback for method.</p>
         * @param rollbackClassifier {@link org.springframework.classify.Classifier} <p>The rollback classifier parameter is <code>Classifier</code> type.</p>
         * @see  org.springframework.classify.Classifier
         * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.StatefulRetryInterceptorBuilder} <p>The rollback for return object is <code>StatefulRetryInterceptorBuilder</code> type.</p>
         */
        public StatefulRetryInterceptorBuilder rollbackFor(Classifier<? super Throwable, Boolean> rollbackClassifier) {
			this.rollbackClassifier = rollbackClassifier;
			return this;
		}

		@Override
		public StatefulRetryInterceptorBuilder retryOperations(RetryOperations retryOperations) {
			super.retryOperations(retryOperations);
			return this;
		}

		@Override
		public StatefulRetryInterceptorBuilder maxAttempts(int maxAttempts) {
			super.maxAttempts(maxAttempts);
			return this;
		}

		@Override
		public StatefulRetryInterceptorBuilder backOffOptions(long initialInterval, double multiplier,
				long maxInterval) {
			super.backOffOptions(initialInterval, multiplier, maxInterval);
			return this;
		}

		@Override
		public StatefulRetryInterceptorBuilder retryPolicy(RetryPolicy policy) {
			super.retryPolicy(policy);
			return this;
		}

		@Override
		public StatefulRetryInterceptorBuilder backOffPolicy(BackOffPolicy policy) {
			super.backOffPolicy(policy);
			return this;
		}

		@Override
		public StatefulRetryInterceptorBuilder recoverer(MethodInvocationRecoverer<?> recoverer) {
			super.recoverer(recoverer);
			return this;
		}

		@Override
		public StatefulRetryOperationsInterceptor build() {
			if (this.recoverer != null) {
				this.interceptor.setRecoverer(this.recoverer);
			}
			if (this.retryOperations != null) {
				this.interceptor.setRetryOperations(this.retryOperations);
			}
			else {
				this.interceptor.setRetryOperations(this.retryTemplate);
			}
			if (this.keyGenerator != null) {
				this.interceptor.setKeyGenerator(this.keyGenerator);
			}
			if (this.rollbackClassifier != null) {
				this.interceptor.setRollbackClassifier(this.rollbackClassifier);
			}
			if (this.newMethodArgumentsIdentifier != null) {
				this.interceptor.setNewItemIdentifier(this.newMethodArgumentsIdentifier);
			}
			if (this.label != null) {
				this.interceptor.setLabel(this.label);
			}
			return this.interceptor;
		}

		private StatefulRetryInterceptorBuilder() {
		}

	}

    /**
     * <code>CircuitBreakerInterceptorBuilder</code>
     * <p>The circuit breaker interceptor builder class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class CircuitBreakerInterceptorBuilder
			extends RetryInterceptorBuilder<StatefulRetryOperationsInterceptor> {

		private final StatefulRetryOperationsInterceptor interceptor = new StatefulRetryOperationsInterceptor();

		private MethodArgumentsKeyGenerator keyGenerator;

		@Override
		public CircuitBreakerInterceptorBuilder retryOperations(RetryOperations retryOperations) {
			super.retryOperations(retryOperations);
			return this;
		}

		@Override
		public CircuitBreakerInterceptorBuilder maxAttempts(int maxAttempts) {
			super.maxAttempts(maxAttempts);
			return this;
		}

		@Override
		public CircuitBreakerInterceptorBuilder retryPolicy(RetryPolicy policy) {
			super.retryPolicy(policy);
			return this;
		}

        /**
         * <code>keyGenerator</code>
         * <p>The key generator method.</p>
         * @param keyGenerator {@link org.springframework.retry.interceptor.MethodArgumentsKeyGenerator} <p>The key generator parameter is <code>MethodArgumentsKeyGenerator</code> type.</p>
         * @see  org.springframework.retry.interceptor.MethodArgumentsKeyGenerator
         * @return  {@link org.springframework.retry.interceptor.RetryInterceptorBuilder.CircuitBreakerInterceptorBuilder} <p>The key generator return object is <code>CircuitBreakerInterceptorBuilder</code> type.</p>
         */
        public CircuitBreakerInterceptorBuilder keyGenerator(MethodArgumentsKeyGenerator keyGenerator) {
			this.keyGenerator = keyGenerator;
			return this;
		}

		@Override
		public CircuitBreakerInterceptorBuilder recoverer(MethodInvocationRecoverer<?> recoverer) {
			super.recoverer(recoverer);
			return this;
		}

		@Override
		public StatefulRetryOperationsInterceptor build() {
			if (this.recoverer != null) {
				this.interceptor.setRecoverer(this.recoverer);
			}
			if (this.retryOperations != null) {
				this.interceptor.setRetryOperations(this.retryOperations);
			}
			else {
				this.interceptor.setRetryOperations(this.retryTemplate);
			}
			if (this.keyGenerator != null) {
				this.interceptor.setKeyGenerator(this.keyGenerator);
			}
			if (this.label != null) {
				this.interceptor.setLabel(this.label);
			}
			this.interceptor.setRollbackClassifier(new BinaryExceptionClassifier(false));
			return this.interceptor;
		}

		private CircuitBreakerInterceptorBuilder() {
		}

	}

    /**
     * <code>StatelessRetryInterceptorBuilder</code>
     * <p>The stateless retry interceptor builder class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class StatelessRetryInterceptorBuilder extends RetryInterceptorBuilder<RetryOperationsInterceptor> {

		private final RetryOperationsInterceptor interceptor = new RetryOperationsInterceptor();

		@Override
		public RetryOperationsInterceptor build() {
			if (this.recoverer != null) {
				this.interceptor.setRecoverer(this.recoverer);
			}
			if (this.retryOperations != null) {
				this.interceptor.setRetryOperations(this.retryOperations);
			}
			else {
				this.interceptor.setRetryOperations(this.retryTemplate);
			}
			if (this.label != null) {
				this.interceptor.setLabel(this.label);
			}
			return this.interceptor;
		}

		private StatelessRetryInterceptorBuilder() {
		}

	}

}
