/*
 * Copyright 2006-2026 the original author or authors.
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

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryException;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryOperations;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.RetryState;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.MapRetryContextCache;
import org.springframework.retry.policy.RetryContextCache;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.util.Assert;

/**
 * <code>RetryTemplate</code>
 * <p>The retry template class.</p>
 * @see  org.springframework.retry.RetryOperations
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetryTemplate implements RetryOperations {

	private static final String GLOBAL_STATE = "state.global";

    /**
     * <code>logger</code>
     * {@link org.apache.commons.logging.Log} <p>The <code>logger</code> field.</p>
     * @see  org.apache.commons.logging.Log
     */
    protected Log logger = LogFactory.getLog(getClass());

	private volatile BackOffPolicy backOffPolicy = new NoBackOffPolicy();

	private volatile RetryPolicy retryPolicy = new SimpleRetryPolicy(3);

	private volatile RetryListener[] listeners = new RetryListener[0];

	private CompositeRetryContextCache retryContextCache = new CompositeRetryContextCache();

	private boolean throwLastExceptionOnExhausted;

    /**
     * <code>builder</code>
     * <p>The builder method.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The builder return object is <code>RetryTemplateBuilder</code> type.</p>
     * @see  org.springframework.retry.support.RetryTemplateBuilder
     */
    public static RetryTemplateBuilder builder() {
		return new RetryTemplateBuilder();
	}

    /**
     * <code>defaultInstance</code>
     * <p>The default instance method.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplate} <p>The default instance return object is <code>RetryTemplate</code> type.</p>
     */
    public static RetryTemplate defaultInstance() {
		return new RetryTemplateBuilder().build();
	}

    /**
     * <code>setThrowLastExceptionOnExhausted</code>
     * <p>The set throw last exception on exhausted setter method.</p>
     * @param throwLastExceptionOnExhausted boolean <p>The throw last exception on exhausted parameter is <code>boolean</code> type.</p>
     */
    public void setThrowLastExceptionOnExhausted(boolean throwLastExceptionOnExhausted) {
		this.throwLastExceptionOnExhausted = throwLastExceptionOnExhausted;
	}

    /**
     * <code>setRetryContextCache</code>
     * <p>The set retry context cache setter method.</p>
     * @param retryContextCache {@link org.springframework.retry.policy.RetryContextCache} <p>The retry context cache parameter is <code>RetryContextCache</code> type.</p>
     * @see  org.springframework.retry.policy.RetryContextCache
     */
    public void setRetryContextCache(RetryContextCache retryContextCache) {
		this.retryContextCache = this.retryContextCache.withStatefulCache(retryContextCache);
	}

    /**
     * <code>setCircuitBreakerRetryContextCache</code>
     * <p>The set circuit breaker retry context cache setter method.</p>
     * @param circuitBreakerRetryContextCache {@link org.springframework.retry.policy.RetryContextCache} <p>The circuit breaker retry context cache parameter is <code>RetryContextCache</code> type.</p>
     * @see  org.springframework.retry.policy.RetryContextCache
     */
    public void setCircuitBreakerRetryContextCache(RetryContextCache circuitBreakerRetryContextCache) {
		this.retryContextCache = retryContextCache.withCircuitBreakerCache(circuitBreakerRetryContextCache);
	}

    /**
     * <code>setListeners</code>
     * <p>The set listeners setter method.</p>
     * @param listeners {@link org.springframework.retry.RetryListener} <p>The listeners parameter is <code>RetryListener</code> type.</p>
     * @see  org.springframework.retry.RetryListener
     */
    public void setListeners(RetryListener[] listeners) {
		Assert.notNull(listeners, "'listeners' must not be null");
		this.listeners = Arrays.copyOf(listeners, listeners.length);
	}

    /**
     * <code>registerListener</code>
     * <p>The register listener method.</p>
     * @param listener {@link org.springframework.retry.RetryListener} <p>The listener parameter is <code>RetryListener</code> type.</p>
     * @see  org.springframework.retry.RetryListener
     */
    public void registerListener(RetryListener listener) {
		registerListener(listener, this.listeners.length);
	}

    /**
     * <code>registerListener</code>
     * <p>The register listener method.</p>
     * @param listener {@link org.springframework.retry.RetryListener} <p>The listener parameter is <code>RetryListener</code> type.</p>
     * @param index int <p>The index parameter is <code>int</code> type.</p>
     * @see  org.springframework.retry.RetryListener
     */
    public void registerListener(RetryListener listener, int index) {
		List<RetryListener> list = new ArrayList<>(Arrays.asList(this.listeners));
		if (index >= list.size()) {
			list.add(listener);
		}
		else {
			list.add(index, listener);
		}
		this.listeners = list.toArray(new RetryListener[0]);
	}

    /**
     * <code>hasListeners</code>
     * <p>The has listeners method.</p>
     * @return  boolean <p>The has listeners return object is <code>boolean</code> type.</p>
     */
    public boolean hasListeners() {
		return this.listeners.length > 0;
	}

    /**
     * <code>setLogger</code>
     * <p>The set logger setter method.</p>
     * @param logger {@link org.apache.commons.logging.Log} <p>The logger parameter is <code>Log</code> type.</p>
     * @see  org.apache.commons.logging.Log
     */
    public void setLogger(Log logger) {
		this.logger = logger;
	}

    /**
     * <code>setBackOffPolicy</code>
     * <p>The set back off policy setter method.</p>
     * @param backOffPolicy {@link org.springframework.retry.backoff.BackOffPolicy} <p>The back off policy parameter is <code>BackOffPolicy</code> type.</p>
     * @see  org.springframework.retry.backoff.BackOffPolicy
     */
    public void setBackOffPolicy(BackOffPolicy backOffPolicy) {
		this.backOffPolicy = backOffPolicy;
	}

    /**
     * <code>setRetryPolicy</code>
     * <p>The set retry policy setter method.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     */
    public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	@Override
	public final <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback) throws E {
		return doExecute(retryCallback, null, null);
	}

	@Override
	public final <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback,
			RecoveryCallback<T> recoveryCallback) throws E {
		return doExecute(retryCallback, recoveryCallback, null);
	}

	@Override
	public final <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback, RetryState retryState)
			throws E, ExhaustedRetryException {
		return doExecute(retryCallback, null, retryState);
	}

	@Override
	public final <T, E extends Throwable> T execute(RetryCallback<T, E> retryCallback,
			RecoveryCallback<T> recoveryCallback, RetryState retryState) throws E, ExhaustedRetryException {
		return doExecute(retryCallback, recoveryCallback, retryState);
	}

    /**
     * <code>doExecute</code>
     * <p>The do execute method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param retryCallback {@link org.springframework.retry.RetryCallback} <p>The retry callback parameter is <code>RetryCallback</code> type.</p>
     * @param recoveryCallback {@link org.springframework.retry.RecoveryCallback} <p>The recovery callback parameter is <code>RecoveryCallback</code> type.</p>
     * @param state {@link org.springframework.retry.RetryState} <p>The state parameter is <code>RetryState</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryCallback
     * @see  org.springframework.retry.RecoveryCallback
     * @see  org.springframework.retry.RetryState
     * @see  E
     * @see  org.springframework.retry.ExhaustedRetryException
     * @return  T <p>The do execute return object is <code>T</code> type.</p>
     * @throws E E <p>The e is <code>E</code> type.</p>
     * @throws ExhaustedRetryException {@link org.springframework.retry.ExhaustedRetryException} <p>The exhausted retry exception is <code>ExhaustedRetryException</code> type.</p>
     */
    protected <T, E extends Throwable> T doExecute(RetryCallback<T, E> retryCallback,
			RecoveryCallback<T> recoveryCallback, RetryState state) throws E, ExhaustedRetryException {

		RetryPolicy retryPolicy = this.retryPolicy;
		BackOffPolicy backOffPolicy = this.backOffPolicy;

		// Allow the retry policy to initialise itself...
		RetryContext context = open(retryPolicy, state);
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("RetryContext retrieved: " + context);
		}

		// Make sure the context is available globally for clients who need
		// it...
		RetrySynchronizationManager.register(context);

		Throwable lastException = null;

		boolean exhausted = false;
		try {

			// Give clients a chance to enhance the context...
			boolean running = doOpenInterceptors(retryCallback, context);

			if (!running) {
				throw new TerminatedRetryException("Retry terminated abnormally by interceptor before first attempt");
			}

			if (!context.hasAttribute(RetryContext.MAX_ATTEMPTS)) {
				context.setAttribute(RetryContext.MAX_ATTEMPTS, retryPolicy.getMaxAttempts());
			}

			// Get or Start the backoff context...
			BackOffContext backOffContext = null;
			Object resource = context.getAttribute("backOffContext");

			if (resource instanceof BackOffContext) {
				backOffContext = (BackOffContext) resource;
			}

			if (backOffContext == null) {
				backOffContext = backOffPolicy.start(context);
				if (backOffContext != null) {
					context.setAttribute("backOffContext", backOffContext);
				}
			}

			Object label = retryCallback.getLabel();
			String labelMessage = (label != null) ? "; for: '" + label + "'" : "";

			/*
			 * We allow the whole loop to be skipped if the policy or context already
			 * forbid the first try. This is used in the case of external retry to allow a
			 * recovery in handleRetryExhausted without the callback processing (which
			 * would throw an exception).
			 */
			while (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {

				try {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Retry: count=" + context.getRetryCount() + labelMessage);
					}
					// Reset the last exception, so if we are successful
					// the close interceptors will not think we failed...
					lastException = null;
					T result = retryCallback.doWithRetry(context);
					doOnSuccessInterceptors(retryCallback, context, result);
					return result;
				}
				catch (Throwable e) {

					lastException = e;

					try {
						registerThrowable(retryPolicy, state, context, e);
					}
					catch (Exception ex) {
						throw new TerminatedRetryException("Could not register throwable", ex);
					}
					finally {
						doOnErrorInterceptors(retryCallback, context, e);
					}

					if (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {
						try {
							backOffPolicy.backOff(backOffContext);
						}
						catch (BackOffInterruptedException ex) {
							// back off was prevented by another thread - fail the retry
							if (this.logger.isDebugEnabled()) {
								this.logger.debug("Abort retry because interrupted: count=" + context.getRetryCount()
										+ labelMessage);
							}
							throw ex;
						}
					}

					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Checking for rethrow: count=" + context.getRetryCount() + labelMessage);
					}

					if (shouldRethrow(retryPolicy, context, state)) {
						if (this.logger.isDebugEnabled()) {
							this.logger
								.debug("Rethrow in retry for policy: count=" + context.getRetryCount() + labelMessage);
						}
						throw RetryTemplate.<E>wrapIfNecessary(e);
					}

				}

				/*
				 * A stateful attempt that can retry may rethrow the exception before now,
				 * but if we get this far in a stateful retry there's a reason for it,
				 * like a circuit breaker or a rollback classifier.
				 */
				if (state != null && context.hasAttribute(GLOBAL_STATE)) {
					break;
				}
			}

			if (state == null && this.logger.isDebugEnabled()) {
				this.logger.debug("Retry failed last attempt: count=" + context.getRetryCount() + labelMessage);
			}

			exhausted = true;
			return handleRetryExhausted(recoveryCallback, context, state);

		}
		catch (Throwable e) {
			throw RetryTemplate.<E>wrapIfNecessary(e);
		}
		finally {
			close(retryPolicy, context, state, lastException == null || exhausted);
			doCloseInterceptors(retryCallback, context, lastException);
			RetrySynchronizationManager.clear();
		}

	}

    /**
     * <code>canRetry</code>
     * <p>The can retry method.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.springframework.retry.RetryContext
     * @return  boolean <p>The can retry return object is <code>boolean</code> type.</p>
     */
    protected boolean canRetry(RetryPolicy retryPolicy, RetryContext context) {
		return retryPolicy.canRetry(context);
	}

    /**
     * <code>close</code>
     * <p>The close method.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param state {@link org.springframework.retry.RetryState} <p>The state parameter is <code>RetryState</code> type.</p>
     * @param succeeded boolean <p>The succeeded parameter is <code>boolean</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryState
     */
    protected void close(RetryPolicy retryPolicy, RetryContext context, RetryState state, boolean succeeded) {
		if (state != null) {
			if (succeeded) {
				this.retryContextCache.remove(state.getKey(), context);
				retryPolicy.close(context);
				context.setAttribute(RetryContext.CLOSED, true);
			}
		}
		else {
			retryPolicy.close(context);
			context.setAttribute(RetryContext.CLOSED, true);
		}
	}

    /**
     * <code>registerThrowable</code>
     * <p>The register throwable method.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @param state {@link org.springframework.retry.RetryState} <p>The state parameter is <code>RetryState</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.springframework.retry.RetryState
     * @see  org.springframework.retry.RetryContext
     * @see  java.lang.Throwable
     */
    protected void registerThrowable(RetryPolicy retryPolicy, RetryState state, RetryContext context, Throwable e) {
		retryPolicy.registerThrowable(context, e);
		registerContext(context, state);
	}

	private void registerContext(RetryContext context, RetryState state) {
		if (state != null) {
			Object key = state.getKey();
			if (key != null) {
				if (context.getRetryCount() > 1 && !this.retryContextCache.containsKey(key, context)) {
					throw new RetryException("Inconsistent state for failed item key: cache key has changed. "
							+ "Consider whether equals() or hashCode() for the key might be inconsistent, "
							+ "or if you need to supply a better key");
				}
				this.retryContextCache.put(key, context);
			}
		}
	}

    /**
     * <code>open</code>
     * <p>The open method.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @param state {@link org.springframework.retry.RetryState} <p>The state parameter is <code>RetryState</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.springframework.retry.RetryState
     * @see  org.springframework.retry.RetryContext
     * @return  {@link org.springframework.retry.RetryContext} <p>The open return object is <code>RetryContext</code> type.</p>
     */
    protected RetryContext open(RetryPolicy retryPolicy, RetryState state) {

		if (state == null) {
			return doOpenInternal(retryPolicy);
		}

		Object key = state.getKey();
		if (state.isForceRefresh()) {
			return doOpenInternal(retryPolicy, state);
		}

		// If there is no cache hit we can avoid the possible expense of the
		// cache re-hydration.
		if (!this.retryContextCache.containsKey(key)) {
			// The cache is only used if there is a failure.
			return doOpenInternal(retryPolicy, state);
		}

		RetryContext context = this.retryContextCache.get(key);
		if (context == null) {
			if (this.retryContextCache.containsKey(key)) {
				throw new RetryException("Inconsistent state for failed item: no history found. "
						+ "Consider whether equals() or hashCode() for the item might be inconsistent, "
						+ "or if you need to supply a better ItemKeyGenerator");
			}
			// The cache could have been expired in between calls to
			// containsKey(), so we have to live with this:
			return doOpenInternal(retryPolicy, state);
		}

		// Start with a clean slate for state that others may be inspecting
		context.removeAttribute(RetryContext.CLOSED);
		context.removeAttribute(RetryContext.EXHAUSTED);
		context.removeAttribute(RetryContext.RECOVERED);
		return context;

	}

	private RetryContext doOpenInternal(RetryPolicy retryPolicy, RetryState state) {
		RetryContext context = retryPolicy.open(RetrySynchronizationManager.getContext());
		if (state != null) {
			context.setAttribute(RetryContext.STATE_KEY, state.getKey());
		}
		if (context.hasAttribute(GLOBAL_STATE)) {
			registerContext(context, state);
		}
		return context;
	}

	private RetryContext doOpenInternal(RetryPolicy retryPolicy) {
		return doOpenInternal(retryPolicy, null);
	}

    /**
     * <code>handleRetryExhausted</code>
     * <p>The handle retry exhausted method.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param recoveryCallback {@link org.springframework.retry.RecoveryCallback} <p>The recovery callback parameter is <code>RecoveryCallback</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param state {@link org.springframework.retry.RetryState} <p>The state parameter is <code>RetryState</code> type.</p>
     * @see  org.springframework.retry.RecoveryCallback
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryState
     * @see  java.lang.Throwable
     * @return  T <p>The handle retry exhausted return object is <code>T</code> type.</p>
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    protected <T> T handleRetryExhausted(RecoveryCallback<T> recoveryCallback, RetryContext context, RetryState state)
			throws Throwable {
		context.setAttribute(RetryContext.EXHAUSTED, true);
		if (state != null) {
			this.retryContextCache.remove(state.getKey(), context);
		}
		boolean doRecover = !Boolean.TRUE.equals(context.getAttribute(RetryContext.NO_RECOVERY));
		if (recoveryCallback != null) {
			if (doRecover) {
				try {
					T recovered = recoveryCallback.recover(context);
					context.setAttribute(RetryContext.RECOVERED, true);
					return recovered;
				}
				catch (UndeclaredThrowableException undeclaredThrowableException) {
					throw wrapIfNecessary(undeclaredThrowableException.getUndeclaredThrowable());
				}
			}
			else {
				logger.debug("Retry exhausted and recovery disabled for this throwable");
			}
		}
		if (state != null) {
			this.logger.debug("Retry exhausted after last attempt with no recovery path.");
			rethrow(context, "Retry exhausted after last attempt with no recovery path",
					this.throwLastExceptionOnExhausted || !doRecover);
		}
		throw wrapIfNecessary(context.getLastThrowable());
	}

    /**
     * <code>rethrow</code>
     * <p>The rethrow method.</p>
     * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param message {@link java.lang.String} <p>The message parameter is <code>String</code> type.</p>
     * @param wrap boolean <p>The wrap parameter is <code>boolean</code> type.</p>
     * @see  java.lang.Throwable
     * @see  org.springframework.retry.RetryContext
     * @see  java.lang.String
     * @see  E
     * @throws E E <p>The e is <code>E</code> type.</p>
     */
    protected <E extends Throwable> void rethrow(RetryContext context, String message, boolean wrap) throws E {
		if (wrap) {
			@SuppressWarnings("unchecked")
			E rethrow = (E) context.getLastThrowable();
			throw rethrow;
		}
		else {
			throw new ExhaustedRetryException(message, context.getLastThrowable());
		}
	}

    /**
     * <code>shouldRethrow</code>
     * <p>The should rethrow method.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @param state {@link org.springframework.retry.RetryState} <p>The state parameter is <code>RetryState</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.springframework.retry.RetryContext
     * @see  org.springframework.retry.RetryState
     * @return  boolean <p>The should rethrow return object is <code>boolean</code> type.</p>
     */
    protected boolean shouldRethrow(RetryPolicy retryPolicy, RetryContext context, RetryState state) {
		return state != null && state.rollbackFor(context.getLastThrowable());
	}

	private <T, E extends Throwable> boolean doOpenInterceptors(RetryCallback<T, E> callback, RetryContext context) {

		boolean result = true;

		for (RetryListener listener : this.listeners) {
			result = result && listener.open(context, callback);
		}

		return result;

	}

	private <T, E extends Throwable> void doCloseInterceptors(RetryCallback<T, E> callback, RetryContext context,
			Throwable lastException) {
		for (int i = this.listeners.length; i-- > 0;) {
			this.listeners[i].close(context, callback, lastException);
		}
	}

	private <T, E extends Throwable> void doOnSuccessInterceptors(RetryCallback<T, E> callback, RetryContext context,
			T result) {
		for (int i = this.listeners.length; i-- > 0;) {
			this.listeners[i].onSuccess(context, callback, result);
		}
	}

	private <T, E extends Throwable> void doOnErrorInterceptors(RetryCallback<T, E> callback, RetryContext context,
			Throwable throwable) {
		for (int i = this.listeners.length; i-- > 0;) {
			this.listeners[i].onError(context, callback, throwable);
		}
	}

	private static <E extends Throwable> E wrapIfNecessary(Throwable throwable) throws RetryException {
		if (throwable instanceof Error) {
			throw (Error) throwable;
		}
		else if (throwable instanceof Exception) {
			@SuppressWarnings("unchecked")
			E rethrow = (E) throwable;
			return rethrow;
		}
		else {
			throw new RetryException("Exception in retry", throwable);
		}
	}

	private static class CompositeRetryContextCache {

		private final RetryContextCache statefulCache;

		private final RetryContextCache circuitBreakerCache;

        /**
         * <code>CompositeRetryContextCache</code>
         * <p>Instantiates a new composite retry context cache.</p>
         */
        public CompositeRetryContextCache() {
			this(new MapRetryContextCache(MapRetryContextCache.DEFAULT_CAPACITY, true),
					new MapRetryContextCache(MapRetryContextCache.DEFAULT_CAPACITY, false));
		}

		private CompositeRetryContextCache(RetryContextCache statefulCache, RetryContextCache circuitBreakerCache) {
			this.statefulCache = statefulCache;
			this.circuitBreakerCache = circuitBreakerCache;
		}

        /**
         * <code>withStatefulCache</code>
         * <p>The with stateful cache method.</p>
         * @param statefulCache {@link org.springframework.retry.policy.RetryContextCache} <p>The stateful cache parameter is <code>RetryContextCache</code> type.</p>
         * @see  org.springframework.retry.policy.RetryContextCache
         * @return  {@link org.springframework.retry.support.RetryTemplate.CompositeRetryContextCache} <p>The with stateful cache return object is <code>CompositeRetryContextCache</code> type.</p>
         */
        CompositeRetryContextCache withStatefulCache(RetryContextCache statefulCache) {
			return new CompositeRetryContextCache(statefulCache, this.circuitBreakerCache);
		}

        /**
         * <code>withCircuitBreakerCache</code>
         * <p>The with circuit breaker cache method.</p>
         * @param circuitBreakerCache {@link org.springframework.retry.policy.RetryContextCache} <p>The circuit breaker cache parameter is <code>RetryContextCache</code> type.</p>
         * @see  org.springframework.retry.policy.RetryContextCache
         * @return  {@link org.springframework.retry.support.RetryTemplate.CompositeRetryContextCache} <p>The with circuit breaker cache return object is <code>CompositeRetryContextCache</code> type.</p>
         */
        CompositeRetryContextCache withCircuitBreakerCache(RetryContextCache circuitBreakerCache) {
			return new CompositeRetryContextCache(this.statefulCache, circuitBreakerCache);
		}

        /**
         * <code>get</code>
         * <p>The get method.</p>
         * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
         * @see  java.lang.Object
         * @see  org.springframework.retry.RetryContext
         * @return  {@link org.springframework.retry.RetryContext} <p>The get return object is <code>RetryContext</code> type.</p>
         */
        public RetryContext get(Object key) {
			RetryContext retryContext = this.statefulCache.get(key);
			return (retryContext != null) ? retryContext : this.circuitBreakerCache.get(key);
		}

        /**
         * <code>put</code>
         * <p>The put method.</p>
         * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
         * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
         * @see  java.lang.Object
         * @see  org.springframework.retry.RetryContext
         */
        public void put(Object key, RetryContext context) {
			if (context.hasAttribute(GLOBAL_STATE)) {
				this.circuitBreakerCache.put(key, context);
			}
			else {
				this.statefulCache.put(key, context);
			}
		}

        /**
         * <code>remove</code>
         * <p>The remove method.</p>
         * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
         * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
         * @see  java.lang.Object
         * @see  org.springframework.retry.RetryContext
         */
        public void remove(Object key, RetryContext context) {
			if (!context.hasAttribute(GLOBAL_STATE)) {
				this.statefulCache.remove(key);
			}
		}

        /**
         * <code>containsKey</code>
         * <p>The contains key method.</p>
         * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
         * @see  java.lang.Object
         * @return  boolean <p>The contains key return object is <code>boolean</code> type.</p>
         */
        public boolean containsKey(Object key) {
			return this.statefulCache.containsKey(key) || this.circuitBreakerCache.containsKey(key);
		}

        /**
         * <code>containsKey</code>
         * <p>The contains key method.</p>
         * @param key {@link java.lang.Object} <p>The key parameter is <code>Object</code> type.</p>
         * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
         * @see  java.lang.Object
         * @see  org.springframework.retry.RetryContext
         * @return  boolean <p>The contains key return object is <code>boolean</code> type.</p>
         */
        public boolean containsKey(Object key, RetryContext context) {
			if (context.hasAttribute(GLOBAL_STATE)) {
				return this.circuitBreakerCache.containsKey(key);
			}
			else {
				return this.statefulCache.containsKey(key);
			}
		}

	}

}
