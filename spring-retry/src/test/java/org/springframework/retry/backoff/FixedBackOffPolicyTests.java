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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>FixedBackOffPolicyTests</code>
 * <p>The fixed back off policy tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class FixedBackOffPolicyTests {

	private final DummySleeper sleeper = new DummySleeper();

    /**
     * <code>testSetBackoffPeriodNegative</code>
     * <p>The test set backoff period negative method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSetBackoffPeriodNegative() {
		FixedBackOffPolicy strategy = new FixedBackOffPolicy();
		strategy.setBackOffPeriod(-1000L);
		strategy.setSleeper(sleeper);
		strategy.backOff(null);
		// We should see a zero backoff if we try to set it negative
		assertThat(sleeper.getBackOffs().length).isEqualTo(1);
		assertThat(sleeper.getLastBackOff()).isEqualTo(1);
	}

    /**
     * <code>testSingleBackOff</code>
     * <p>The test single back off method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSingleBackOff() {
		int backOffPeriod = 50;
		FixedBackOffPolicy strategy = new FixedBackOffPolicy();
		strategy.setBackOffPeriod(backOffPeriod);
		strategy.setSleeper(sleeper);
		strategy.backOff(null);
		assertThat(sleeper.getBackOffs().length).isEqualTo(1);
		assertThat(sleeper.getLastBackOff()).isEqualTo(backOffPeriod);
	}

    /**
     * <code>testManyBackOffCalls</code>
     * <p>The test many back off calls method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testManyBackOffCalls() {
		int backOffPeriod = 50;
		FixedBackOffPolicy strategy = new FixedBackOffPolicy();
		strategy.setBackOffPeriod(backOffPeriod);
		strategy.setSleeper(sleeper);
		for (int x = 0; x < 10; x++) {
			strategy.backOff(null);
			assertThat(sleeper.getLastBackOff()).isEqualTo(backOffPeriod);
		}
		assertThat(sleeper.getBackOffs().length).isEqualTo(10);
	}

    /**
     * <code>testInterruptedStatusIsRestored</code>
     * <p>The test interrupted status is restored method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testInterruptedStatusIsRestored() {
		int backOffPeriod = 50;
		FixedBackOffPolicy strategy = new FixedBackOffPolicy();
		strategy.setBackOffPeriod(backOffPeriod);
		strategy.setSleeper(new Sleeper() {
			@Override
			public void sleep(long backOffPeriod) throws InterruptedException {
				throw new InterruptedException("foo");
			}
		});
		assertThatExceptionOfType(BackOffInterruptedException.class).isThrownBy(() -> strategy.backOff(null));
		assertThat(Thread.interrupted()).isTrue();
	}

}
