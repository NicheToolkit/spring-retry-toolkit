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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.support.Args;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/**
 * <code>RetryOperationsInterceptor</code>
 * <p>The retry operations interceptor class.</p>
 * @see  org.aopalliance.intercept.MethodInterceptor
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetryOperationsInterceptor implements MethodInterceptor {

    /**
     * <code>METHOD</code>
     * {@link java.lang.String} <p>The constant <code>METHOD</code> field.</p>
     * @see  java.lang.String
     */
    public static final String METHOD = "method";

    /**
     * <code>METHOD_ARGS</code>
     * {@link java.lang.String} <p>The constant <code>METHOD_ARGS</code> field.</p>
     * @see  java.lang.String
     */
    public static final String METHOD_ARGS = "methodArgs";

	private RetryOperations retryOperations = new RetryTemplate();

	@Nullable
	private MethodInvocationRecoverer<?> recoverer;

	private String label;

    /**
     * <code>setLabel</code>
     * <p>The set label setter method.</p>
     * @param label {@link java.lang.String} <p>The label parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public void setLabel(String label) {
		this.label = label;
	}

    /**
     * <code>setRetryOperations</code>
     * <p>The set retry operations setter method.</p>
     * @param retryTemplate {@link org.springframework.retry.RetryOperations} <p>The retry template parameter is <code>RetryOperations</code> type.</p>
     * @see  org.springframework.retry.RetryOperations
     */
    public void setRetryOperations(RetryOperations retryTemplate) {
		Assert.notNull(retryTemplate, "'retryOperations' cannot be null.");
		this.retryOperations = retryTemplate;
	}

    /**
     * <code>setRecoverer</code>
     * <p>The set recoverer setter method.</p>
     * @param recoverer {@link org.springframework.retry.interceptor.MethodInvocationRecoverer} <p>The recoverer parameter is <code>MethodInvocationRecoverer</code> type.</p>
     * @see  org.springframework.retry.interceptor.MethodInvocationRecoverer
     */
    public void setRecoverer(MethodInvocationRecoverer<?> recoverer) {
		this.recoverer = recoverer;
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		RetryCallback<Object, Throwable> retryCallback = new MethodInvocationRetryCallback<Object, Throwable>(invocation, this.label) {

			@Override
			public Object doWithRetry(RetryContext context) throws Exception {

				context.setAttribute(RetryContext.NAME, this.label);
				Args args = new Args(invocation.getArguments());
				context.setAttribute(METHOD, invocation.getMethod());
				context.setAttribute(METHOD_ARGS, args);
				// TODO remove this attribute in the next major/minor version
				context.setAttribute("ARGS", args);

				/*
				 * If we don't copy the invocation carefully it won't keep a reference to
				 * the other interceptors in the chain. We don't have a choice here but to
				 * specialise to ReflectiveMethodInvocation (but how often would another
				 * implementation come along?).
				 */
				if (this.invocation instanceof ProxyMethodInvocation) {
					context.setAttribute("___proxy___", ((ProxyMethodInvocation) this.invocation).getProxy());
					try {
						return ((ProxyMethodInvocation) this.invocation).invocableClone().proceed();
					}
					catch (Exception | Error e) {
						throw e;
					}
					catch (Throwable e) {
						throw new IllegalStateException(e);
					}
				}
				else {
					throw new IllegalStateException(
							"MethodInvocation of the wrong type detected - this should not happen with Spring AOP, "
									+ "so please raise an issue if you see this exception");
				}
			}

		};

		RecoveryCallback<Object> recoveryCallback = (this.recoverer != null)
				? new ItemRecovererCallback(invocation.getArguments(), this.recoverer) : null;
		try {
			return this.retryOperations.execute(retryCallback, recoveryCallback);
		}
		finally {
			RetryContext context = RetrySynchronizationManager.getContext();
			if (context != null) {
				context.removeAttribute("___proxy___");
			}
		}
	}

	private static class ItemRecovererCallback implements RecoveryCallback<Object> {
		private final Object[] args;
		private final MethodInvocationRecoverer<?> recoverer;

        /**
         * <code>ItemRecovererCallback</code>
         * <p>Instantiates a new item recoverer callback.</p>
         * @param args {@link java.lang.Object} <p>The args parameter is <code>Object</code> type.</p>
         * @param recoverer {@link org.springframework.retry.interceptor.MethodInvocationRecoverer} <p>The recoverer parameter is <code>MethodInvocationRecoverer</code> type.</p>
         * @see  java.lang.Object
         * @see  org.springframework.retry.interceptor.MethodInvocationRecoverer
         */
        public ItemRecovererCallback(Object[] args, MethodInvocationRecoverer<?> recoverer) {
			this.args = args;
			this.recoverer = recoverer;
		}

		@Override
		public Object recover(RetryContext context) {
			return this.recoverer.recover(this.args, context.getLastThrowable());
		}
	}

}
