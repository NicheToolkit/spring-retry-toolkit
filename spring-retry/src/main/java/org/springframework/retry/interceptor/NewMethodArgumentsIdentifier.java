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

package org.springframework.retry.interceptor;

/**
 * <code>NewMethodArgumentsIdentifier</code>
 * <p>The new method arguments identifier interface.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface NewMethodArgumentsIdentifier {

    /**
     * <code>isNew</code>
     * <p>The is new method.</p>
     * @param args {@link java.lang.Object} <p>The args parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @return  boolean <p>The is new return object is <code>boolean</code> type.</p>
     */
    boolean isNew(Object[] args);

}
