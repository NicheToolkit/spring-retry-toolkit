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

package org.springframework.retry.backoff;

import java.util.Random;
import java.util.function.Supplier;

import org.springframework.retry.RetryContext;

/**
 * <code>ExponentialRandomBackOffPolicy</code>
 * <p>The exponential random back off policy class.</p>
 * @see  org.springframework.retry.backoff.ExponentialBackOffPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ExponentialRandomBackOffPolicy extends ExponentialBackOffPolicy {

	public BackOffContext start(RetryContext context) {
		return new ExponentialRandomBackOffContext(getInitialInterval(), getMultiplier(), getMaxInterval(),
				getInitialIntervalSupplier(), getMultiplierSupplier(), getMaxIntervalSupplier());
	}

	protected ExponentialBackOffPolicy newInstance() {
		return new ExponentialRandomBackOffPolicy();
	}

    /**
     * <code>ExponentialRandomBackOffContext</code>
     * <p>The exponential random back off context class.</p>
     * @see  org.springframework.retry.backoff.ExponentialBackOffPolicy.ExponentialBackOffContext
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class ExponentialRandomBackOffContext extends ExponentialBackOffContext {

		private final Random r = new Random();

        /**
         * <code>ExponentialRandomBackOffContext</code>
         * <p>Instantiates a new exponential random back off context.</p>
         * @param expSeed long <p>The exp seed parameter is <code>long</code> type.</p>
         * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
         * @param maxInterval long <p>The max interval parameter is <code>long</code> type.</p>
         * @param expSeedSupplier {@link java.util.function.Supplier} <p>The exp seed supplier parameter is <code>Supplier</code> type.</p>
         * @param multiplierSupplier {@link java.util.function.Supplier} <p>The multiplier supplier parameter is <code>Supplier</code> type.</p>
         * @param maxIntervalSupplier {@link java.util.function.Supplier} <p>The max interval supplier parameter is <code>Supplier</code> type.</p>
         * @see  java.util.function.Supplier
         */
        public ExponentialRandomBackOffContext(long expSeed, double multiplier, long maxInterval,
				Supplier<Long> expSeedSupplier, Supplier<Double> multiplierSupplier,
				Supplier<Long> maxIntervalSupplier) {

			super(expSeed, multiplier, maxInterval, expSeedSupplier, multiplierSupplier, maxIntervalSupplier);
		}

		@Override
		public synchronized long getSleepAndIncrement() {
			long next = super.getSleepAndIncrement();
			next = (long) (next * (1 + r.nextFloat() * (getMultiplier() - 1)));
			if (next > super.getMaxInterval()) {
				next = super.getMaxInterval();
			}
			return next;
		}

	}

}
