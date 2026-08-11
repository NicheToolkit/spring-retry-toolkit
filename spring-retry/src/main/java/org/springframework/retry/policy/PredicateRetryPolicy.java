/*
 * Copyright 2024-2024 the original author or authors.
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

import java.util.function.Predicate;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.Assert;

/**
 * <code>PredicateRetryPolicy</code>
 * <p>The predicate retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class PredicateRetryPolicy implements RetryPolicy {

	private final Predicate<Throwable> predicate;

    /**
     * <code>PredicateRetryPolicy</code>
     * <p>Instantiates a new predicate retry policy.</p>
     * @param predicate {@link java.util.function.Predicate} <p>The predicate parameter is <code>Predicate</code> type.</p>
     * @see  java.util.function.Predicate
     */
    public PredicateRetryPolicy(Predicate<Throwable> predicate) {
		Assert.notNull(predicate, "'predicate' must not be null");
		this.predicate = predicate;
	}

	@Override
	public boolean canRetry(RetryContext context) {
		Throwable t = context.getLastThrowable();
		return t == null || predicate.test(t);
	}

	@Override
	public void close(RetryContext status) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		((RetryContextSupport) context).registerThrowable(throwable);
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new RetryContextSupport(parent);
	}

}
