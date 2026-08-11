/*
 * Copyright 2006-2026 the original author or authors.
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
package org.springframework.retry.policy;

import java.lang.ref.SoftReference;
import java.util.Map;

import org.springframework.retry.RetryContext;

/**
 * <code>SoftReferenceMapRetryContextCache</code>
 * <p>The soft reference map retry context cache class.</p>
 * @see  org.springframework.retry.policy.AbstractMapRetryContextCache
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class SoftReferenceMapRetryContextCache extends AbstractMapRetryContextCache<SoftReference<RetryContext>> {

    /**
     * <code>SoftReferenceMapRetryContextCache</code>
     * <p>Instantiates a new soft reference map retry context cache.</p>
     */
    public SoftReferenceMapRetryContextCache() {
		this(DEFAULT_CAPACITY);
	}

    /**
     * <code>SoftReferenceMapRetryContextCache</code>
     * <p>Instantiates a new soft reference map retry context cache.</p>
     * @param capacity int <p>The capacity parameter is <code>int</code> type.</p>
     */
    public SoftReferenceMapRetryContextCache(int capacity) {
		this(capacity, true);
	}

    /**
     * <code>SoftReferenceMapRetryContextCache</code>
     * <p>Instantiates a new soft reference map retry context cache.</p>
     * @param capacity int <p>The capacity parameter is <code>int</code> type.</p>
     * @param removeEldestEntries boolean <p>The remove eldest entries parameter is <code>boolean</code> type.</p>
     */
    public SoftReferenceMapRetryContextCache(int capacity, boolean removeEldestEntries) {
		super(capacity, removeEldestEntries);
	}

	@Override
	public void setCapacity(int capacity) {
		super.setCapacity(capacity);
	}

	public boolean containsKey(Object key) {
		Map<Object, SoftReference<RetryContext>> map = getMap();
		if (!map.containsKey(key)) {
			return false;
		}
		if (map.get(key).get() == null) {
			// our reference was garbage collected
			map.remove(key);
		}
		return map.containsKey(key);
	}

	@Override
	protected SoftReference<RetryContext> toValue(RetryContext context) {
		return new SoftReference<>(context);
	}

	@Override
	protected RetryContext fromValue(SoftReference<RetryContext> value) {
		return value.get();
	}

}
