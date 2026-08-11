/*
 * Copyright 2006-2025 the original author or authors.
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

package org.springframework.retry.backoff;

import org.springframework.retry.RetryException;

/**
 * <code>BackOffInterruptedException</code>
 * <p>The back off interrupted exception class.</p>
 * @see  org.springframework.retry.RetryException
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class BackOffInterruptedException extends RetryException {

    /**
     * <code>BackOffInterruptedException</code>
     * <p>Instantiates a new back off interrupted exception.</p>
     * @param msg {@link java.lang.String} <p>The msg parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public BackOffInterruptedException(String msg) {
		super(msg);
	}

    /**
     * <code>BackOffInterruptedException</code>
     * <p>Instantiates a new back off interrupted exception.</p>
     * @param msg {@link java.lang.String} <p>The msg parameter is <code>String</code> type.</p>
     * @param cause {@link java.lang.Throwable} <p>The cause parameter is <code>Throwable</code> type.</p>
     * @see  java.lang.String
     * @see  java.lang.Throwable
     */
    public BackOffInterruptedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
