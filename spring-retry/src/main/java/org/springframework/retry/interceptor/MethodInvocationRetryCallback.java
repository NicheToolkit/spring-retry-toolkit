/*
 * Copyright 2006-2024 the original author or authors.
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

package org.springframework.retry.interceptor;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.lang.Nullable;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryOperations;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * <code>MethodInvocationRetryCallback</code>
 * <p>The method invocation retry callback class.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
 * @see  java.lang.Throwable
 * @see  org.springframework.retry.RetryCallback
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public abstract class MethodInvocationRetryCallback<T, E extends Throwable> implements RetryCallback<T, E> {

    /**
     * <code>invocation</code>
     * {@link org.aopalliance.intercept.MethodInvocation} <p>The <code>invocation</code> field.</p>
     * @see  org.aopalliance.intercept.MethodInvocation
     */
    protected final MethodInvocation invocation;

    /**
     * <code>label</code>
     * {@link java.lang.String} <p>The <code>label</code> field.</p>
     * @see  java.lang.String
     */
    protected final String label;

    /**
     * <code>MethodInvocationRetryCallback</code>
     * <p>Instantiates a new method invocation retry callback.</p>
     * @param invocation {@link org.aopalliance.intercept.MethodInvocation} <p>The invocation parameter is <code>MethodInvocation</code> type.</p>
     * @param label {@link java.lang.String} <p>The label parameter is <code>String</code> type.</p>
     * @see  org.aopalliance.intercept.MethodInvocation
     * @see  java.lang.String
     * @see  org.springframework.lang.Nullable
     */
    public MethodInvocationRetryCallback(MethodInvocation invocation, @Nullable String label) {
		this.invocation = invocation;
		if (StringUtils.hasText(label)) {
			this.label = label;
		}
		else {
			this.label = ClassUtils.getQualifiedMethodName(invocation.getMethod());
		}
	}

    /**
     * <code>getInvocation</code>
     * <p>The get invocation getter method.</p>
     * @return  {@link org.aopalliance.intercept.MethodInvocation} <p>The get invocation return object is <code>MethodInvocation</code> type.</p>
     * @see  org.aopalliance.intercept.MethodInvocation
     */
    public MethodInvocation getInvocation() {
		return this.invocation;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

}
