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

import java.util.Random;
import java.util.function.Supplier;

import org.springframework.util.Assert;

/**
 * <code>UniformRandomBackOffPolicy</code>
 * <p>The uniform random back off policy class.</p>
 * @see  org.springframework.retry.backoff.StatelessBackOffPolicy
 * @see  org.springframework.retry.backoff.SleepingBackOffPolicy
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class UniformRandomBackOffPolicy extends StatelessBackOffPolicy
		implements SleepingBackOffPolicy<UniformRandomBackOffPolicy> {

	private static final long DEFAULT_BACK_OFF_MIN_PERIOD = 500L;

	private static final long DEFAULT_BACK_OFF_MAX_PERIOD = 1500L;

	private Supplier<Long> minBackOffPeriod = () -> DEFAULT_BACK_OFF_MIN_PERIOD;

	private Supplier<Long> maxBackOffPeriod = () -> DEFAULT_BACK_OFF_MAX_PERIOD;

	private final Random random = new Random(System.currentTimeMillis());

	private Sleeper sleeper = new ThreadWaitSleeper();

	public UniformRandomBackOffPolicy withSleeper(Sleeper sleeper) {
		UniformRandomBackOffPolicy res = new UniformRandomBackOffPolicy();
		res.minBackOffPeriodSupplier(minBackOffPeriod);
		res.maxBackOffPeriodSupplier(maxBackOffPeriod);
		res.setSleeper(sleeper);
		return res;
	}

    /**
     * <code>setSleeper</code>
     * <p>The set sleeper setter method.</p>
     * @param sleeper {@link org.springframework.retry.backoff.Sleeper} <p>The sleeper parameter is <code>Sleeper</code> type.</p>
     * @see  org.springframework.retry.backoff.Sleeper
     */
    public void setSleeper(Sleeper sleeper) {
		this.sleeper = sleeper;
	}

    /**
     * <code>setMinBackOffPeriod</code>
     * <p>The set min back off period setter method.</p>
     * @param backOffPeriod long <p>The back off period parameter is <code>long</code> type.</p>
     */
    public void setMinBackOffPeriod(long backOffPeriod) {
		this.minBackOffPeriod = () -> (backOffPeriod > 0 ? backOffPeriod : 1);
	}

    /**
     * <code>minBackOffPeriodSupplier</code>
     * <p>The min back off period supplier method.</p>
     * @param backOffPeriodSupplier {@link java.util.function.Supplier} <p>The back off period supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void minBackOffPeriodSupplier(Supplier<Long> backOffPeriodSupplier) {
		Assert.notNull(backOffPeriodSupplier, "'backOffPeriodSupplier' cannot be null");
		this.minBackOffPeriod = backOffPeriodSupplier;
	}

    /**
     * <code>getMinBackOffPeriod</code>
     * <p>The get min back off period getter method.</p>
     * @return  long <p>The get min back off period return object is <code>long</code> type.</p>
     */
    public long getMinBackOffPeriod() {
		return minBackOffPeriod.get();
	}

    /**
     * <code>setMaxBackOffPeriod</code>
     * <p>The set max back off period setter method.</p>
     * @param backOffPeriod long <p>The back off period parameter is <code>long</code> type.</p>
     */
    public void setMaxBackOffPeriod(long backOffPeriod) {
		this.maxBackOffPeriod = () -> (backOffPeriod > 0 ? backOffPeriod : 1);
	}

    /**
     * <code>maxBackOffPeriodSupplier</code>
     * <p>The max back off period supplier method.</p>
     * @param backOffPeriodSupplier {@link java.util.function.Supplier} <p>The back off period supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void maxBackOffPeriodSupplier(Supplier<Long> backOffPeriodSupplier) {
		Assert.notNull(backOffPeriodSupplier, "'backOffPeriodSupplier' cannot be null");
		this.maxBackOffPeriod = backOffPeriodSupplier;
	}

    /**
     * <code>getMaxBackOffPeriod</code>
     * <p>The get max back off period getter method.</p>
     * @return  long <p>The get max back off period return object is <code>long</code> type.</p>
     */
    public long getMaxBackOffPeriod() {
		return maxBackOffPeriod.get();
	}

	protected void doBackOff() throws BackOffInterruptedException {
		try {
			Long min = this.minBackOffPeriod.get();
			Long max = this.maxBackOffPeriod.get();
			long delta = max <= min ? 0 : this.random.nextInt((int) (max - min));
			this.sleeper.sleep(min + delta);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
		}
	}

	public String toString() {
		return "RandomBackOffPolicy[backOffPeriod=" + minBackOffPeriod + ", " + maxBackOffPeriod + "]";
	}

}
