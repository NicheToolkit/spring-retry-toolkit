/*
 * Copyright 2006-2007 the original author or authors.
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

import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.RetryState;

/**
 * <code>DefaultRetryState</code>
 * <p>The default retry state class.</p>
 * @see  org.springframework.retry.RetryState
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class DefaultRetryState implements RetryState {

	final private Object key;

	final private boolean forceRefresh;

	final private Classifier<? super Throwable, Boolean> rollbackClassifier;

    /**
     * <code>DefaultRetryState</code>
     * <p>Instantiates a new default retry state.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @param forceRefresh boolean <p>The force refresh parameter is <code>boolean</code> type.</p>
     * @param rollbackClassifier {@link org.springframework.classify.Classifier} <p>The rollback classifier parameter is <code>Classifier</code> type.</p>
     * @see  java.lang.Object
     * @see  org.springframework.classify.Classifier
     */
    public DefaultRetryState(Object key, boolean forceRefresh,
			Classifier<? super Throwable, Boolean> rollbackClassifier) {
		this.key = key;
		this.forceRefresh = forceRefresh;
		this.rollbackClassifier = rollbackClassifier;
	}

    /**
     * <code>DefaultRetryState</code>
     * <p>Instantiates a new default retry state.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @param rollbackClassifier {@link org.springframework.classify.Classifier} <p>The rollback classifier parameter is <code>Classifier</code> type.</p>
     * @see  java.lang.Object
     * @see  org.springframework.classify.Classifier
     */
    public DefaultRetryState(Object key, Classifier<? super Throwable, Boolean> rollbackClassifier) {
		this(key, false, rollbackClassifier);
	}

    /**
     * <code>DefaultRetryState</code>
     * <p>Instantiates a new default retry state.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @param forceRefresh boolean <p>The force refresh parameter is <code>boolean</code> type.</p>
     * @see  java.lang.Object
     */
    public DefaultRetryState(Object key, boolean forceRefresh) {
		this(key, forceRefresh, null);
	}

    /**
     * <code>DefaultRetryState</code>
     * <p>Instantiates a new default retry state.</p>
     * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    public DefaultRetryState(Object key) {
		this(key, false, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.retry.IRetryState#getKey()
	 */
	public Object getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.retry.IRetryState#isForceRefresh()
	 */
	public boolean isForceRefresh() {
		return forceRefresh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.retry.RetryState#rollbackFor(java.lang.Throwable )
	 */
	public boolean rollbackFor(Throwable exception) {
		if (rollbackClassifier == null) {
			return true;
		}
		return rollbackClassifier.classify(exception);
	}

	@Override
	public String toString() {
		return String.format("[%s: key=%s, forceRefresh=%b]", getClass().getSimpleName(), key, forceRefresh);
	}

}
