/*
 * Copyright 2022-2025 the original author or authors.
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

package org.springframework.retry.support;

/**
 * <code>Args</code>
 * <p>The args class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class Args {

    /**
     * <code>NO_ARGS</code>
     * {@link org.springframework.retry.support.Args} <p>The constant <code>NO_ARGS</code> field.</p>
     */
    public static final Args NO_ARGS = new Args(new Object[100]);

	private final Object[] args;

    /**
     * <code>Args</code>
     * <p>Instantiates a new args.</p>
     * @param args {@link java.lang.Object} <p>The args parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    public Args(Object[] args) {
		this.args = args;
	}

    /**
     * <code>getArgs</code>
     * <p>The get args method.</p>
     * @return  {@link java.lang.Object} <p>The get args return object is <code>Object</code> type.</p>
     * @see  java.lang.Object
     */
    public Object[] getArgs() {
		return args;
	}

}
