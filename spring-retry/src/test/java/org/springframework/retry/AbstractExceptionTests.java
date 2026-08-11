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

package org.springframework.retry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>AbstractExceptionTests</code>
 * <p>The abstract exception tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public abstract class AbstractExceptionTests {

    /**
     * <code>testExceptionString</code>
     * <p>The test exception string method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Exception
     * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
     */
    @Test
	public void testExceptionString() throws Exception {
		Exception exception = getException("foo");
		assertThat(exception.getMessage()).isEqualTo("foo");
	}

    /**
     * <code>testExceptionStringThrowable</code>
     * <p>The test exception string throwable method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Exception
     * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
     */
    @Test
	public void testExceptionStringThrowable() throws Exception {
		Exception exception = getException("foo", new IllegalStateException());
		assertThat(exception.getMessage().substring(0, 3)).isEqualTo("foo");
	}

    /**
     * <code>getException</code>
     * <p>The get exception getter method.</p>
     * @param msg {@link java.lang.String} <p>The msg parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  java.lang.Exception
     * @return  {@link java.lang.Exception} <p>The get exception return object is <code>Exception</code> type.</p>
     */
    public abstract Exception getException(String msg);

    /**
     * <code>getException</code>
     * <p>The get exception getter method.</p>
     * @param msg {@link java.lang.String} <p>The msg parameter is <code>String</code> type.</p>
     * @param t {@link java.lang.Throwable} <p>The t parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.String
     * @see  java.lang.Throwable
     * @see  java.lang.Exception
     * @return  {@link java.lang.Exception} <p>The get exception return object is <code>Exception</code> type.</p>
     */
    public abstract Exception getException(String msg, Throwable t);

}
