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

package org.springframework.retry;

/**
 * <code>RetryListener</code>
 * <p>The retry listener interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryListener {

    /**
     * <code>open</code>
     * <p>The open method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.RetryCallback} <p>The callback parameter is <code>RetryCallback</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryCallback
     * @return  boolean <p>The open return object is <code>boolean</code> type.</p>
     */
    default <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		return true;
	}

    /**
     * <code>close</code>
     * <p>The close method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.RetryCallback} <p>The callback parameter is <code>RetryCallback</code> type.</p>
     * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryCallback
     */
    default <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
	}

    /**
     * <code>onSuccess</code>
     * <p>The on success method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.RetryCallback} <p>The callback parameter is <code>RetryCallback</code> type.</p>
     * @param result T <p>The result parameter is <code>T</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryCallback
     */
    default <T, E extends Throwable> void onSuccess(RetryContext context, RetryCallback<T, E> callback, T result) {
	}

    /**
     * <code>onError</code>
     * <p>The on error method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param callback {@link org.springframework.retry.RetryCallback} <p>The callback parameter is <code>RetryCallback</code> type.</p>
     * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryCallback
     */
    default <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
	}

}
