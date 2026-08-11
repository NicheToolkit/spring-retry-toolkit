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

import org.springframework.util.Assert;

/**
 * <code>FixedBackOffPolicy</code>
 * <p>The fixed back off policy class.</p>
 * @see  org.springframework.retry.backoff.StatelessBackOffPolicy
 * @see  org.springframework.retry.backoff.SleepingBackOffPolicy
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class FixedBackOffPolicy extends StatelessBackOffPolicy implements SleepingBackOffPolicy<FixedBackOffPolicy> {

	private static final long DEFAULT_BACK_OFF_PERIOD = 1000L;

	private Supplier<Long> backOffPeriod = () -> DEFAULT_BACK_OFF_PERIOD;

	private Sleeper sleeper = new ThreadWaitSleeper();

	public FixedBackOffPolicy withSleeper(Sleeper sleeper) {
		FixedBackOffPolicy res = new FixedBackOffPolicy();
		res.backOffPeriodSupplier(backOffPeriod);
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
     * <code>setBackOffPeriod</code>
     * <p>The set back off period setter method.</p>
     * @param backOffPeriod long <p>The back off period parameter is <code>long</code> type.</p>
     */
    public void setBackOffPeriod(long backOffPeriod) {
		this.backOffPeriod = () -> (backOffPeriod > 0 ? backOffPeriod : 1);
	}

    /**
     * <code>backOffPeriodSupplier</code>
     * <p>The back off period supplier method.</p>
     * @param backOffPeriodSupplier {@link java.util.function.Supplier} <p>The back off period supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     */
    public void backOffPeriodSupplier(Supplier<Long> backOffPeriodSupplier) {
		Assert.notNull(backOffPeriodSupplier, "'backOffPeriodSupplier' cannot be null");
		this.backOffPeriod = backOffPeriodSupplier;
	}

    /**
     * <code>getBackOffPeriod</code>
     * <p>The get back off period getter method.</p>
     * @return  long <p>The get back off period return object is <code>long</code> type.</p>
     */
    public long getBackOffPeriod() {
		return this.backOffPeriod.get();
	}

	protected void doBackOff() throws BackOffInterruptedException {
		try {
			sleeper.sleep(this.backOffPeriod.get());
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
		}
	}

	public String toString() {
		return "FixedBackOffPolicy[backOffPeriod=" + this.backOffPeriod.get() + "]";
	}

}
