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
package org.springframework.classify;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <code>SubclassClassifier</code>
 * <p>The subclass classifier class.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <C>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  org.springframework.classify.Classifier
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class SubclassClassifier<T, C> implements Classifier<T, C> {

	private ConcurrentMap<Class<? extends T>, C> classified;

	private C defaultValue;

    /**
     * <code>SubclassClassifier</code>
     * <p>Instantiates a new subclass classifier.</p>
     */
    public SubclassClassifier() {
		this(null);
	}

    /**
     * <code>SubclassClassifier</code>
     * <p>Instantiates a new subclass classifier.</p>
     * @param defaultValue C <p>The default value parameter is <code>C</code> type.</p>
     */
    public SubclassClassifier(C defaultValue) {
		this(new HashMap<>(), defaultValue);
	}

    /**
     * <code>SubclassClassifier</code>
     * <p>Instantiates a new subclass classifier.</p>
     * @param typeMap {@link java.util.Map} <p>The type map parameter is <code>Map</code> type.</p>
     * @param defaultValue C <p>The default value parameter is <code>C</code> type.</p>
     * @see  java.util.Map
     */
    public SubclassClassifier(Map<Class<? extends T>, C> typeMap, C defaultValue) {
		super();
		this.classified = new ConcurrentHashMap<>(typeMap);
		this.defaultValue = defaultValue;
	}

    /**
     * <code>setDefaultValue</code>
     * <p>The set default value setter method.</p>
     * @param defaultValue C <p>The default value parameter is <code>C</code> type.</p>
     */
    public void setDefaultValue(C defaultValue) {
		this.defaultValue = defaultValue;
	}

    /**
     * <code>setTypeMap</code>
     * <p>The set type map setter method.</p>
     * @param map {@link java.util.Map} <p>The map parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public void setTypeMap(Map<Class<? extends T>, C> map) {
		this.classified = new ConcurrentHashMap<>(map);
	}

    /**
     * <code>add</code>
     * <p>The add method.</p>
     * @param type {@link java.lang.Class} <p>The type parameter is <code>Class</code> type.</p>
     * @param target C <p>The target parameter is <code>C</code> type.</p>
     * @see  java.lang.Class
     */
    public void add(Class<? extends T> type, C target) {
		this.classified.put(type, target);
	}

	@Override
	public C classify(T classifiable) {
		if (classifiable == null) {
			return this.defaultValue;
		}

		@SuppressWarnings("unchecked")
		Class<? extends T> exceptionClass = (Class<? extends T>) classifiable.getClass();
		if (this.classified.containsKey(exceptionClass)) {
			return this.classified.get(exceptionClass);
		}

		// check for subclasses
		C value = null;
		for (Class<?> cls = exceptionClass.getSuperclass(); !cls.equals(Object.class)
				&& value == null; cls = cls.getSuperclass()) {

			value = this.classified.get(cls);
		}

		// check for interfaces subclasses
		if (value == null) {
			for (Class<?> cls = exceptionClass; !cls.equals(Object.class) && value == null; cls = cls.getSuperclass()) {
				for (Class<?> ifc : cls.getInterfaces()) {
					value = this.classified.get(ifc);
					if (value != null) {
						break;
					}
				}
			}
		}

		// ConcurrentHashMap doesn't allow nulls
		if (value != null) {
			this.classified.put(exceptionClass, value);
		}

		if (value == null) {
			value = this.defaultValue;
		}

		return value;
	}

    /**
     * <code>getDefault</code>
     * <p>The get default getter method.</p>
     * @return  C <p>The get default return object is <code>C</code> type.</p>
     */
    final public C getDefault() {
		return this.defaultValue;
	}

    /**
     * <code>getClassified</code>
     * <p>The get classified getter method.</p>
     * @return  {@link java.util.Map} <p>The get classified return object is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    protected Map<Class<? extends T>, C> getClassified() {
		return this.classified;
	}

}
