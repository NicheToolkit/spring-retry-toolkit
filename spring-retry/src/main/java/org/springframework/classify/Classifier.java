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

import java.io.Serializable;

/**
 * <code>Classifier</code>
 * <p>The classifier interface.</p>
 * @param <C>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @see  java.io.Serializable
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface Classifier<C, T> extends Serializable {

    /**
     * <code>classify</code>
     * <p>The classify method.</p>
     * @param classifiable C <p>The classifiable parameter is <code>C</code> type.</p>
     * @return  T <p>The classify return object is <code>T</code> type.</p>
     */
    T classify(C classifiable);

}
