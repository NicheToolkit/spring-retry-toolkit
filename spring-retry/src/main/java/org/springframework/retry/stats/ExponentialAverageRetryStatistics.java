/*
 * Copyright 2012-2015 the original author or authors.
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

package org.springframework.retry.stats;

/**
 * <code>ExponentialAverageRetryStatistics</code>
 * <p>The exponential average retry statistics class.</p>
 * @see  org.springframework.retry.stats.DefaultRetryStatistics
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ExponentialAverageRetryStatistics extends DefaultRetryStatistics {

	private long window = 15000;

	private ExponentialAverage started;

	private ExponentialAverage error;

	private ExponentialAverage complete;

	private ExponentialAverage recovery;

	private ExponentialAverage abort;

    /**
     * <code>ExponentialAverageRetryStatistics</code>
     * <p>Instantiates a new exponential average retry statistics.</p>
     * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public ExponentialAverageRetryStatistics(String name) {
		super(name);
		init();
	}

	private void init() {
		started = new ExponentialAverage(window);
		error = new ExponentialAverage(window);
		complete = new ExponentialAverage(window);
		abort = new ExponentialAverage(window);
		recovery = new ExponentialAverage(window);
	}

    /**
     * <code>setWindow</code>
     * <p>The set window setter method.</p>
     * @param window long <p>The window parameter is <code>long</code> type.</p>
     */
    public void setWindow(long window) {
		this.window = window;
		init();
	}

    /**
     * <code>getRollingStartedCount</code>
     * <p>The get rolling started count getter method.</p>
     * @return  int <p>The get rolling started count return object is <code>int</code> type.</p>
     */
    public int getRollingStartedCount() {
		return (int) Math.round(started.getValue());
	}

    /**
     * <code>getRollingErrorCount</code>
     * <p>The get rolling error count getter method.</p>
     * @return  int <p>The get rolling error count return object is <code>int</code> type.</p>
     */
    public int getRollingErrorCount() {
		return (int) Math.round(error.getValue());
	}

    /**
     * <code>getRollingAbortCount</code>
     * <p>The get rolling abort count getter method.</p>
     * @return  int <p>The get rolling abort count return object is <code>int</code> type.</p>
     */
    public int getRollingAbortCount() {
		return (int) Math.round(abort.getValue());
	}

    /**
     * <code>getRollingRecoveryCount</code>
     * <p>The get rolling recovery count getter method.</p>
     * @return  int <p>The get rolling recovery count return object is <code>int</code> type.</p>
     */
    public int getRollingRecoveryCount() {
		return (int) Math.round(recovery.getValue());
	}

    /**
     * <code>getRollingCompleteCount</code>
     * <p>The get rolling complete count getter method.</p>
     * @return  int <p>The get rolling complete count return object is <code>int</code> type.</p>
     */
    public int getRollingCompleteCount() {
		return (int) Math.round(complete.getValue());
	}

    /**
     * <code>getRollingErrorRate</code>
     * <p>The get rolling error rate getter method.</p>
     * @return  double <p>The get rolling error rate return object is <code>double</code> type.</p>
     */
    public double getRollingErrorRate() {
		if (Math.round(started.getValue()) == 0) {
			return 0.;
		}
		return (abort.getValue() + recovery.getValue()) / started.getValue();
	}

	@Override
	public void incrementStartedCount() {
		super.incrementStartedCount();
		started.increment();
	}

	@Override
	public void incrementCompleteCount() {
		super.incrementCompleteCount();
		complete.increment();
	}

	@Override
	public void incrementRecoveryCount() {
		super.incrementRecoveryCount();
		recovery.increment();
	}

	@Override
	public void incrementErrorCount() {
		super.incrementErrorCount();
		error.increment();
	}

	@Override
	public void incrementAbortCount() {
		super.incrementAbortCount();
		abort.increment();
	}

	private class ExponentialAverage {

		private final double alpha;

		private volatile long lastTime = System.currentTimeMillis();

		private volatile double value = 0;

        /**
         * <code>ExponentialAverage</code>
         * <p>Instantiates a new exponential average.</p>
         * @param window long <p>The window parameter is <code>long</code> type.</p>
         */
        public ExponentialAverage(long window) {
			alpha = 1. / window;
		}

        /**
         * <code>increment</code>
         * <p>The increment method.</p>
         */
        public synchronized void increment() {
			long time = System.currentTimeMillis();
			value = value * Math.exp(-alpha * (time - lastTime)) + 1;
			lastTime = time;
		}

        /**
         * <code>getValue</code>
         * <p>The get value getter method.</p>
         * @return  double <p>The get value return object is <code>double</code> type.</p>
         */
        public double getValue() {
			long time = System.currentTimeMillis();
			return value * Math.exp(-alpha * (time - lastTime));
		}

	}

}
