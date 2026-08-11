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

import org.springframework.retry.RetryContext;

/**
 * <code>StatelessBackOffPolicy</code>
 * <p>The stateless back off policy class.</p>
 * @see  org.springframework.retry.backoff.BackOffPolicy
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public abstract class StatelessBackOffPolicy implements BackOffPolicy {

	@Override
	public final void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
		doBackOff();
	}

	@Override
	public BackOffContext start(RetryContext status) {
		return null;
	}

    /**
     * <code>doBackOff</code>
     * <p>The do back off method.</p>
     * @throws BackOffInterruptedException {@link org.springframework.retry.backoff.BackOffInterruptedException} <p>The back off interrupted exception is <code>BackOffInterruptedException</code> type.</p>
     * @see  org.springframework.retry.backoff.BackOffInterruptedException
     */
    protected abstract void doBackOff() throws BackOffInterruptedException;

}
