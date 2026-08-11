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

import java.util.HashMap;
import java.util.Map;

/**
 * <code>PatternMatchingClassifier</code>
 * <p>The pattern matching classifier class.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  org.springframework.classify.Classifier
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class PatternMatchingClassifier<T> implements Classifier<String, T> {

	private PatternMatcher<T> values;

    /**
     * <code>PatternMatchingClassifier</code>
     * <p>Instantiates a new pattern matching classifier.</p>
     */
    public PatternMatchingClassifier() {
		this(new HashMap<>());
	}

    /**
     * <code>PatternMatchingClassifier</code>
     * <p>Instantiates a new pattern matching classifier.</p>
     * @param values {@link java.util.Map} <p>The values parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public PatternMatchingClassifier(Map<String, T> values) {
		super();
		this.values = new PatternMatcher<>(values);
	}

    /**
     * <code>setPatternMap</code>
     * <p>The set pattern map setter method.</p>
     * @param values {@link java.util.Map} <p>The values parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public void setPatternMap(Map<String, T> values) {
		this.values = new PatternMatcher<>(values);
	}

	@Override
	public T classify(String classifiable) {
		T value = this.values.match(classifiable);
		return value;
	}

}
