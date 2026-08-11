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

import org.springframework.classify.util.MethodInvoker;
import org.springframework.classify.util.MethodInvokerUtils;
import org.springframework.util.Assert;

/**
 * <code>ClassifierAdapter</code>
 * <p>The classifier adapter class.</p>
 * @param <C>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ClassifierAdapter<C, T> implements Classifier<C, T> {

	private MethodInvoker invoker;

	private Classifier<C, T> classifier;

    /**
     * <code>ClassifierAdapter</code>
     * <p>Instantiates a new classifier adapter.</p>
     */
    public ClassifierAdapter() {
		super();
	}

    /**
     * <code>ClassifierAdapter</code>
     * <p>Instantiates a new classifier adapter.</p>
     * @param delegate {@link java.lang.Object} <p>The delegate parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    public ClassifierAdapter(Object delegate) {
		setDelegate(delegate);
	}

    /**
     * <code>ClassifierAdapter</code>
     * <p>Instantiates a new classifier adapter.</p>
     * @param delegate {@link org.springframework.classify.Classifier} <p>The delegate parameter is <code>Classifier</code> type.</p>
     */
    public ClassifierAdapter(Classifier<C, T> delegate) {
		this.classifier = delegate;
	}

    /**
     * <code>setDelegate</code>
     * <p>The set delegate setter method.</p>
     * @param delegate {@link org.springframework.classify.Classifier} <p>The delegate parameter is <code>Classifier</code> type.</p>
     */
    public void setDelegate(Classifier<C, T> delegate) {
		this.classifier = delegate;
		this.invoker = null;
	}

    /**
     * <code>setDelegate</code>
     * <p>The set delegate setter method.</p>
     * @param delegate {@link java.lang.Object} <p>The delegate parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    public final void setDelegate(Object delegate) {
		this.classifier = null;
		this.invoker = MethodInvokerUtils
			.getMethodInvokerByAnnotation(org.springframework.classify.annotation.Classifier.class, delegate);
		if (this.invoker == null) {
			this.invoker = MethodInvokerUtils.<C, T>getMethodInvokerForSingleArgument(delegate);
		}
		Assert.state(this.invoker != null, "No single argument public method with or without "
				+ "@Classifier was found in delegate of type " + delegate.getClass());
	}

	@Override
	@SuppressWarnings("unchecked")
	public T classify(C classifiable) {
		if (this.classifier != null) {
			return this.classifier.classify(classifiable);
		}
		return (T) this.invoker.invokeMethod(classifiable);
	}

}
