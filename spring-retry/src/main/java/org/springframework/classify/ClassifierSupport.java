/*
 * Copyright 2006-2007 the original author or authors.
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

/**
 * <code>ClassifierSupport</code>
 * <p>The classifier support class.</p>
 * @param <C>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ClassifierSupport<C, T> implements Classifier<C, T> {

	final private T defaultValue;

    /**
     * <code>ClassifierSupport</code>
     * <p>Instantiates a new classifier support.</p>
     * @param defaultValue T <p>The default value parameter is <code>T</code> type.</p>
     */
    public ClassifierSupport(T defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}

	@Override
	public T classify(C throwable) {
		return this.defaultValue;
	}

}
