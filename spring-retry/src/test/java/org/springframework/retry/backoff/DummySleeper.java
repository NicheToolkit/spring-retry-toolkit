/*
 * Copyright 2006-2025 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

/**
 * <code>DummySleeper</code>
 * <p>The dummy sleeper class.</p>
 * @see  org.springframework.retry.backoff.Sleeper
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class DummySleeper implements Sleeper {

	private final List<Long> backOffs = new ArrayList<>();

    /**
     * <code>getLastBackOff</code>
     * <p>The get last back off getter method.</p>
     * @return  long <p>The get last back off return object is <code>long</code> type.</p>
     */
    public long getLastBackOff() {
		return backOffs.get(backOffs.size() - 1);
	}

    /**
     * <code>getBackOffs</code>
     * <p>The get back offs method.</p>
     * @return  long <p>The get back offs return object is <code>long</code> type.</p>
     */
    public long[] getBackOffs() {
		long[] result = new long[backOffs.size()];
		int i = 0;
		for (Long value : backOffs) {
			result[i++] = value;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.retry.backoff.Sleeper#sleep(long)
	 */
	public void sleep(long backOffPeriod) throws InterruptedException {
		this.backOffs.add(backOffPeriod);
	}

}
