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

package org.springframework.retry.interceptor;

/**
 * <code>FixedKeyGenerator</code>
 * <p>The fixed key generator class.</p>
 * @see  org.springframework.retry.interceptor.MethodArgumentsKeyGenerator
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class FixedKeyGenerator implements MethodArgumentsKeyGenerator {

	private final String label;

    /**
     * <code>FixedKeyGenerator</code>
     * <p>Instantiates a new fixed key generator.</p>
     * @param label {@link java.lang.String} <p>The label parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public FixedKeyGenerator(String label) {
		this.label = label;
	}

	@Override
	public Object getKey(Object[] item) {
		return this.label;
	}

}
