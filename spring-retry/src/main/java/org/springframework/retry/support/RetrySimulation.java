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
import java.util.Collections;
import java.util.List;

/**
 * <code>RetrySimulation</code>
 * <p>The retry simulation class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetrySimulation {

	private final List<SleepSequence> sleepSequences = new ArrayList<>();

	private final List<Long> sleepHistogram = new ArrayList<>();

    /**
     * <code>RetrySimulation</code>
     * <p>Instantiates a new retry simulation.</p>
     */
    public RetrySimulation() {
	}

    /**
     * <code>addSequence</code>
     * <p>The add sequence method.</p>
     * @param sleeps {@link java.util.List} <p>The sleeps parameter is <code>List</code> type.</p>
     * @see  java.util.List
     */
    public void addSequence(List<Long> sleeps) {
		sleepHistogram.addAll(sleeps);
		sleepSequences.add(new SleepSequence(sleeps));
	}

    /**
     * <code>getPercentiles</code>
     * <p>The get percentiles getter method.</p>
     * @return  {@link java.util.List} <p>The get percentiles return object is <code>List</code> type.</p>
     * @see  java.util.List
     */
    public List<Double> getPercentiles() {
		List<Double> res = new ArrayList<>();
		for (double percentile : new double[] { 10, 20, 30, 40, 50, 60, 70, 80, 90 }) {
			res.add(getPercentile(percentile / 100));
		}
		return res;
	}

    /**
     * <code>getPercentile</code>
     * <p>The get percentile getter method.</p>
     * @param p double <p>The p parameter is <code>double</code> type.</p>
     * @return  double <p>The get percentile return object is <code>double</code> type.</p>
     */
    public double getPercentile(double p) {
		Collections.sort(sleepHistogram);
		int size = sleepHistogram.size();
		double pos = p * (size - 1);
		int i0 = (int) pos;
		int i1 = i0 + 1;
		double weight = pos - i0;
		return sleepHistogram.get(i0) * (1 - weight) + sleepHistogram.get(i1) * weight;

	}

    /**
     * <code>getLongestTotalSleepSequence</code>
     * <p>The get longest total sleep sequence getter method.</p>
     * @return  {@link org.springframework.retry.support.RetrySimulation.SleepSequence} <p>The get longest total sleep sequence return object is <code>SleepSequence</code> type.</p>
     * @see  org.springframework.retry.support.RetrySimulation.SleepSequence
     */
    public SleepSequence getLongestTotalSleepSequence() {
		SleepSequence longest = null;
		for (SleepSequence sequence : sleepSequences) {
			if (longest == null || sequence.getTotalSleep() > longest.getTotalSleep()) {
				longest = sequence;
			}
		}
		return longest;
	}

    /**
     * <code>SleepSequence</code>
     * <p>The sleep sequence class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class SleepSequence {

		private final List<Long> sleeps;

		private final long longestSleep;

		private final long totalSleep;

        /**
         * <code>SleepSequence</code>
         * <p>Instantiates a new sleep sequence.</p>
         * @param sleeps {@link java.util.List} <p>The sleeps parameter is <code>List</code> type.</p>
         * @see  java.util.List
         */
        public SleepSequence(List<Long> sleeps) {
			this.sleeps = sleeps;
			this.longestSleep = Collections.max(sleeps);
			long totalSleep = 0;
			for (Long sleep : sleeps) {
				totalSleep += sleep;
			}
			this.totalSleep = totalSleep;
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

        /**
         * <code>getLongestSleep</code>
         * <p>The get longest sleep getter method.</p>
         * @return  long <p>The get longest sleep return object is <code>long</code> type.</p>
         */
        public long getLongestSleep() {
			return longestSleep;
		}

        /**
         * <code>getTotalSleep</code>
         * <p>The get total sleep getter method.</p>
         * @return  long <p>The get total sleep return object is <code>long</code> type.</p>
         */
        public long getTotalSleep() {
			return totalSleep;
		}

		public String toString() {
			return "totalSleep=" + totalSleep + ": " + sleeps.toString();
		}

	}

}
