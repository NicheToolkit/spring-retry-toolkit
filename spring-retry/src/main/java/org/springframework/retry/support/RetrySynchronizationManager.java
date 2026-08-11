/*
 * Copyright 2006-2023 the original author or authors.
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.Nullable;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryOperations;

/**
 * <code>RetrySynchronizationManager</code>
 * <p>The retry synchronization manager class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public final class RetrySynchronizationManager {

	private RetrySynchronizationManager() {
	}

	private static final ThreadLocal<RetryContext> context = new ThreadLocal<>();

	private static final Map<Thread, RetryContext> contexts = new ConcurrentHashMap<>();

	private static boolean useThreadLocal = true;

    /**
     * <code>setUseThreadLocal</code>
     * <p>The set use thread local setter method.</p>
     * @param use boolean <p>The use parameter is <code>boolean</code> type.</p>
     */
    public static void setUseThreadLocal(boolean use) {
		useThreadLocal = use;
	}

    /**
     * <code>isUseThreadLocal</code>
     * <p>The is use thread local method.</p>
     * @return  boolean <p>The is use thread local return object is <code>boolean</code> type.</p>
     */
    public static boolean isUseThreadLocal() {
		return useThreadLocal;
	}

    /**
     * <code>getContext</code>
     * <p>The get context getter method.</p>
     * @return  {@link org.springframework.retry.RetryContext} <p>The get context return object is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.lang.Nullable
     */
    @Nullable public static RetryContext getContext() {
		if (useThreadLocal) {
			return context.get();
		}
		else {
			return contexts.get(Thread.currentThread());
		}
	}

    /**
     * <code>register</code>
     * <p>The register method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.lang.Nullable
     * @return  {@link org.springframework.retry.RetryContext} <p>The register return object is <code>RetryContext</code> type.</p>
     */
    @Nullable public static RetryContext register(RetryContext context) {
		if (useThreadLocal) {
			RetryContext oldContext = getContext();
			RetrySynchronizationManager.context.set(context);
			return oldContext;
		}
		else {
			RetryContext oldContext = contexts.get(Thread.currentThread());
			contexts.put(Thread.currentThread(), context);
			return oldContext;
		}
	}

    /**
     * <code>clear</code>
     * <p>The clear method.</p>
     * @return  {@link org.springframework.retry.RetryContext} <p>The clear return object is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.lang.Nullable
     */
    @Nullable public static RetryContext clear() {
		RetryContext value = getContext();
		RetryContext parent = value == null ? null : value.getParent();
		if (useThreadLocal) {
			RetrySynchronizationManager.context.set(parent);
		}
		else {
			if (parent != null) {
				contexts.put(Thread.currentThread(), parent);
			}
			else {
				contexts.remove(Thread.currentThread());
			}
		}
		return value;
	}

}
