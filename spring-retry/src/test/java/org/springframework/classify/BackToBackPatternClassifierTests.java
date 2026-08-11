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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.classify.annotation.Classifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>BackToBackPatternClassifierTests</code>
 * <p>The back to back pattern classifier tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class BackToBackPatternClassifierTests {

	private BackToBackPatternClassifier<String, String> classifier = new BackToBackPatternClassifier<>();

	private Map<String, String> map;

    /**
     * <code>createMap</code>
     * <p>The create map method.</p>
     * @see  org.junit.jupiter.api.BeforeEach
     */
    @BeforeEach
	public void createMap() {
		map = new HashMap<>();
		map.put("foo", "bar");
		map.put("*", "spam");
	}

    /**
     * <code>testNoClassifiers</code>
     * <p>The test no classifiers method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testNoClassifiers() {
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> classifier.classify("foo"));
	}

    /**
     * <code>testCreateFromConstructor</code>
     * <p>The test create from constructor method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCreateFromConstructor() {
		classifier = new BackToBackPatternClassifier<>(
				new PatternMatchingClassifier<>(Collections.singletonMap("oof", "bucket")),
				new PatternMatchingClassifier<>(map));
		assertThat(classifier.classify("oof")).isEqualTo("spam");
	}

    /**
     * <code>testSetRouterDelegate</code>
     * <p>The test set router delegate method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSetRouterDelegate() {
		classifier.setRouterDelegate(new Object() {
			@Classifier
			public String convert(String value) {
				return "bucket";
			}
		});
		classifier.setMatcherMap(map);
		assertThat(classifier.classify("oof")).isEqualTo("spam");
	}

    /**
     * <code>testSingleMethodWithNoAnnotation</code>
     * <p>The test single method with no annotation method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testSingleMethodWithNoAnnotation() {
		classifier = new BackToBackPatternClassifier<>();
		classifier.setRouterDelegate(new RouterDelegate());
		classifier.setMatcherMap(map);
		assertThat(classifier.classify("oof")).isEqualTo("spam");
	}

	@SuppressWarnings("serial")
	private class RouterDelegate implements org.springframework.classify.Classifier<Object, String> {

		@Override
		public String classify(Object classifiable) {
			return "bucket";
		}

	}

}
