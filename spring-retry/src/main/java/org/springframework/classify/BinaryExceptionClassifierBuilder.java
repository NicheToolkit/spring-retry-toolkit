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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

/**
 * <code>BinaryExceptionClassifierBuilder</code>
 * <p>The binary exception classifier builder class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class BinaryExceptionClassifierBuilder {

	private Boolean isWhiteList = null;

	private boolean traverseCauses = false;

	private final List<Class<? extends Throwable>> exceptionClasses = new ArrayList<>();

    /**
     * <code>retryOn</code>
     * <p>The retry on method.</p>
     * @param throwable {@link java.lang.Class} <p>The throwable parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @return  {@link org.springframework.classify.BinaryExceptionClassifierBuilder} <p>The retry on return object is <code>BinaryExceptionClassifierBuilder</code> type.</p>
     */
    public BinaryExceptionClassifierBuilder retryOn(Class<? extends Throwable> throwable) {
		Assert.isTrue(isWhiteList == null || isWhiteList, "Please use only retryOn() or only notRetryOn()");
		Assert.notNull(throwable, "Exception class can not be null");
		isWhiteList = true;
		exceptionClasses.add(throwable);
		return this;

	}

    /**
     * <code>notRetryOn</code>
     * <p>The not retry on method.</p>
     * @param throwable {@link java.lang.Class} <p>The throwable parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @return  {@link org.springframework.classify.BinaryExceptionClassifierBuilder} <p>The not retry on return object is <code>BinaryExceptionClassifierBuilder</code> type.</p>
     */
    public BinaryExceptionClassifierBuilder notRetryOn(Class<? extends Throwable> throwable) {
		Assert.isTrue(isWhiteList == null || !isWhiteList, "Please use only retryOn() or only notRetryOn()");
		Assert.notNull(throwable, "Exception class can not be null");
		isWhiteList = false;
		exceptionClasses.add(throwable);
		return this;
	}

    /**
     * <code>traversingCauses</code>
     * <p>The traversing causes method.</p>
     * @return  {@link org.springframework.classify.BinaryExceptionClassifierBuilder} <p>The traversing causes return object is <code>BinaryExceptionClassifierBuilder</code> type.</p>
     */
    public BinaryExceptionClassifierBuilder traversingCauses() {
		this.traverseCauses = true;
		return this;
	}

    /**
     * <code>build</code>
     * <p>The build method.</p>
     * @return  {@link org.springframework.classify.BinaryExceptionClassifier} <p>The build return object is <code>BinaryExceptionClassifier</code> type.</p>
     */
    public BinaryExceptionClassifier build() {
		Assert.isTrue(!exceptionClasses.isEmpty(),
				"Attempt to build classifier with empty rules. To build always true, or always false "
						+ "instance, please use explicit rule for Throwable");
		BinaryExceptionClassifier classifier = new BinaryExceptionClassifier(exceptionClasses, isWhiteList // using
																											// white
																											// list
																											// means
																											// classifying
																											// provided
																											// classes
																											// as
																											// "true"
																											// (is
																											// retryable)
		);
		classifier.setTraverseCauses(traverseCauses);
		return classifier;
	}

}
