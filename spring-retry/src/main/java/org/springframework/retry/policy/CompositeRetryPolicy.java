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
import java.util.Arrays;
import java.util.List;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * <code>CompositeRetryPolicy</code>
 * <p>The composite retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class CompositeRetryPolicy implements RetryPolicy {

    /**
     * <code>policies</code>
     * {@link org.springframework.retry.RetryPolicy} <p>The <code>policies</code> field.</p>
     * @see  org.springframework.retry.RetryPolicy
     */
    RetryPolicy[] policies = new RetryPolicy[0];

	private boolean optimistic = false;

    /**
     * <code>setOptimistic</code>
     * <p>The set optimistic setter method.</p>
     * @param optimistic boolean <p>The optimistic parameter is <code>boolean</code> type.</p>
     */
    public void setOptimistic(boolean optimistic) {
		this.optimistic = optimistic;
	}

    /**
     * <code>setPolicies</code>
     * <p>The set policies setter method.</p>
     * @param policies {@link org.springframework.retry.RetryPolicy} <p>The policies parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     */
    public void setPolicies(RetryPolicy[] policies) {
		this.policies = Arrays.asList(policies).toArray(new RetryPolicy[policies.length]);
	}

	@Override
	public boolean canRetry(RetryContext context) {
		RetryContext[] contexts = ((CompositeRetryContext) context).contexts;
		RetryPolicy[] policies = ((CompositeRetryContext) context).policies;

		boolean retryable = true;

		if (this.optimistic) {
			retryable = false;
			for (int i = 0; i < contexts.length; i++) {
				if (policies[i].canRetry(contexts[i])) {
					retryable = true;
				}
			}
		}
		else {
			for (int i = 0; i < contexts.length; i++) {
				if (!policies[i].canRetry(contexts[i])) {
					retryable = false;
				}
			}
		}

		return retryable;
	}

	@Override
	public void close(RetryContext context) {
		RetryContext[] contexts = ((CompositeRetryContext) context).contexts;
		RetryPolicy[] policies = ((CompositeRetryContext) context).policies;
		RuntimeException exception = null;
		for (int i = 0; i < contexts.length; i++) {
			try {
				policies[i].close(contexts[i]);
			}
			catch (RuntimeException e) {
				if (exception == null) {
					exception = e;
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	@Override
	public RetryContext open(RetryContext parent) {
		List<RetryContext> list = new ArrayList<>();
		for (RetryPolicy policy : this.policies) {
			list.add(policy.open(parent));
		}
		return new CompositeRetryContext(parent, list, this.policies);
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		RetryContext[] contexts = ((CompositeRetryContext) context).contexts;
		RetryPolicy[] policies = ((CompositeRetryContext) context).policies;
		for (int i = 0; i < contexts.length; i++) {
			policies[i].registerThrowable(contexts[i], throwable);
		}
		((RetryContextSupport) context).registerThrowable(throwable);
	}

	@Override
	public int getMaxAttempts() {
		return Arrays.stream(policies)
			.map(RetryPolicy::getMaxAttempts)
			.filter(maxAttempts -> maxAttempts != NO_MAXIMUM_ATTEMPTS_SET)
			.sorted()
			.findFirst()
			.orElse(NO_MAXIMUM_ATTEMPTS_SET);
	}

	private static class CompositeRetryContext extends RetryContextSupport {

        /**
         * <code>contexts</code>
         * <p>The contexts field.</p>
         * @see  org.springframework.retry.RetryContext
         */
        RetryContext[] contexts;

        /**
         * <code>policies</code>
         * <p>The policies field.</p>
         * @see  org.springframework.retry.RetryPolicy
         */
        RetryPolicy[] policies;

        /**
         * <code>CompositeRetryContext</code>
         * <p>Instantiates a new composite retry context.</p>
         * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
         * @param contexts {@link java.util.List} <p>The contexts parameter is <code>List</code> type.</p>
         * @param policies {@link org.springframework.retry.RetryPolicy} <p>The policies parameter is <code>RetryPolicy</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         * @see  java.util.List
         * @see  org.springframework.retry.RetryPolicy
         */
        public CompositeRetryContext(RetryContext parent, List<RetryContext> contexts, RetryPolicy[] policies) {
			super(parent);
			this.contexts = contexts.toArray(new RetryContext[contexts.size()]);
			this.policies = policies;
		}

	}

}
