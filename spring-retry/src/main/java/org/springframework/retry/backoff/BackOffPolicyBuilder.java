/*
 * Copyright 2022-2022 the original author or authors.
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

/**
 * <code>BackOffPolicyBuilder</code>
 * <p>The back off policy builder class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class BackOffPolicyBuilder {

	private static final long DEFAULT_INITIAL_DELAY = 1000L;

	private Long delay = DEFAULT_INITIAL_DELAY;

	private Long maxDelay;

	private Double multiplier;

	private Boolean random;

	private Sleeper sleeper;

	private Supplier<Long> delaySupplier;

	private Supplier<Long> maxDelaySupplier;

	private Supplier<Double> multiplierSupplier;

	private Supplier<Boolean> randomSupplier;

	private BackOffPolicyBuilder() {
	}

    /**
     * <code>newBuilder</code>
     * <p>The new builder method.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The new builder return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public static BackOffPolicyBuilder newBuilder() {
		return new BackOffPolicyBuilder();
	}

    /**
     * <code>newDefaultPolicy</code>
     * <p>The new default policy method.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicy} <p>The new default policy return object is <code>BackOffPolicy</code> type.</p>
     */
    public static BackOffPolicy newDefaultPolicy() {
		return new BackOffPolicyBuilder().build();
	}

    /**
     * <code>delay</code>
     * <p>The delay method.</p>
     * @param delay long <p>The delay parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The delay return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder delay(long delay) {
		this.delay = delay;
		return this;
	}

    /**
     * <code>maxDelay</code>
     * <p>The max delay method.</p>
     * @param maxDelay long <p>The max delay parameter is <code>long</code> type.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The max delay return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder maxDelay(long maxDelay) {
		this.maxDelay = maxDelay;
		return this;
	}

    /**
     * <code>multiplier</code>
     * <p>The multiplier method.</p>
     * @param multiplier double <p>The multiplier parameter is <code>double</code> type.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The multiplier return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder multiplier(double multiplier) {
		this.multiplier = multiplier;
		return this;
	}

    /**
     * <code>random</code>
     * <p>The random method.</p>
     * @param random boolean <p>The random parameter is <code>boolean</code> type.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The random return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder random(boolean random) {
		this.random = random;
		return this;
	}

    /**
     * <code>sleeper</code>
     * <p>The sleeper method.</p>
     * @param sleeper {@link org.springframework.retry.backoff.Sleeper} <p>The sleeper parameter is <code>Sleeper</code> type.</p>
     * @see  org.springframework.retry.backoff.Sleeper
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The sleeper return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder sleeper(Sleeper sleeper) {
		this.sleeper = sleeper;
		return this;
	}

    /**
     * <code>delaySupplier</code>
     * <p>The delay supplier method.</p>
     * @param delaySupplier {@link java.util.function.Supplier} <p>The delay supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The delay supplier return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder delaySupplier(Supplier<Long> delaySupplier) {
		this.delaySupplier = delaySupplier;
		return this;
	}

    /**
     * <code>maxDelaySupplier</code>
     * <p>The max delay supplier method.</p>
     * @param maxDelaySupplier {@link java.util.function.Supplier} <p>The max delay supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The max delay supplier return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder maxDelaySupplier(Supplier<Long> maxDelaySupplier) {
		this.maxDelaySupplier = maxDelaySupplier;
		return this;
	}

    /**
     * <code>multiplierSupplier</code>
     * <p>The multiplier supplier method.</p>
     * @param multiplierSupplier {@link java.util.function.Supplier} <p>The multiplier supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The multiplier supplier return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder multiplierSupplier(Supplier<Double> multiplierSupplier) {
		this.multiplierSupplier = multiplierSupplier;
		return this;
	}

    /**
     * <code>randomSupplier</code>
     * <p>The random supplier method.</p>
     * @param randomSupplier {@link java.util.function.Supplier} <p>The random supplier parameter is <code>Supplier</code> type.</p>
     * @see  java.util.function.Supplier
     * @return  {@link org.springframework.retry.backoff.BackOffPolicyBuilder} <p>The random supplier return object is <code>BackOffPolicyBuilder</code> type.</p>
     */
    public BackOffPolicyBuilder randomSupplier(Supplier<Boolean> randomSupplier) {
		this.randomSupplier = randomSupplier;
		return this;
	}

    /**
     * <code>build</code>
     * <p>The build method.</p>
     * @return  {@link org.springframework.retry.backoff.BackOffPolicy} <p>The build return object is <code>BackOffPolicy</code> type.</p>
     */
    public BackOffPolicy build() {
		if (this.multiplier != null && this.multiplier > 0 || this.multiplierSupplier != null) {
			ExponentialBackOffPolicy policy;
			if (isRandom()) {
				policy = new ExponentialRandomBackOffPolicy();
			}
			else {
				policy = new ExponentialBackOffPolicy();
			}
			if (this.delay != null) {
				policy.setInitialInterval(this.delay);
			}
			if (this.delaySupplier != null) {
				policy.initialIntervalSupplier(this.delaySupplier);
			}
			if (this.multiplier != null) {
				policy.setMultiplier(this.multiplier);
			}
			if (this.multiplierSupplier != null) {
				policy.multiplierSupplier(this.multiplierSupplier);
			}
			if (this.maxDelay != null && this.delay != null) {
				policy.setMaxInterval(
						this.maxDelay > this.delay ? this.maxDelay : ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL);
			}
			if (this.maxDelaySupplier != null) {
				policy.maxIntervalSupplier(this.maxDelaySupplier);
			}
			if (this.sleeper != null) {
				policy.setSleeper(this.sleeper);
			}
			return policy;
		}
		if (this.maxDelay != null && this.delay != null && this.maxDelay > this.delay) {
			UniformRandomBackOffPolicy policy = new UniformRandomBackOffPolicy();
			if (this.delay != null) {
				policy.setMinBackOffPeriod(this.delay);
			}
			if (this.delaySupplier != null) {
				policy.minBackOffPeriodSupplier(this.delaySupplier);
			}
			if (this.maxDelay != null) {
				policy.setMaxBackOffPeriod(this.maxDelay);
			}
			if (this.maxDelaySupplier != null) {
				policy.maxBackOffPeriodSupplier(this.maxDelaySupplier);
			}
			if (this.sleeper != null) {
				policy.setSleeper(this.sleeper);
			}
			return policy;
		}
		FixedBackOffPolicy policy = new FixedBackOffPolicy();
		if (this.delaySupplier != null) {
			policy.backOffPeriodSupplier(this.delaySupplier);
		}
		else if (this.delay != null) {
			policy.setBackOffPeriod(this.delay);
		}
		if (this.sleeper != null) {
			policy.setSleeper(this.sleeper);
		}
		return policy;
	}

	private boolean isRandom() {
		return (this.randomSupplier != null && Boolean.TRUE.equals(this.randomSupplier.get()))
				|| Boolean.TRUE.equals(this.random);
	}

}
