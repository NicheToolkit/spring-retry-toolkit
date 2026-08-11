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

import java.util.Map;

/**
 * <code>BackToBackPatternClassifier</code>
 * <p>The back to back pattern classifier class.</p>
 * @param <C>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  org.springframework.classify.Classifier
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class BackToBackPatternClassifier<C, T> implements Classifier<C, T> {

	private Classifier<C, String> router;

	private Classifier<String, T> matcher;

    /**
     * <code>BackToBackPatternClassifier</code>
     * <p>Instantiates a new back to back pattern classifier.</p>
     */
    public BackToBackPatternClassifier() {
	}

    /**
     * <code>BackToBackPatternClassifier</code>
     * <p>Instantiates a new back to back pattern classifier.</p>
     * @param router {@link org.springframework.classify.Classifier} <p>The router parameter is <code>Classifier</code> type.</p>
     * @param matcher {@link org.springframework.classify.Classifier} <p>The matcher parameter is <code>Classifier</code> type.</p>
     * @see  org.springframework.classify.Classifier
     */
    public BackToBackPatternClassifier(Classifier<C, String> router, Classifier<String, T> matcher) {
		super();
		this.router = router;
		this.matcher = matcher;
	}

    /**
     * <code>setMatcherMap</code>
     * <p>The set matcher map setter method.</p>
     * @param map {@link java.util.Map} <p>The map parameter is <code>Map</code> type.</p>
     * @see  java.util.Map
     */
    public void setMatcherMap(Map<String, T> map) {
		this.matcher = new PatternMatchingClassifier<>(map);
	}

    /**
     * <code>setRouterDelegate</code>
     * <p>The set router delegate setter method.</p>
     * @param delegate {@link java.lang.Object} <p>The delegate parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    public void setRouterDelegate(Object delegate) {
		this.router = new ClassifierAdapter<>(delegate);
	}

	@Override
	public T classify(C classifiable) {
		return this.matcher.classify(this.router.classify(classifiable));
	}

}
