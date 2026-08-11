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

package org.springframework.retry.support;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.logging.Log;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.BinaryExceptionClassifierBuilder;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.BinaryExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.policy.PredicateRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.util.Assert;

/**
 * <code>RetryTemplateBuilder</code>
 * <p>The retry template builder class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetryTemplateBuilder {

	private RetryPolicy baseRetryPolicy;

	private Log logger;

	private BackOffPolicy backOffPolicy;

	private List<RetryListener> listeners;

	private BinaryExceptionClassifierBuilder classifierBuilder;

	private Predicate<Throwable> retryOnPredicate;

	/* ---------------- Configure retry policy -------------- */

    /**
     * <code>maxAttempts</code>
     * <p>The max attempts method.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The max attempts return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder maxAttempts(int maxAttempts) {
		Assert.isTrue(maxAttempts > 0, "Number of attempts should be positive");
		Assert.isNull(this.baseRetryPolicy, "You have already selected another retry policy");
		this.baseRetryPolicy = new MaxAttemptsRetryPolicy(maxAttempts);
		return this;
	}

    /**
     * <code>withinMillis</code>
     * <p>The within millis method.</p>
     * @deprecated  <p>The within millis method has be deprecated.</p>
     * @param timeout long <p>The timeout parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The within millis return object is <code>RetryTemplateBuilder</code> type.</p>
     * @see  java.lang.Deprecated
     */
    @Deprecated
	public RetryTemplateBuilder withinMillis(long timeout) {
		return withTimeout(timeout);
	}

    /**
     * <code>withTimeout</code>
     * <p>The with timeout method.</p>
     * @param timeoutMillis long <p>The timeout millis parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The with timeout return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder withTimeout(long timeoutMillis) {
		Assert.isTrue(timeoutMillis > 0, "timeoutMillis should be greater than 0");
		Assert.isNull(this.baseRetryPolicy, "You have already selected another retry policy");
		this.baseRetryPolicy = new TimeoutRetryPolicy(timeoutMillis);
		return this;
	}

    /**
     * <code>withTimeout</code>
     * <p>The with timeout method.</p>
     * @param timeout {@link java.time.Duration} <p>The timeout parameter is <code>Duration</code> type.</p>
     * @see  java.time.Duration
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The with timeout return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder withTimeout(Duration timeout) {
		Assert.notNull(timeout, "timeout must not be null");
		return withTimeout(timeout.toMillis());
	}

    /**
     * <code>infiniteRetry</code>
     * <p>The infinite retry method.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The infinite retry return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder infiniteRetry() {
		Assert.isNull(this.baseRetryPolicy, "You have already selected another retry policy");
		this.baseRetryPolicy = new AlwaysRetryPolicy();
		return this;
	}

    /**
     * <code>customPolicy</code>
     * <p>The custom policy method.</p>
     * @param policy {@link org.springframework.retry.RetryPolicy} <p>The policy parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The custom policy return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder customPolicy(RetryPolicy policy) {
		Assert.notNull(policy, "Policy should not be null");
		Assert.isNull(this.baseRetryPolicy, "You have already selected another retry policy");
		this.baseRetryPolicy = policy;
		return this;
	}

	/* ---------------- Configure backoff policy -------------- */

    /**
     * <code>exponentialBackoff</code>
     * <p>The exponential backoff method.</p>
     * @param initialInterval long <p>The initial interval parameter is <code>long</code> type.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The exponential backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder exponentialBackoff(long initialInterval, double multiplier, long maxInterval) {
		return exponentialBackoff(initialInterval, multiplier, maxInterval, false);
	}

    /**
     * <code>exponentialBackoff</code>
     * <p>The exponential backoff method.</p>
     * @param initialInterval {@link java.time.Duration} <p>The initial interval parameter is <code>Duration</code> type.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     * @param maxInterval {@link java.time.Duration} <p>The max interval parameter is <code>Duration</code> type.</p>
     * @see  java.time.Duration
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The exponential backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder exponentialBackoff(Duration initialInterval, double multiplier, Duration maxInterval) {
		Assert.notNull(initialInterval, "initialInterval must not be null");
		Assert.notNull(maxInterval, "maxInterval must not be null");
		return exponentialBackoff(initialInterval.toMillis(), multiplier, maxInterval.toMillis(), false);
	}

    /**
     * <code>exponentialBackoff</code>
     * <p>The exponential backoff method.</p>
     * @param initialInterval long <p>The initial interval parameter is <code>long</code> type.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
     * @param withRandom boolean <p>The with random parameter is <code>boolean</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The exponential backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder exponentialBackoff(long initialInterval, double multiplier, long maxInterval,
			boolean withRandom) {
		Assert.isNull(this.backOffPolicy, "You have already selected backoff policy");
		Assert.isTrue(initialInterval >= 1, "Initial interval should be >= 1");
		Assert.isTrue(multiplier > 1, "Multiplier should be > 1");
		Assert.isTrue(maxInterval > initialInterval, "Max interval should be > than initial interval");
		ExponentialBackOffPolicy policy = withRandom ? new ExponentialRandomBackOffPolicy()
				: new ExponentialBackOffPolicy();
		policy.setInitialInterval(initialInterval);
		policy.setMultiplier(multiplier);
		policy.setMaxInterval(maxInterval);
		this.backOffPolicy = policy;
		return this;
	}

    /**
     * <code>exponentialBackoff</code>
     * <p>The exponential backoff method.</p>
     * @param initialInterval {@link java.time.Duration} <p>The initial interval parameter is <code>Duration</code> type.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     * @param maxInterval {@link java.time.Duration} <p>The max interval parameter is <code>Duration</code> type.</p>
     * @param withRandom boolean <p>The with random parameter is <code>boolean</code> type.</p>
     * @see  java.time.Duration
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The exponential backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder exponentialBackoff(Duration initialInterval, double multiplier, Duration maxInterval,
			boolean withRandom) {
		Assert.notNull(initialInterval, "initialInterval most not be null");
		Assert.notNull(maxInterval, "maxInterval must not be null");
		return this.exponentialBackoff(initialInterval.toMillis(), multiplier, maxInterval.toMillis(), withRandom);
	}

    /**
     * <code>withLogger</code>
     * <p>The with logger method.</p>
     * @param logger {@link org.apache.commons.logging.Log} <p>The logger parameter is <code>Log</code> type.</p>
     * @see  org.apache.commons.logging.Log
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The with logger return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder withLogger(Log logger) {
		Assert.isNull(this.logger, "You have already applied a logger");
		Assert.notNull(logger, "The given logger should not be null");
		this.logger = logger;
		return this;
	}

    /**
     * <code>fixedBackoff</code>
     * <p>The fixed backoff method.</p>
     * @param interval long <p>The interval parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The fixed backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder fixedBackoff(long interval) {
		Assert.isNull(this.backOffPolicy, "You have already selected backoff policy");
		Assert.isTrue(interval >= 1, "Interval should be >= 1");
		FixedBackOffPolicy policy = new FixedBackOffPolicy();
		policy.setBackOffPeriod(interval);
		this.backOffPolicy = policy;
		return this;
	}

    /**
     * <code>fixedBackoff</code>
     * <p>The fixed backoff method.</p>
     * @param interval {@link java.time.Duration} <p>The interval parameter is <code>Duration</code> type.</p>
     * @see  java.time.Duration
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The fixed backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder fixedBackoff(Duration interval) {
		Assert.notNull(interval, "interval must not be null");

		long millis = interval.toMillis();
		Assert.isTrue(millis >= 1, "interval is less than 1 millisecond");

		return this.fixedBackoff(millis);
	}

    /**
     * <code>uniformRandomBackoff</code>
     * <p>The uniform random backoff method.</p>
     * @param minInterval long <p>The min interval parameter is <code>long</code> type.</p>
     * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The uniform random backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder uniformRandomBackoff(long minInterval, long maxInterval) {
		Assert.isNull(this.backOffPolicy, "You have already selected backoff policy");
		Assert.isTrue(minInterval >= 1, "Min interval should be >= 1");
		Assert.isTrue(maxInterval >= 1, "Max interval should be >= 1");
		Assert.isTrue(maxInterval > minInterval, "Max interval should be > than min interval");
		UniformRandomBackOffPolicy policy = new UniformRandomBackOffPolicy();
		policy.setMinBackOffPeriod(minInterval);
		policy.setMaxBackOffPeriod(maxInterval);
		this.backOffPolicy = policy;
		return this;
	}

    /**
     * <code>uniformRandomBackoff</code>
     * <p>The uniform random backoff method.</p>
     * @param minInterval {@link java.time.Duration} <p>The min interval parameter is <code>Duration</code> type.</p>
     * @param maxInterval {@link java.time.Duration} <p>The max interval parameter is <code>Duration</code> type.</p>
     * @see  java.time.Duration
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The uniform random backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder uniformRandomBackoff(Duration minInterval, Duration maxInterval) {
		Assert.notNull(minInterval, "minInterval must not be null");
		Assert.notNull(maxInterval, "maxInterval must not be null");
		return this.uniformRandomBackoff(minInterval.toMillis(), maxInterval.toMillis());
	}

    /**
     * <code>noBackoff</code>
     * <p>The no backoff method.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The no backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder noBackoff() {
		Assert.isNull(this.backOffPolicy, "You have already selected backoff policy");
		this.backOffPolicy = new NoBackOffPolicy();
		return this;
	}

    /**
     * <code>customBackoff</code>
     * <p>The custom backoff method.</p>
     * @param backOffPolicy {@link org.springframework.retry.backoff.BackOffPolicy} <p>The back off policy parameter is <code>BackOffPolicy</code> type.</p>
     * @see  org.springframework.retry.backoff.BackOffPolicy
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The custom backoff return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder customBackoff(BackOffPolicy backOffPolicy) {
		Assert.isNull(this.backOffPolicy, "You have already selected backoff policy");
		Assert.notNull(backOffPolicy, "You should provide non null custom policy");
		this.backOffPolicy = backOffPolicy;
		return this;
	}

	/* ---------------- Configure exception classifier -------------- */

    /**
     * <code>retryOn</code>
     * <p>The retry on method.</p>
     * @param throwable {@link java.lang.Class} <p>The throwable parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The retry on return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder retryOn(Class<? extends Throwable> throwable) {
		classifierBuilder().retryOn(throwable);
		return this;
	}

    /**
     * <code>notRetryOn</code>
     * <p>The not retry on method.</p>
     * @param throwable {@link java.lang.Class} <p>The throwable parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The not retry on return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder notRetryOn(Class<? extends Throwable> throwable) {
		classifierBuilder().notRetryOn(throwable);
		return this;
	}

    /**
     * <code>retryOn</code>
     * <p>The retry on method.</p>
     * @param throwables {@link java.util.List} <p>The throwables parameter is <code>List</code> type.</p>
     * @see  java.util.List
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The retry on return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder retryOn(List<Class<? extends Throwable>> throwables) {
		for (final Class<? extends Throwable> throwable : throwables) {
			classifierBuilder().retryOn(throwable);
		}
		return this;
	}

    /**
     * <code>notRetryOn</code>
     * <p>The not retry on method.</p>
     * @param throwables {@link java.util.List} <p>The throwables parameter is <code>List</code> type.</p>
     * @see  java.util.List
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The not retry on return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder notRetryOn(List<Class<? extends Throwable>> throwables) {
		for (final Class<? extends Throwable> throwable : throwables) {
			classifierBuilder().notRetryOn(throwable);
		}
		return this;
	}

    /**
     * <code>retryOn</code>
     * <p>The retry on method.</p>
     * @param predicate {@link java.util.function.Predicate} <p>The predicate parameter is <code>Predicate</code> type.</p>
     * @see  java.util.function.Predicate
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The retry on return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder retryOn(Predicate<Throwable> predicate) {
		Assert.isTrue(this.classifierBuilder == null && this.retryOnPredicate == null,
				"retryOn(Predicate<Throwable>) cannot be mixed with other retryOn() or noRetryOn()");
		Assert.notNull(predicate, "Predicate can not be null");
		this.retryOnPredicate = predicate;
		return this;
	}

    /**
     * <code>traversingCauses</code>
     * <p>The traversing causes method.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The traversing causes return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder traversingCauses() {
		classifierBuilder().traversingCauses();
		return this;
	}

	/* ---------------- Add listeners -------------- */

    /**
     * <code>withListener</code>
     * <p>The with listener method.</p>
     * @param listener {@link org.springframework.retry.RetryListener} <p>The listener parameter is <code>RetryListener</code> type.</p>
     * @see  org.springframework.retry.RetryListener
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The with listener return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder withListener(RetryListener listener) {
		Assert.notNull(listener, "Listener should not be null");
		listenersList().add(listener);
		return this;
	}

    /**
     * <code>withListeners</code>
     * <p>The with listeners method.</p>
     * @param listeners {@link java.util.List} <p>The listeners parameter is <code>List</code> type.</p>
     * @see  java.util.List
     * @return  {@link org.springframework.retry.support.RetryTemplateBuilder} <p>The with listeners return object is <code>RetryTemplateBuilder</code> type.</p>
     */
    public RetryTemplateBuilder withListeners(List<RetryListener> listeners) {
		for (final RetryListener listener : listeners) {
			Assert.notNull(listener, "Listener should not be null");
		}
		listenersList().addAll(listeners);
		return this;
	}

	/* ---------------- Building -------------- */

    /**
     * <code>build</code>
     * <p>The build method.</p>
     * @return  {@link org.springframework.retry.support.RetryTemplate} <p>The build return object is <code>RetryTemplate</code> type.</p>
     */
    public RetryTemplate build() {
		RetryTemplate retryTemplate = new RetryTemplate();

		// Retry policy

		if (this.baseRetryPolicy == null) {
			this.baseRetryPolicy = new MaxAttemptsRetryPolicy();
		}

		RetryPolicy exceptionRetryPolicy;
		if (this.retryOnPredicate == null) {
			BinaryExceptionClassifier exceptionClassifier = this.classifierBuilder != null
					? this.classifierBuilder.build() : BinaryExceptionClassifier.defaultClassifier();
			exceptionRetryPolicy = new BinaryExceptionClassifierRetryPolicy(exceptionClassifier);
		}
		else {
			exceptionRetryPolicy = new PredicateRetryPolicy(this.retryOnPredicate);
		}

		CompositeRetryPolicy finalPolicy = new CompositeRetryPolicy();
		finalPolicy.setPolicies(new RetryPolicy[] { this.baseRetryPolicy, exceptionRetryPolicy });
		retryTemplate.setRetryPolicy(finalPolicy);

		// Logger

		if (this.logger != null) {
			retryTemplate.setLogger(this.logger);
		}

		// Backoff policy

		if (this.backOffPolicy == null) {
			this.backOffPolicy = new NoBackOffPolicy();
		}
		retryTemplate.setBackOffPolicy(this.backOffPolicy);

		// Listeners

		if (this.listeners != null) {
			retryTemplate.setListeners(this.listeners.toArray(new RetryListener[0]));
		}

		return retryTemplate;
	}

	/* ---------------- Private utils -------------- */

	private BinaryExceptionClassifierBuilder classifierBuilder() {
		if (this.classifierBuilder == null) {
			this.classifierBuilder = new BinaryExceptionClassifierBuilder();
		}
		return this.classifierBuilder;
	}

	private List<RetryListener> listenersList() {
		if (this.listeners == null) {
			this.listeners = new ArrayList<>();
		}
		return this.listeners;
	}

}
