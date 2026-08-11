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

package org.springframework.retry.backoff;

import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.retry.RetryContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * <code>ExponentialBackOffPolicy</code>
 * <p>The exponential back off policy class.</p>
 * @see  org.springframework.retry.backoff.SleepingBackOffPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ExponentialBackOffPolicy implements SleepingBackOffPolicy<ExponentialBackOffPolicy> {

    /**
     * <code>logger</code>
     * {@link org.apache.commons.logging.Log} <p>The <code>logger</code> field.</p>
     * @see  org.apache.commons.logging.Log
     */
    protected final Log logger = LogFactory.getLog(this.getClass());

    /**
     * <code>DEFAULT_INITIAL_INTERVAL</code>
     * <p>The constant <code>DEFAULT_INITIAL_INTERVAL</code> field.</p>
     */
    public static final long DEFAULT_INITIAL_INTERVAL = 100L;

    /**
     * <code>DEFAULT_MAX_INTERVAL</code>
     * <p>The constant <code>DEFAULT_MAX_INTERVAL</code> field.</p>
     */
    public static final long DEFAULT_MAX_INTERVAL = 30000L;

    /**
     * <code>DEFAULT_MULTIPLIER</code>
     * <p>The constant <code>DEFAULT_MULTIPLIER</code> field.</p>
     */
    public static final double DEFAULT_MULTIPLIER = 2;

	private long initialInterval = DEFAULT_INITIAL_INTERVAL;

	private long maxInterval = DEFAULT_MAX_INTERVAL;

	private double multiplier = DEFAULT_MULTIPLIER;

	private Supplier<Long> initialIntervalSupplier;

	private Supplier<Long> maxIntervalSupplier;

	private Supplier<Double> multiplierSupplier;

	private Sleeper sleeper = new ThreadWaitSleeper();

    /**
     * <code>setSleeper</code>
     * <p>The set sleeper setter method.</p>
     * @param sleeper {@link org.springframework.retry.backoff.Sleeper} <p>The sleeper parameter is <code>Sleeper</code> type.</p>
     * @see  org.springframework.retry.backoff.Sleeper
     */
    public void setSleeper(Sleeper sleeper) {
		this.sleeper = sleeper;
	}

	@Override
	public ExponentialBackOffPolicy withSleeper(Sleeper sleeper) {
		ExponentialBackOffPolicy res = newInstance();
		cloneValues(res);
		res.setSleeper(sleeper);
		return res;
	}

    /**
     * <code>newInstance</code>
     * <p>The new instance method.</p>
     * @return  {@link org.springframework.retry.backoff.ExponentialBackOffPolicy} <p>The new instance return object is <code>ExponentialBackOffPolicy</code> type.</p>
     */
    protected ExponentialBackOffPolicy newInstance() {
		return new ExponentialBackOffPolicy();
	}

    /**
     * <code>cloneValues</code>
     * <p>The clone values method.</p>
     * @param target {@link org.springframework.retry.backoff.ExponentialBackOffPolicy} <p>The target parameter is <code>ExponentialBackOffPolicy</code> type.</p>
     */
    protected void cloneValues(ExponentialBackOffPolicy target) {
		target.setInitialInterval(getInitialInterval());
		target.setMaxInterval(getMaxInterval());
		target.setMultiplier(getMultiplier());
		target.setSleeper(this.sleeper);
	}

    /**
     * <code>setInitialInterval</code>
     * <p>The set initial interval setter method.</p>
     * @param initialInterval long <p>The initial interval parameter is <code>long</code> type.</p>
     */
    public void setInitialInterval(long initialInterval) {
		if (initialInterval < 1) {
			logger.warn("Initial interval must be at least 1, but was " + initialInterval);
		}
		this.initialInterval = initialInterval > 1 ? initialInterval : 1;
	}

    /**
     * <code>setMultiplier</code>
     * <p>The set multiplier setter method.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     */
    public void setMultiplier(double multiplier) {
		if (multiplier <= 1.0) {
			logger.warn("Multiplier must be > 1.0 for effective exponential backoff, but was " + multiplier);
		}
		this.multiplier = multiplier > 1.0 ? multiplier : 1.0;
	}

    /**
     * <code>setMaxInterval</code>
     * <p>The set max interval setter method.</p>
     * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
     */
    public void setMaxInterval(long maxInterval) {
		if (maxInterval < 1) {
			logger.warn("Max interval must be positive, but was " + maxInterval);
		}
		this.maxInterval = maxInterval > 0 ? maxInterval : 1;
	}

    /**
     * <code>initialIntervalSupplier</code>
     * <p>The initial interval supplier method.</p>
     * @param initialIntervalSupplier {@link java.util.function.Supplier} <p>The initial interval supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void initialIntervalSupplier(Supplier<Long> initialIntervalSupplier) {
		Assert.notNull(initialIntervalSupplier, "'initialIntervalSupplier' cannot be null");
		this.initialIntervalSupplier = initialIntervalSupplier;
	}

    /**
     * <code>multiplierSupplier</code>
     * <p>The multiplier supplier method.</p>
     * @param multiplierSupplier {@link java.util.function.Supplier} <p>The multiplier supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void multiplierSupplier(Supplier<Double> multiplierSupplier) {
		Assert.notNull(multiplierSupplier, "'multiplierSupplier' cannot be null");
		this.multiplierSupplier = multiplierSupplier;
	}

    /**
     * <code>maxIntervalSupplier</code>
     * <p>The max interval supplier method.</p>
     * @param maxIntervalSupplier {@link java.util.function.Supplier} <p>The max interval supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void maxIntervalSupplier(Supplier<Long> maxIntervalSupplier) {
		Assert.notNull(maxIntervalSupplier, "'maxIntervalSupplier' cannot be null");
		this.maxIntervalSupplier = maxIntervalSupplier;
	}

    /**
     * <code>getInitialIntervalSupplier</code>
     * <p>The get initial interval supplier getter method.</p>
     * @return  {@link java.util.function.Supplier} <p>The get initial interval supplier return object is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    protected Supplier<Long> getInitialIntervalSupplier() {
		return initialIntervalSupplier;
	}

    /**
     * <code>getMaxIntervalSupplier</code>
     * <p>The get max interval supplier getter method.</p>
     * @return  {@link java.util.function.Supplier} <p>The get max interval supplier return object is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    protected Supplier<Long> getMaxIntervalSupplier() {
		return maxIntervalSupplier;
	}

    /**
     * <code>getMultiplierSupplier</code>
     * <p>The get multiplier supplier getter method.</p>
     * @return  {@link java.util.function.Supplier} <p>The get multiplier supplier return object is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    protected Supplier<Double> getMultiplierSupplier() {
		return multiplierSupplier;
	}

    /**
     * <code>getInitialInterval</code>
     * <p>The get initial interval getter method.</p>
     * @return  long <p>The get initial interval return object is <code>long</code> type.</p>
     */
    public long getInitialInterval() {
		return this.initialIntervalSupplier != null ? this.initialIntervalSupplier.get() : this.initialInterval;
	}

    /**
     * <code>getMaxInterval</code>
     * <p>The get max interval getter method.</p>
     * @return  long <p>The get max interval return object is <code>long</code> type.</p>
     */
    public long getMaxInterval() {
		return this.maxIntervalSupplier != null ? this.maxIntervalSupplier.get() : this.maxInterval;
	}

    /**
     * <code>getMultiplier</code>
     * <p>The get multiplier getter method.</p>
     * @return  double <p>The get multiplier return object is <code>double</code> type.</p>
     */
    public double getMultiplier() {
		return this.multiplierSupplier != null ? this.multiplierSupplier.get() : this.multiplier;
	}

	@Override
	public BackOffContext start(RetryContext context) {
		return new ExponentialBackOffContext(this.initialInterval, this.multiplier, this.maxInterval,
				this.initialIntervalSupplier, this.multiplierSupplier, this.maxIntervalSupplier);
	}

	@Override
	public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
		ExponentialBackOffContext context = (ExponentialBackOffContext) backOffContext;
		try {
			long sleepTime = context.getSleepAndIncrement();
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Sleeping for " + sleepTime);
			}
			this.sleeper.sleep(sleepTime);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
		}
	}

    /**
     * <code>ExponentialBackOffContext</code>
     * <p>The exponential back off context class.</p>
     * @see  org.springframework.retry.backoff.BackOffContext
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class ExponentialBackOffContext implements BackOffContext {

		private final double multiplier;

		private long interval;

		private final long maxInterval;

		private Supplier<Long> initialIntervalSupplier;

		private Supplier<Double> multiplierSupplier;

		private Supplier<Long> maxIntervalSupplier;

        /**
         * <code>ExponentialBackOffContext</code>
         * <p>Instantiates a new exponential back off context.</p>
         * @param interval long <p>The interval parameter is <code>long</code> type.</p>
         * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
         * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
         * @param intervalSupplier {@link java.util.function.Supplier} <p>The interval supplier parameter is <code>Supplier</code> type.</p>
         * @param multiplierSupplier {@link java.util.function.Supplier} <p>The multiplier supplier parameter is <code>Supplier</code> type.</p>
         * @param maxIntervalSupplier {@link java.util.function.Supplier} <p>The max interval supplier parameter is <code>Supplier</code> type.</p>
         * @see  java.util.function.Supplier
         */
        public ExponentialBackOffContext(long interval, double multiplier, long maxInterval,
				Supplier<Long> intervalSupplier, Supplier<Double> multiplierSupplier,
				Supplier<Long> maxIntervalSupplier) {
			this.interval = interval;
			this.multiplier = multiplier;
			this.maxInterval = maxInterval;
			this.initialIntervalSupplier = intervalSupplier;
			this.multiplierSupplier = multiplierSupplier;
			this.maxIntervalSupplier = maxIntervalSupplier;
		}

        /**
         * <code>getSleepAndIncrement</code>
         * <p>The get sleep and increment getter method.</p>
         * @return  long <p>The get sleep and increment return object is <code>long</code> type.</p>
         */
        public synchronized long getSleepAndIncrement() {
			long sleep = getInterval();
			long max = getMaxInterval();
			if (sleep > max) {
				sleep = max;
			}
			else {
				this.interval = getNextInterval();
			}
			return sleep;
		}

        /**
         * <code>getNextInterval</code>
         * <p>The get next interval getter method.</p>
         * @return  long <p>The get next interval return object is <code>long</code> type.</p>
         */
        protected long getNextInterval() {
			return (long) (this.interval * getMultiplier());
		}

        /**
         * <code>getMultiplier</code>
         * <p>The get multiplier getter method.</p>
         * @return  double <p>The get multiplier return object is <code>double</code> type.</p>
         */
        public double getMultiplier() {
			return this.multiplierSupplier != null ? this.multiplierSupplier.get() : this.multiplier;
		}

        /**
         * <code>getInterval</code>
         * <p>The get interval getter method.</p>
         * @return  long <p>The get interval return object is <code>long</code> type.</p>
         */
        public long getInterval() {
			if (this.initialIntervalSupplier != null) {
				this.interval = this.initialIntervalSupplier.get();
				this.initialIntervalSupplier = null;
			}
			return this.interval;
		}

        /**
         * <code>getMaxInterval</code>
         * <p>The get max interval getter method.</p>
         * @return  long <p>The get max interval return object is <code>long</code> type.</p>
         */
        public long getMaxInterval() {
			return this.maxIntervalSupplier != null ? this.maxIntervalSupplier.get() : this.maxInterval;
		}

	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[initialInterval=" + getInitialInterval() + ", multiplier="
				+ getMultiplier() + ", maxInterval=" + getMaxInterval() + "]";
	}

}
