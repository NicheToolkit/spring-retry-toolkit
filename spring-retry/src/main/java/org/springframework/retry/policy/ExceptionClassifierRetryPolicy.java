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

import java.util.HashMap;
import java.util.Map;

import org.springframework.classify.Classifier;
import org.springframework.classify.ClassifierSupport;
import org.springframework.classify.SubclassClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.Assert;

/**
 * <code>ExceptionClassifierRetryPolicy</code>
 * <p>The exception classifier retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ExceptionClassifierRetryPolicy implements RetryPolicy {

	private Classifier<Throwable, RetryPolicy> exceptionClassifier = new ClassifierSupport<>(new NeverRetryPolicy());

    /**
     * <code>setPolicyMap</code>
     * <p>The set policy map setter method.</p>
     * @param policyMap {@link java.util.Map} <p>The policy map parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public void setPolicyMap(Map<Class<? extends Throwable>, RetryPolicy> policyMap) {
		this.exceptionClassifier = new SubclassClassifier<>(policyMap, new NeverRetryPolicy());
	}

    /**
     * <code>setExceptionClassifier</code>
     * <p>The set exception classifier setter method.</p>
     * @param exceptionClassifier {@link org.springframework.classify.Classifier} <p>The exception classifier parameter is <code>Classifier</code> type.</p>
     * @see  org.springframework.classify.Classifier
     */
    public void setExceptionClassifier(Classifier<Throwable, RetryPolicy> exceptionClassifier) {
		this.exceptionClassifier = exceptionClassifier;
	}

	public boolean canRetry(RetryContext context) {
		RetryPolicy policy = (RetryPolicy) context;
		return policy.canRetry(context);
	}

	public void close(RetryContext context) {
		RetryPolicy policy = (RetryPolicy) context;
		policy.close(context);
	}

	public RetryContext open(RetryContext parent) {
		return new ExceptionClassifierRetryContext(parent, exceptionClassifier).open(parent);
	}

	public void registerThrowable(RetryContext context, Throwable throwable) {
		RetryPolicy policy = (RetryPolicy) context;
		policy.registerThrowable(context, throwable);
		((RetryContextSupport) context).registerThrowable(throwable);
	}

	private static class ExceptionClassifierRetryContext extends RetryContextSupport implements RetryPolicy {

		final private Classifier<Throwable, RetryPolicy> exceptionClassifier;

		// Dynamic: depends on the latest exception:
		private RetryPolicy policy;

		// Dynamic: depends on the policy:
		private RetryContext context;

		final private Map<RetryPolicy, RetryContext> contexts = new HashMap<>();

        /**
         * <code>ExceptionClassifierRetryContext</code>
         * <p>Instantiates a new exception classifier retry context.</p>
         * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
         * @param exceptionClassifier {@link org.springframework.classify.Classifier} <p>The exception classifier parameter is <code>Classifier</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         * @see  org.springframework.classify.Classifier
         */
        public ExceptionClassifierRetryContext(RetryContext parent,
				Classifier<Throwable, RetryPolicy> exceptionClassifier) {
			super(parent);
			this.exceptionClassifier = exceptionClassifier;
		}

		public boolean canRetry(RetryContext context) {
			return this.context == null || policy.canRetry(this.context);
		}

		public void close(RetryContext context) {
			// Only close those policies that have been used (opened):
			for (RetryPolicy policy : contexts.keySet()) {
				policy.close(getContext(policy, context.getParent()));
			}
		}

		public RetryContext open(RetryContext parent) {
			return this;
		}

		public void registerThrowable(RetryContext context, Throwable throwable) {
			policy = exceptionClassifier.classify(throwable);
			Assert.notNull(policy, "Could not locate policy for exception=[" + throwable + "].");
			this.context = getContext(policy, context.getParent());
			policy.registerThrowable(this.context, throwable);
		}

		private RetryContext getContext(RetryPolicy policy, RetryContext parent) {
			RetryContext context = contexts.get(policy);
			if (context == null) {
				context = policy.open(parent);
				contexts.put(policy, context);
			}
			return context;
		}

	}

}
