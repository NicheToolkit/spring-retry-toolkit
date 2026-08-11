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

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetrySimulation;
import org.springframework.retry.support.RetrySimulator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>ExponentialRandomBackOffPolicyTests</code>
 * <p>The exponential random back off policy tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class ExponentialRandomBackOffPolicyTests {

    /**
     * <code>NUM_TRIALS</code>
     * <p>The <code>NUM_TRIALS</code> field.</p>
     */
    static final int NUM_TRIALS = 10000;
    /**
     * <code>MAX_RETRIES</code>
     * <p>The <code>MAX_RETRIES</code> field.</p>
     */
    static final int MAX_RETRIES = 6;

	private ExponentialBackOffPolicy makeBackoffPolicy() {
		ExponentialBackOffPolicy policy = new ExponentialRandomBackOffPolicy();
		policy.setInitialInterval(50);
		policy.setMultiplier(2.0);
		policy.setMaxInterval(3000);
		return policy;
	}

	private SimpleRetryPolicy makeRetryPolicy() {
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(MAX_RETRIES);
		return retryPolicy;
	}

    /**
     * <code>testSingleBackoff</code>
     * <p>The test single backoff method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSingleBackoff() {
		ExponentialBackOffPolicy backOffPolicy = makeBackoffPolicy();
		RetrySimulator simulator = new RetrySimulator(backOffPolicy, makeRetryPolicy());
		RetrySimulation simulation = simulator.executeSimulation(1);

		List<Long> sleeps = simulation.getLongestTotalSleepSequence().getSleeps();
		System.out.println("Single trial of " + backOffPolicy + ": sleeps=" + sleeps);
		assertThat(sleeps).hasSize(MAX_RETRIES - 1);
		long initialInterval = backOffPolicy.getInitialInterval();
		for (int i = 0; i < sleeps.size(); i++) {
			long expectedMaxValue = 2 * (long) (initialInterval
					+ initialInterval * Math.max(1, Math.pow(backOffPolicy.getMultiplier(), i)));
			assertThat(sleeps.get(i))
				.describedAs("Found a sleep [%d] which exceeds our max expected value of %d at interval %d",
						sleeps.get(i), expectedMaxValue, i)
				.isLessThan(expectedMaxValue);
		}
	}

    /**
     * <code>testMaxInterval</code>
     * <p>The test max interval method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testMaxInterval() {
		ExponentialBackOffPolicy backOffPolicy = makeBackoffPolicy();
		backOffPolicy.setInitialInterval(3000);
		long maxInterval = backOffPolicy.getMaxInterval();

		RetrySimulator simulator = new RetrySimulator(backOffPolicy, makeRetryPolicy());
		RetrySimulation simulation = simulator.executeSimulation(1);

		List<Long> sleeps = simulation.getLongestTotalSleepSequence().getSleeps();
		System.out.println("Single trial of " + backOffPolicy + ": sleeps=" + sleeps);
		assertThat(sleeps).hasSize(MAX_RETRIES - 1);
		long initialInterval = backOffPolicy.getInitialInterval();
		for (int i = 0; i < sleeps.size(); i++) {
			long expectedMaxValue = 2 * (long) (initialInterval
					+ initialInterval * Math.max(1, Math.pow(backOffPolicy.getMultiplier(), i)));
			assertThat(sleeps.get(i))
				.describedAs("Found a sleep [%d] which exceeds our max expected value of %d at interval %d",
						sleeps.get(i), expectedMaxValue, i)
				.isLessThanOrEqualTo(expectedMaxValue);
		}
	}

    /**
     * <code>testMultiBackOff</code>
     * <p>The test multi back off method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testMultiBackOff() {
		ExponentialBackOffPolicy backOffPolicy = makeBackoffPolicy();
		RetrySimulator simulator = new RetrySimulator(backOffPolicy, makeRetryPolicy());
		RetrySimulation simulation = simulator.executeSimulation(NUM_TRIALS);

		System.out.println("Ran " + NUM_TRIALS + " backoff trials.  Each trial retried " + MAX_RETRIES + " times");
		System.out.println("Policy: " + backOffPolicy);
		System.out.println("All generated backoffs:");
		System.out.println("    " + simulation.getPercentiles());

		System.out.println("Backoff frequencies:");
		System.out.print("    " + simulation.getPercentiles());

	}

}
