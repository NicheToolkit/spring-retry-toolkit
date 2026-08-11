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

package org.springframework.retry.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.Sleeper;
import org.springframework.retry.backoff.SleepingBackOffPolicy;

/**
 * <code>RetrySimulator</code>
 * <p>The retry simulator class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetrySimulator {

	private final SleepingBackOffPolicy<?> backOffPolicy;

	private final RetryPolicy retryPolicy;

    /**
     * <code>RetrySimulator</code>
     * <p>Instantiates a new retry simulator.</p>
     * @param backOffPolicy {@link org.springframework.retry.backoff.SleepingBackOffPolicy} <p>The back off policy parameter is <code>SleepingBackOffPolicy</code> type.</p>
     * @param retryPolicy {@link org.springframework.retry.RetryPolicy} <p>The retry policy parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.backoff.SleepingBackOffPolicy
     * @see  org.springframework.retry.RetryPolicy
     */
    public RetrySimulator(SleepingBackOffPolicy<?> backOffPolicy, RetryPolicy retryPolicy) {
		this.backOffPolicy = backOffPolicy;
		this.retryPolicy = retryPolicy;
	}

    /**
     * <code>executeSimulation</code>
     * <p>The execute simulation method.</p>
     * @param numSimulations int <p>The num simulations parameter is <code>int</code> type.</p>
     * @return  {@link org.springframework.retry.support.RetrySimulation} <p>The execute simulation return object is <code>RetrySimulation</code> type.</p>
     * @see  org.springframework.retry.support.RetrySimulation
     */
    public RetrySimulation executeSimulation(int numSimulations) {
		RetrySimulation simulation = new RetrySimulation();

		for (int i = 0; i < numSimulations; i++) {
			simulation.addSequence(executeSingleSimulation());
		}
		return simulation;
	}

    /**
     * <code>executeSingleSimulation</code>
     * <p>The execute single simulation method.</p>
     * @return  {@link java.util.List} <p>The execute single simulation return object is <code>List</code> type.</p>
     * @see  java.util.List
     */
    public List<Long> executeSingleSimulation() {
		StealingSleeper stealingSleeper = new StealingSleeper();
		SleepingBackOffPolicy<?> stealingBackoff = backOffPolicy.withSleeper(stealingSleeper);

		RetryTemplate template = new RetryTemplate();
		template.setBackOffPolicy(stealingBackoff);
		template.setRetryPolicy(retryPolicy);

		try {
			template.execute(new FailingRetryCallback());
		}
		catch (FailingRetryException e) {

		}
		catch (Throwable e) {
			throw new RuntimeException("Unexpected exception", e);
		}

		return stealingSleeper.getSleeps();
	}

    /**
     * <code>FailingRetryCallback</code>
     * <p>The failing retry callback class.</p>
     * @see  org.springframework.retry.RetryCallback
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class FailingRetryCallback implements RetryCallback<Object, Exception> {

		public Object doWithRetry(RetryContext context) throws Exception {
			throw new FailingRetryException();
		}

	}

    /**
     * <code>FailingRetryException</code>
     * <p>The failing retry exception class.</p>
     * @see  java.lang.Exception
     * @see  java.lang.SuppressWarnings
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @SuppressWarnings("serial")
	static class FailingRetryException extends Exception {

	}

    /**
     * <code>StealingSleeper</code>
     * <p>The stealing sleeper class.</p>
     * @see  org.springframework.retry.backoff.Sleeper
     * @see  java.lang.SuppressWarnings
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @SuppressWarnings("serial")
	static class StealingSleeper implements Sleeper {

		private final List<Long> sleeps = new ArrayList<>();

		public void sleep(long backOffPeriod) throws InterruptedException {
			sleeps.add(backOffPeriod);
		}

        /**
         * <code>getSleeps</code>
         * <p>The get sleeps getter method.</p>
         * @return  {@link java.util.List} <p>The get sleeps return object is <code>List</code> type.</p>
         * @see  java.util.List
         */
        public List<Long> getSleeps() {
			return sleeps;
		}

	}

}
