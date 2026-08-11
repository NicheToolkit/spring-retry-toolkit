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
package org.springframework.classify;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>BinaryExceptionClassifier</code>
 * <p>The binary exception classifier class.</p>
 * @see  org.springframework.classify.SubclassClassifier
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class BinaryExceptionClassifier extends SubclassClassifier<Throwable, Boolean> {

	private boolean traverseCauses;

    /**
     * <code>builder</code>
     * <p>The builder method.</p>
     * @return  {@link org.springframework.classify.BinaryExceptionClassifierBuilder} <p>The builder return object is <code>BinaryExceptionClassifierBuilder</code> type.</p>
     * @see  org.springframework.classify.BinaryExceptionClassifierBuilder
     */
    public static BinaryExceptionClassifierBuilder builder() {
		return new BinaryExceptionClassifierBuilder();
	}

    /**
     * <code>defaultClassifier</code>
     * <p>The default classifier method.</p>
     * @return  {@link org.springframework.classify.BinaryExceptionClassifier} <p>The default classifier return object is <code>BinaryExceptionClassifier</code> type.</p>
     */
    public static BinaryExceptionClassifier defaultClassifier() {
		// create new instance for each call due to mutability
		return new BinaryExceptionClassifier(
				Collections.<Class<? extends Throwable>, Boolean>singletonMap(Exception.class, true), false);
	}

    /**
     * <code>BinaryExceptionClassifier</code>
     * <p>Instantiates a new binary exception classifier.</p>
     * @param defaultValue boolean <p>The default value parameter is <code>boolean</code> type.</p>
     */
    public BinaryExceptionClassifier(boolean defaultValue) {
		super(defaultValue);
	}

    /**
     * <code>BinaryExceptionClassifier</code>
     * <p>Instantiates a new binary exception classifier.</p>
     * @param exceptionClasses {@link java.util.Collection} <p>The exception classes parameter is <code>Collection</code> type.</p>
     * @param value boolean <p>The value parameter is <code>boolean</code> type.</p>
     * @see  java.util.Collection
     */
    public BinaryExceptionClassifier(Collection<Class<? extends Throwable>> exceptionClasses, boolean value) {
		this(!value);
		if (exceptionClasses != null) {
			Map<Class<? extends Throwable>, Boolean> map = new HashMap<>();
			for (Class<? extends Throwable> type : exceptionClasses) {
				map.put(type, !getDefault());
			}
			setTypeMap(map);
		}
	}

    /**
     * <code>BinaryExceptionClassifier</code>
     * <p>Instantiates a new binary exception classifier.</p>
     * @param exceptionClasses {@link java.util.Collection} <p>The exception classes parameter is <code>Collection</code> type.</p>
     * @see  java.util.Collection
     */
    public BinaryExceptionClassifier(Collection<Class<? extends Throwable>> exceptionClasses) {
		this(exceptionClasses, true);
	}

    /**
     * <code>BinaryExceptionClassifier</code>
     * <p>Instantiates a new binary exception classifier.</p>
     * @param typeMap {@link java.util.Map} <p>The type map parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public BinaryExceptionClassifier(Map<Class<? extends Throwable>, Boolean> typeMap) {
		this(typeMap, false);
	}

    /**
     * <code>BinaryExceptionClassifier</code>
     * <p>Instantiates a new binary exception classifier.</p>
     * @param typeMap {@link java.util.Map} <p>The type map parameter is <code>Map</code> type.</p>
     * @param defaultValue boolean <p>The default value parameter is <code>boolean</code> type.</p>
     * @see  java.util.Map
     */
    public BinaryExceptionClassifier(Map<Class<? extends Throwable>, Boolean> typeMap, boolean defaultValue) {
		super(typeMap, defaultValue);
	}

    /**
     * <code>BinaryExceptionClassifier</code>
     * <p>Instantiates a new binary exception classifier.</p>
     * @param typeMap {@link java.util.Map} <p>The type map parameter is <code>Map</code> type.</p>
     * @param defaultValue boolean <p>The default value parameter is <code>boolean</code> type.</p>
     * @param traverseCauses boolean <p>The traverse causes parameter is <code>boolean</code> type.</p>
     * @see  java.util.Map
     */
    public BinaryExceptionClassifier(Map<Class<? extends Throwable>, Boolean> typeMap, boolean defaultValue,
			boolean traverseCauses) {
		super(typeMap, defaultValue);
		this.traverseCauses = traverseCauses;
	}

    /**
     * <code>setTraverseCauses</code>
     * <p>The set traverse causes setter method.</p>
     * @param traverseCauses boolean <p>The traverse causes parameter is <code>boolean</code> type.</p>
     */
    public void setTraverseCauses(boolean traverseCauses) {
		this.traverseCauses = traverseCauses;
	}

	@Override
	public Boolean classify(Throwable classifiable) {
		Boolean classified = super.classify(classifiable);
		if (!this.traverseCauses) {
			return classified;
		}

		/*
		 * If the result is the default, we need to find out if it was by default or so
		 * configured; if default, try the cause(es).
		 */
		if (classified.equals(this.getDefault())) {
			Throwable cause = classifiable;
			do {
				if (this.getClassified().containsKey(cause.getClass())) {
					return classified; // non-default classification
				}
				cause = cause.getCause();
				classified = super.classify(cause);
			}
			while (cause != null && classified.equals(this.getDefault()));
		}

		return classified;
	}

}
