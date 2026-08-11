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
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * <code>SimpleRetryPolicy</code>
 * <p>The simple retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class SimpleRetryPolicy implements RetryPolicy {

    /**
     * <code>DEFAULT_MAX_ATTEMPTS</code>
     * <p>The constant <code>DEFAULT_MAX_ATTEMPTS</code> field.</p>
     */
    public final static int DEFAULT_MAX_ATTEMPTS = 3;

	private int maxAttempts;

	private Supplier<Integer> maxAttemptsSupplier;

	private BinaryExceptionClassifier retryableClassifier;

	private BinaryExceptionClassifier recoverableClassifier = new BinaryExceptionClassifier(Collections.emptyMap(),
			true, true);

    /**
     * <code>SimpleRetryPolicy</code>
     * <p>Instantiates a new simple retry policy.</p>
     */
    public SimpleRetryPolicy() {
		this(DEFAULT_MAX_ATTEMPTS, BinaryExceptionClassifier.defaultClassifier());
	}

    /**
     * <code>SimpleRetryPolicy</code>
     * <p>Instantiates a new simple retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     */
    public SimpleRetryPolicy(int maxAttempts) {
		this(maxAttempts, BinaryExceptionClassifier.defaultClassifier());
	}

    /**
     * <code>SimpleRetryPolicy</code>
     * <p>Instantiates a new simple retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @param retryableExceptions {@link java.util.Map} <p>The retryable exceptions parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
		this(maxAttempts, retryableExceptions, false);
	}

    /**
     * <code>SimpleRetryPolicy</code>
     * <p>Instantiates a new simple retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @param retryableExceptions {@link java.util.Map} <p>The retryable exceptions parameter is <code>Map</code> type.</p>
     * @param traverseCauses boolean <p>The traverse causes parameter is <code>boolean</code> type.</p>
     * @see  java.util.Map
     */
    public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses) {
		this(maxAttempts, retryableExceptions, traverseCauses, false);
	}

    /**
     * <code>SimpleRetryPolicy</code>
     * <p>Instantiates a new simple retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @param retryableExceptions {@link java.util.Map} <p>The retryable exceptions parameter is <code>Map</code> type.</p>
     * @param traverseCauses boolean <p>The traverse causes parameter is <code>boolean</code> type.</p>
     * @param defaultValue boolean <p>The default value parameter is <code>boolean</code> type.</p>
     * @see  java.util.Map
     */
    public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses, boolean defaultValue) {
		super();
		this.maxAttempts = maxAttempts;
		this.retryableClassifier = new BinaryExceptionClassifier(retryableExceptions, defaultValue);
		this.retryableClassifier.setTraverseCauses(traverseCauses);
	}

    /**
     * <code>SimpleRetryPolicy</code>
     * <p>Instantiates a new simple retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @param classifier {@link org.springframework.classify.BinaryExceptionClassifier} <p>The classifier parameter is <code>BinaryExceptionClassifier</code> type.</p>
     * @see  org.springframework.classify.BinaryExceptionClassifier
     */
    public SimpleRetryPolicy(int maxAttempts, BinaryExceptionClassifier classifier) {
		super();
		this.maxAttempts = maxAttempts;
		this.retryableClassifier = classifier;
	}

    /**
     * <code>setMaxAttempts</code>
     * <p>The set max attempts setter method.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     */
    public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

    /**
     * <code>setNotRecoverable</code>
     * <p>The set not recoverable setter method.</p>
     * @param noRecovery {@link java.lang.Class} <p>The no recovery parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.SuppressWarnings
     */
    @SuppressWarnings("unchecked")
	public void setNotRecoverable(Class<? extends Throwable>... noRecovery) {
		Map<Class<? extends Throwable>, Boolean> map = new HashMap<>();
		for (Class<? extends Throwable> clazz : noRecovery) {
			map.put(clazz, false);
		}
		this.recoverableClassifier = new BinaryExceptionClassifier(map, true, true);
	}

    /**
     * <code>maxAttemptsSupplier</code>
     * <p>The max attempts supplier method.</p>
     * @param maxAttemptsSupplier {@link java.util.function.Supplier} <p>The max attempts supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void maxAttemptsSupplier(Supplier<Integer> maxAttemptsSupplier) {
		Assert.notNull(maxAttemptsSupplier, "'maxAttemptsSupplier' cannot be null");
		this.maxAttemptsSupplier = maxAttemptsSupplier;
	}

	@Override
	public int getMaxAttempts() {
		if (this.maxAttemptsSupplier != null) {
			return this.maxAttemptsSupplier.get();
		}
		return this.maxAttempts;
	}

	@Override
	public boolean canRetry(RetryContext context) {
		Throwable t = context.getLastThrowable();
		boolean can = (t == null || retryForException(t)) && context.getRetryCount() < getMaxAttempts();
		if (!can && t != null && !this.recoverableClassifier.classify(t)) {
			context.setAttribute(RetryContext.NO_RECOVERY, true);
		}
		else {
			context.removeAttribute(RetryContext.NO_RECOVERY);
		}
		return can;
	}

	@Override
	public void close(RetryContext status) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
		simpleContext.registerThrowable(throwable);
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new SimpleRetryContext(parent);
	}

	private static class SimpleRetryContext extends RetryContextSupport {

        /**
         * <code>SimpleRetryContext</code>
         * <p>Instantiates a new simple retry context.</p>
         * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         */
        public SimpleRetryContext(RetryContext parent) {
			super(parent);
		}

	}

	private boolean retryForException(Throwable ex) {
		return this.retryableClassifier.classify(ex);
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[maxAttempts=" + getMaxAttempts() + "]";
	}

}
