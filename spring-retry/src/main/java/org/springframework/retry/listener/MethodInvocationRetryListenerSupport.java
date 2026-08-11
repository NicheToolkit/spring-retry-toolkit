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

package org.springframework.retry.listener;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.interceptor.MethodInvocationRetryCallback;

/**
 * <code>MethodInvocationRetryListenerSupport</code>
 * <p>The method invocation retry listener support class.</p>
 * @see  org.springframework.retry.RetryListener
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class MethodInvocationRetryListenerSupport implements RetryListener {

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		if (callback instanceof MethodInvocationRetryCallback) {
			MethodInvocationRetryCallback<T, E> methodInvocationRetryCallback = (MethodInvocationRetryCallback<T, E>) callback;
			doClose(context, methodInvocationRetryCallback, throwable);
		}
	}

	@Override
	public <T, E extends Throwable> void onSuccess(RetryContext context, RetryCallback<T, E> callback, T result) {
		if (callback instanceof MethodInvocationRetryCallback) {
			MethodInvocationRetryCallback<T, E> methodInvocationRetryCallback = (MethodInvocationRetryCallback<T, E>) callback;
			doOnSuccess(context, methodInvocationRetryCallback, result);
		}
	}

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		if (callback instanceof MethodInvocationRetryCallback) {
			MethodInvocationRetryCallback<T, E> methodInvocationRetryCallback = (MethodInvocationRetryCallback<T, E>) callback;
			doOnError(context, methodInvocationRetryCallback, throwable);
		}
	}

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		if (callback instanceof MethodInvocationRetryCallback) {
			MethodInvocationRetryCallback<T, E> methodInvocationRetryCallback = (MethodInvocationRetryCallback<T, E>) callback;
			return doOpen(context, methodInvocationRetryCallback);
		}
		// in case that the callback is not for a reflective method invocation
		// just go forward with the execution
		return true;
	}

    /**
     * <code>doClose</code>
     * <p>The do close method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.interceptor.MethodInvocationRetryCallback} <p>The callback parameter is <code>MethodInvocationRetryCallback</code> type.</p>
     * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.interceptor.MethodInvocationRetryCallback
     */
    protected <T, E extends Throwable> void doClose(RetryContext context, MethodInvocationRetryCallback<T, E> callback,
			Throwable throwable) {
	}

    /**
     * <code>doOnSuccess</code>
     * <p>The do on success method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.interceptor.MethodInvocationRetryCallback} <p>The callback parameter is <code>MethodInvocationRetryCallback</code> type.</p>
     * @param result T <p>The result parameter is <code>T</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.interceptor.MethodInvocationRetryCallback
     */
    protected <T, E extends Throwable> void doOnSuccess(RetryContext context,
			MethodInvocationRetryCallback<T, E> callback, T result) {
	}

    /**
     * <code>doOnError</code>
     * <p>The do on error method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.interceptor.MethodInvocationRetryCallback} <p>The callback parameter is <code>MethodInvocationRetryCallback</code> type.</p>
     * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.interceptor.MethodInvocationRetryCallback
     */
    protected <T, E extends Throwable> void doOnError(RetryContext context,
			MethodInvocationRetryCallback<T, E> callback, Throwable throwable) {
	}

    /**
     * <code>doOpen</code>
     * <p>The do open method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.interceptor.MethodInvocationRetryCallback} <p>The callback parameter is <code>MethodInvocationRetryCallback</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.interceptor.MethodInvocationRetryCallback
     * @return  boolean <p>The do open return object is <code>boolean</code> type.</p>
     */
    protected <T, E extends Throwable> boolean doOpen(RetryContext context,
			MethodInvocationRetryCallback<T, E> callback) {
		return true;
	}

}
