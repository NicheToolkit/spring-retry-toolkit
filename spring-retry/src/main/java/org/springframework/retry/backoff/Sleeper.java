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
package org.springframework.retry.backoff;

import java.io.Serializable;

/**
 * <code>Sleeper</code>
 * <p>The sleeper interface.</p>
 * @see  java.io.Serializable
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public interface Sleeper extends Serializable {

    /**
     * <code>sleep</code>
     * <p>The sleep method.</p>
     * @param backOffPeriod long <p>The back off period parameter is <code>long</code> type.</p>
     * @throws InterruptedException {@link java.lang.InterruptedException} <p>The interrupted exception is <code>InterruptedException</code> type.</p>
     * @see  java.lang.InterruptedException
     */
    void sleep(long backOffPeriod) throws InterruptedException;

}
