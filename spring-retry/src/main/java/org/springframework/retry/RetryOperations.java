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

package org.springframework.retry;

import org.springframework.retry.support.DefaultRetryState;

/**
 * <code>RetryOperations</code>
 * <p>The retry operations interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryOperations {

	/**
	 * <code>execute</code>
	 * <p>The execute method.</p>
	 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
	 * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
	 * @param retryCallback {@link org.springframework.retry.RetryCallback} <p>The retry callback parameter is <code>RetryCallback</code> type.</p>
	 * @see  java.lang.Throwable
	 * @see  org.springframework.retry.RetryCallback
	 * @see  E
	 * @return  T <p>The execute return object is <code>T</code> type.</p>
	 * @throws E E <p>The e is <code>E</code> type.</p>
	 */
	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback) throws E;

	/**
	 * <code>execute</code>
	 * <p>The execute method.</p>
	 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
	 * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
	 * @param retryCallback {@link org.springframework.retry.RetryCallback} <p>The retry callback parameter is <code>RetryCallback</code> type.</p>
	 * @param recoveryCallback {@link org.springframework.retry.RecoveryCallback} <p>The recovery callback parameter is <code>RecoveryCallback</code> type.</p>
	 * @see  java.lang.Throwable
	 * @see  org.springframework.retry.RetryCallback
	 * @see  org.springframework.retry.RecoveryCallback
	 * @see  E
	 * @return  T <p>The execute return object is <code>T</code> type.</p>
	 * @throws E E <p>The e is <code>E</code> type.</p>
	 */
	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback, RecoveryCallback<T> recoveryCallback)
			throws E;

	/**
	 * <code>execute</code>
	 * <p>The execute method.</p>
	 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
	 * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
	 * @param retryCallback {@link org.springframework.retry.RetryCallback} <p>The retry callback parameter is <code>RetryCallback</code> type.</p>
	 * @param retryState {@link org.springframework.retry.RetryState} <p>The retry state parameter is <code>RetryState</code> type.</p>
	 * @see  java.lang.Throwable
	 * @see  org.springframework.retry.RetryCallback
	 * @see  org.springframework.retry.RetryState
	 * @see  E
	 * @see  org.springframework.retry.ExhaustedRetryException
	 * @return  T <p>The execute return object is <code>T</code> type.</p>
	 * @throws E E <p>The e is <code>E</code> type.</p>
	 * @throws ExhaustedRetryException {@link org.springframework.retry.ExhaustedRetryException} <p>The exhausted retry exception is <code>ExhaustedRetryException</code> type.</p>
	 */
	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback, RetryState retryState)
			throws E, ExhaustedRetryException;

	/**
	 * <code>execute</code>
	 * <p>The execute method.</p>
	 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
	 * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
	 * @param retryCallback {@link org.springframework.retry.RetryCallback} <p>The retry callback parameter is <code>RetryCallback</code> type.</p>
	 * @param recoveryCallback {@link org.springframework.retry.RecoveryCallback} <p>The recovery callback parameter is <code>RecoveryCallback</code> type.</p>
	 * @param retryState {@link org.springframework.retry.RetryState} <p>The retry state parameter is <code>RetryState</code> type.</p>
	 * @see  java.lang.Throwable
	 * @see  org.springframework.retry.RetryCallback
	 * @see  org.springframework.retry.RecoveryCallback
	 * @see  org.springframework.retry.RetryState
	 * @see  E
	 * @return  T <p>The execute return object is <code>T</code> type.</p>
	 * @throws E E <p>The e is <code>E</code> type.</p>
	 */
	<T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback, RecoveryCallback<T> recoveryCallback,
			RetryState retryState) throws E;

}
