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
 * <code>UniformRandomBackOffPolicyTests</code>
 * <p>The uniform random back off policy tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class UniformRandomBackOffPolicyTests {

    /**
     * <code>testSetSleeper</code>
     * <p>The test set sleeper method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSetSleeper() {
		UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
		int minBackOff = 1000;
		int maxBackOff = 10000;
		backOffPolicy.setMinBackOffPeriod(minBackOff);
		backOffPolicy.setMaxBackOffPeriod(maxBackOff);

		DummySleeper dummySleeper = new DummySleeper();
		UniformRandomBackOffPolicy withSleeper = backOffPolicy.withSleeper(dummySleeper);

		assertThat(withSleeper.getMinBackOffPeriod()).isEqualTo(minBackOff);
		assertThat(withSleeper.getMaxBackOffPeriod()).isEqualTo(maxBackOff);

		assertThat(dummySleeper.getBackOffs()).isEmpty();
		withSleeper.backOff(null);

		assertThat(dummySleeper.getBackOffs()).hasSize(1);
		assertThat(dummySleeper.getBackOffs()[0]).isLessThan(maxBackOff);
	}

    /**
     * <code>testInterruptedStatusIsRestored</code>
     * <p>The test interrupted status is restored method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testInterruptedStatusIsRestored() {
		UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
		int minBackOff = 1000;
		int maxBackOff = 10000;
		backOffPolicy.setMinBackOffPeriod(minBackOff);
		backOffPolicy.setMaxBackOffPeriod(maxBackOff);
		UniformRandomBackOffPolicy withSleeper = backOffPolicy.withSleeper(new Sleeper() {
			@Override
			public void sleep(long backOffPeriod) throws InterruptedException {
				throw new InterruptedException("foo");
			}
		});

		assertThatExceptionOfType(BackOffInterruptedException.class).isThrownBy(() -> withSleeper.backOff(null));
		assertThat(Thread.interrupted()).isTrue();
	}

    /**
     * <code>testMaxBackOffLessThanMinBackOff</code>
     * <p>The test max back off less than min back off method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testMaxBackOffLessThanMinBackOff() {
		UniformRandomBackOffPolicy backOffPolicy = new UniformRandomBackOffPolicy();
		int minBackOff = 1000;
		int maxBackOff = 10;
		backOffPolicy.setMinBackOffPeriod(minBackOff);
		backOffPolicy.setMaxBackOffPeriod(maxBackOff);

		DummySleeper dummySleeper = new DummySleeper();
		UniformRandomBackOffPolicy withSleeper = backOffPolicy.withSleeper(dummySleeper);
		assertThat(dummySleeper.getBackOffs()).isEmpty();
		withSleeper.backOff(null);
		assertThat(dummySleeper.getBackOffs()).hasSize(1);
		assertThat(dummySleeper.getBackOffs()[0]).isEqualTo(minBackOff);
	}

}
