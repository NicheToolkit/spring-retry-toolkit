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
 * <code>RecoveryCallback</code>
 * <p>The recovery callback interface.</p>
 * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface RecoveryCallback<T> {

    /**
     * <code>recover</code>
     * <p>The recover method.</p>
     * @param context {@link org.springframework.retry.RetryContext} <p>The context parameter is <code>RetryContext</code> type.</p>
     * @see  org.springframework.retry.RetryContext
     * @see  java.lang.Exception
     * @return  T <p>The recover return object is <code>T</code> type.</p>
     * @throws Exception {@link java.lang.Exception} <p>The exception is <code>Exception</code> type.</p>
     */
    T recover(RetryContext context) throws Exception;

}
