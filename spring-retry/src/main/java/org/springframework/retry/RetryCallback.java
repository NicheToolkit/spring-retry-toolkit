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

package org.springframework.retry;

/**
 * <code>RetryCallback</code>
 * <p>The retry callback interface.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <E>  {@link java.lang.Throwable} <p>The generic parameter is <code>Throwable</code> type.</p>
 * @see  java.lang.Throwable
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RetryCallback<T, E extends Throwable> {

    /**
     * <code>doWithRetry</code>
     * <p>The do with retry method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  E
     * @return  T <p>The do with retry return object is <code>T</code> type.</p>
     * @throws E E <p>The e is <code>E</code> type.</p>
     */
    T doWithRetry(RetryContext context) throws E;

    /**
     * <code>getLabel</code>
     * <p>The get label getter method.</p>
     * @return  {@link java.lang.String} <p>The get label return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    default String getLabel() {
		return null;
	}

}
