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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.retry.RetryContext;

/**
 * <code>AbstractMapRetryContextCache</code>
 * <p>The abstract map retry context cache class.</p>
 * @param <V>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  org.springframework.retry.policy.RetryContextCache
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public abstract class AbstractMapRetryContextCache<V> implements RetryContextCache {

    /**
     * <code>DEFAULT_CAPACITY</code>
     * <p>The constant <code>DEFAULT_CAPACITY</code> field.</p>
     */
    public static final int DEFAULT_CAPACITY = 4096;

	private static final Log logger = LogFactory.getLog(AbstractMapRetryContextCache.class);

	private final Map<Object, V> map;

	private final boolean failIfFull;

	private int capacity;

    /**
     * <code>AbstractMapRetryContextCache</code>
     * <p>Instantiates a new abstract map retry context cache.</p>
     * @param capacity int <p>The capacity parameter is <code>int</code> type.</p>
     * @param removeEldestEntries boolean <p>The remove eldest entries parameter is <code>boolean</code> type.</p>
     */
    protected AbstractMapRetryContextCache(int capacity, boolean removeEldestEntries) {
		this.capacity = capacity;
		this.map = Collections
			.synchronizedMap(removeEldestEntries ? new LinkedHashMap<Object, V>(capacity, 0.75f, true) {
				@Override
				protected boolean removeEldestEntry(Map.Entry<Object, V> eldest) {
					boolean evict = size() > AbstractMapRetryContextCache.this.capacity;
					if (evict && logger.isWarnEnabled()) {
						logger.warn("Retry cache capacity limit breached. "
								+ "Do you need to re-consider the implementation of the key generator, "
								+ "or the equals and hashCode of the items that failed?");
					}
					return evict;
				}
			} : new HashMap<>());
		this.failIfFull = !removeEldestEntries;
	}

    /**
     * <code>getMap</code>
     * <p>The get map getter method.</p>
     * @return  {@link java.util.Map} <p>The get map return object is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    protected final Map<Object, V> getMap() {
		return this.map;
	}

    /**
     * <code>setCapacity</code>
     * <p>The set capacity setter method.</p>
     * @param capacity int <p>The capacity parameter is <code>int</code> type.</p>
     */
    protected void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	@Override
	public RetryContext get(Object key) {
		V value = this.map.get(key);
		return (value != null) ? fromValue(value) : null;
	}

	@Override
	public void put(Object key, RetryContext context) {
		if (this.failIfFull && !this.map.containsKey(key) && this.map.size() >= this.capacity) {
			throw new RetryCacheCapacityExceededException("Cache capacity limit breached. "
					+ "Do you need to re-consider the implementation of the key generator, "
					+ "or the equals and hashCode of the items that failed?");
		}
		this.map.put(key, toValue(context));
	}

	@Override
	public void remove(Object key) {
		this.map.remove(key);
	}

    /**
     * <code>toValue</code>
     * <p>The to value method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @return  V <p>The to value return object is <code>V</code> type.</p>
     */
    protected abstract V toValue(RetryContext context);

    /**
     * <code>fromValue</code>
     * <p>The from value method.</p>
     * @param value V <p>The value parameter is <code>V</code> type.</p>
     * @return  {@link org.springframework.retry.RetryContext} <p>The from value return object is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     */
    protected abstract RetryContext fromValue(V value);

}
