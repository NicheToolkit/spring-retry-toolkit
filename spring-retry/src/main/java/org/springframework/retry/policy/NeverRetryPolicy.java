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

package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * <code>NeverRetryPolicy</code>
 * <p>The never retry policy class.</p>
 * @see  org.springframework.retry.RetryPolicy
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class NeverRetryPolicy implements RetryPolicy {

	public boolean canRetry(RetryContext context) {
		return !((NeverRetryContext) context).isFinished();
	}

	public void close(RetryContext context) {
		// no-op
	}

	public RetryContext open(RetryContext parent) {
		return new NeverRetryContext(parent);
	}

	public void registerThrowable(RetryContext context, Throwable throwable) {
		((NeverRetryContext) context).setFinished();
		((RetryContextSupport) context).registerThrowable(throwable);
	}

	private static class NeverRetryContext extends RetryContextSupport {

		private boolean finished = false;

        /**
         * <code>NeverRetryContext</code>
         * <p>Instantiates a new never retry context.</p>
         * @param parent {@link org.springframework.retry.RetryContext} <p>The parent parameter is <code>RetryContext</code> type.</p>
         * @see  org.springframework.retry.RetryContext
         */
        public NeverRetryContext(RetryContext parent) {
			super(parent);
		}

        /**
         * <code>isFinished</code>
         * <p>The is finished method.</p>
         * @return  boolean <p>The is finished return object is <code>boolean</code> type.</p>
         */
        public boolean isFinished() {
			return finished;
		}

        /**
         * <code>setFinished</code>
         * <p>The set finished setter method.</p>
         */
        public void setFinished() {
			this.finished = true;
		}

	}

}
