/*
 * Copyright 2006-2025 the original author or authors.
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

import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.RetryState;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * <code>StatefulRetryOperationsInterceptor</code>
 * <p>The stateful retry operations interceptor class.</p>
 * @see  org.aopalliance.intercept.MethodInterceptor
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class StatefulRetryOperationsInterceptor implements MethodInterceptor {

	private transient final Log logger = LogFactory.getLog(getClass());

	private MethodArgumentsKeyGenerator keyGenerator;

	private MethodInvocationRecoverer<?> recoverer;

	private NewMethodArgumentsIdentifier newMethodArgumentsIdentifier;

	private RetryOperations retryOperations;

	private String label;

	private Classifier<? super Throwable, Boolean> rollbackClassifier;

	private boolean useRawKey;

    /**
     * <code>StatefulRetryOperationsInterceptor</code>
     * <p>Instantiates a new stateful retry operations interceptor.</p>
     */
    public StatefulRetryOperationsInterceptor() {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(new NeverRetryPolicy());
		this.retryOperations = retryTemplate;
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

    /**
     * <code>setRollbackClassifier</code>
     * <p>The set rollback classifier setter method.</p>
     * @param rollbackClassifier {@link org.springframework.classify.Classifier} <p>The rollback classifier parameter is <code>Classifier</code> type.</p>
     * @see  org.springframework.classify.Classifier
     */
    public void setRollbackClassifier(Classifier<? super Throwable, Boolean> rollbackClassifier) {
		this.rollbackClassifier = rollbackClassifier;
	}

    /**
     * <code>setKeyGenerator</code>
     * <p>The set key generator setter method.</p>
     * @param keyGenerator {@link org.springframework.retry.interceptor.MethodArgumentsKeyGenerator} <p>The key generator parameter is <code>MethodArgumentsKeyGenerator</code> type.</p>
     * @see  org.springframework.retry.interceptor.MethodArgumentsKeyGenerator
     */
    public void setKeyGenerator(MethodArgumentsKeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

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
     * <code>setNewItemIdentifier</code>
     * <p>The set new item identifier setter method.</p>
     * @param newMethodArgumentsIdentifier {@link org.springframework.retry.interceptor.NewMethodArgumentsIdentifier} <p>The new method arguments identifier parameter is <code>NewMethodArgumentsIdentifier</code> type.</p>
     * @see  org.springframework.retry.interceptor.NewMethodArgumentsIdentifier
     */
    public void setNewItemIdentifier(NewMethodArgumentsIdentifier newMethodArgumentsIdentifier) {
		this.newMethodArgumentsIdentifier = newMethodArgumentsIdentifier;
	}

    /**
     * <code>setUseRawKey</code>
     * <p>The set use raw key setter method.</p>
     * @param useRawKey boolean <p>The use raw key parameter is <code>boolean</code> type.</p>
     */
    public void setUseRawKey(boolean useRawKey) {
		this.useRawKey = useRawKey;
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Executing proxied method in stateful retry: " + invocation.getStaticPart() + "("
					+ ObjectUtils.getIdentityHexString(invocation) + ")");
		}

		Object[] args = invocation.getArguments();
		Object defaultKey = Arrays.asList(args);
		if (args.length == 1) {
			defaultKey = args[0];
		}

		Object key = createKey(invocation, defaultKey);
		RetryState retryState = new DefaultRetryState(key,
				this.newMethodArgumentsIdentifier != null && this.newMethodArgumentsIdentifier.isNew(args),
				this.rollbackClassifier);

		Object result = this.retryOperations.execute(new StatefulMethodInvocationRetryCallback(invocation, label),
				this.recoverer != null ? new ItemRecovererCallback(args, this.recoverer) : null, retryState);

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Exiting proxied method in stateful retry with result: (" + result + ")");
		}

		return result;

	}

	private Object createKey(final MethodInvocation invocation, Object defaultKey) {
		Object generatedKey = defaultKey;
		if (this.keyGenerator != null) {
			generatedKey = this.keyGenerator.getKey(invocation.getArguments());
		}
		if (generatedKey == null) {
			// If there's a generator and he still says the key is null, that means he
			// really doesn't want to retry.
			return null;
		}
		if (this.useRawKey) {
			return generatedKey;
		}
		String name = StringUtils.hasText(label) ? label : invocation.getMethod().toGenericString();
		return Arrays.asList(name, generatedKey);
	}

	private static final class StatefulMethodInvocationRetryCallback
			extends MethodInvocationRetryCallback<Object, Throwable> {

		private StatefulMethodInvocationRetryCallback(MethodInvocation invocation, String label) {
			super(invocation, label);
		}

		@Override
		public Object doWithRetry(RetryContext context) throws Exception {
			context.setAttribute(RetryContext.NAME, label);
			try {
				return this.invocation.proceed();
			}
			catch (Exception | Error e) {
				throw e;
			}
			catch (Throwable e) {
				throw new IllegalStateException(e);
			}
		}

	}

	private static final class ItemRecovererCallback implements RecoveryCallback<Object> {

		private final Object[] args;

		private final MethodInvocationRecoverer<?> recoverer;

		private ItemRecovererCallback(Object[] args, MethodInvocationRecoverer<?> recoverer) {
			this.args = Arrays.asList(args).toArray();
			this.recoverer = recoverer;
		}

		@Override
		public Object recover(RetryContext context) {
			return this.recoverer.recover(this.args, context.getLastThrowable());
		}

	}

}
