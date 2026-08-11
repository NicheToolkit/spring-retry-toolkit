/*
 * Copyright 2006-2019 the original author or authors.
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

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * <code>BinaryExceptionClassifierRetryPolicy</code>
 * <p>The binary exception classifier retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class BinaryExceptionClassifierRetryPolicy implements RetryPolicy {

	private final BinaryExceptionClassifier exceptionClassifier;

    /**
     * <code>BinaryExceptionClassifierRetryPolicy</code>
     * <p>Instantiates a new binary exception classifier retry policy.</p>
     * @param exceptionClassifier {@link org.springframework.classify.BinaryExceptionClassifier} <p>The exception classifier parameter is <code>BinaryExceptionClassifier</code> type.</p>
     * @see  org.springframework.classify.BinaryExceptionClassifier
     */
    public BinaryExceptionClassifierRetryPolicy(BinaryExceptionClassifier exceptionClassifier) {
		this.exceptionClassifier = exceptionClassifier;
	}

    /**
     * <code>getExceptionClassifier</code>
     * <p>The get exception classifier getter method.</p>
     * @return  {@link org.springframework.classify.BinaryExceptionClassifier} <p>The get exception classifier return object is <code>BinaryExceptionClassifier</code> type.</p>
     * @see  org.springframework.classify.BinaryExceptionClassifier
     */
    public BinaryExceptionClassifier getExceptionClassifier() {
		return exceptionClassifier;
	}

	@Override
	public boolean canRetry(RetryContext context) {
		Throwable t = context.getLastThrowable();
		return t == null || exceptionClassifier.classify(t);
	}

	@Override
	public void close(RetryContext status) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		RetryContextSupport simpleContext = ((RetryContextSupport) context);
		simpleContext.registerThrowable(throwable);
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new RetryContextSupport(parent);
	}

}
