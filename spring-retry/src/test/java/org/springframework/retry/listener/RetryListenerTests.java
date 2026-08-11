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

package org.springframework.retry.listener;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.TerminatedRetryException;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * <code>RetryListenerTests</code>
 * <p>The retry listener tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetryListenerTests {

    /**
     * <code>template</code>
     * {@link org.springframework.retry.support.RetryTemplate} <p>The <code>template</code> field.</p>
     * @see  org.springframework.retry.support.RetryTemplate
     */
    RetryTemplate template = new RetryTemplate();

    /**
     * <code>count</code>
     * <p>The <code>count</code> field.</p>
     */
    int count = 0;

    /**
     * <code>list</code>
     * {@link java.util.List} <p>The <code>list</code> field.</p>
     * @see  java.util.List
     */
    List<String> list = new ArrayList<>();

    /**
     * <code>testClose</code>
     * <p>The test close method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClose() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThatNoException().isThrownBy(() -> retryListener.close(null, null, null));
	}

    /**
     * <code>noExceptionOnError</code>
     * <p>The no exception on error method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void noExceptionOnError() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThatNoException().isThrownBy(() -> retryListener.onError(null, null, null));
	}

    /**
     * <code>testOpen</code>
     * <p>The test open method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOpen() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThat(retryListener.open(null, null)).isTrue();
	}

    /**
     * <code>testOpenDefaultImplementation</code>
     * <p>The test open default implementation method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOpenDefaultImplementation() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThat(retryListener.open(null, null)).isTrue();
	}

    /**
     * <code>testCloseDefaultImplementation</code>
     * <p>The test close default implementation method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCloseDefaultImplementation() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThatNoException().isThrownBy(() -> retryListener.close(null, null, null));
	}

    /**
     * <code>testOnSuccessDefaultImplementation</code>
     * <p>The test on success default implementation method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOnSuccessDefaultImplementation() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThatNoException().isThrownBy(() -> retryListener.onError(null, null, null));
	}

    /**
     * <code>testOnErrorDefaultImplementation</code>
     * <p>The test on error default implementation method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOnErrorDefaultImplementation() {
		RetryListener retryListener = new RetryListener() {
		};
		assertThatNoException().isThrownBy(() -> retryListener.onError(null, null, null));
	}

    /**
     * <code>testOpenInterceptors</code>
     * <p>The test open interceptors method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOpenInterceptors() {
		template.setListeners(new RetryListener[] { new RetryListener() {
			public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
				count++;
				list.add("1:" + count);
				return true;
			}
		}, new RetryListener() {
			public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
				count++;
				list.add("2:" + count);
				return true;
			}
		} });
		template.execute(context -> null);
		assertThat(count).isEqualTo(2);
		assertThat(list).hasSize(2);
		assertThat(list.get(0)).isEqualTo("1:1");
	}

    /**
     * <code>testOpenCanVetoRetry</code>
     * <p>The test open can veto retry method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOpenCanVetoRetry() {
		template.registerListener(new RetryListener() {
			public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
				list.add("1");
				return false;
			}
		});
		assertThatExceptionOfType(TerminatedRetryException.class).isThrownBy(() -> template.execute(context -> {
			count++;
			return null;
		}));
		assertThat(count).isEqualTo(0);
		assertThat(list).hasSize(1);
		assertThat(list.get(0)).isEqualTo("1");
	}

    /**
     * <code>testCloseInterceptors</code>
     * <p>The test close interceptors method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCloseInterceptors() {
		template.setListeners(new RetryListener[] { new RetryListener() {
			public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
					Throwable t) {
				count++;
				list.add("1:" + count);
			}
		}, new RetryListener() {
			public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
					Throwable t) {
				count++;
				list.add("2:" + count);
			}
		} });
		template.execute(context -> null);
		assertThat(count).isEqualTo(2);
		assertThat(list).hasSize(2);
		// interceptors are called in reverse order on close...
		assertThat(list.get(0)).isEqualTo("2:1");
	}

    /**
     * <code>testOnError</code>
     * <p>The test on error method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testOnError() {
		template.setRetryPolicy(new NeverRetryPolicy());
		template.setListeners(new RetryListener[] { new RetryListener() {
			public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
					Throwable throwable) {
				list.add("1");
			}
		}, new RetryListener() {
			public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
					Throwable throwable) {
				list.add("2");
			}
		} });
		assertThatIllegalStateException().isThrownBy(() -> template.execute(context -> {
			count++;
			throw new IllegalStateException("foo");
		})).withMessage("foo");
		// never retry so callback is executed once
		assertThat(count).isEqualTo(1);
		assertThat(list).hasSize(2);
		// interceptors are called in reverse order on error...
		assertThat(list.get(0)).isEqualTo("2");

	}

    /**
     * <code>testCloseInterceptorsAfterRetry</code>
     * <p>The test close interceptors after retry method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCloseInterceptorsAfterRetry() {
		template.registerListener(new RetryListener() {
			public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
					Throwable t) {
				list.add("" + count);
				// The last attempt should have been successful:
				assertThat(t).isNull();
			}
		});
		template.execute(context -> {
			if (count++ < 1)
				throw new RuntimeException("Retry!");
			return null;
		});
		assertThat(count).isEqualTo(2);
		// The close interceptor was only called once:
		assertThat(list).hasSize(1);
		// We succeeded on the second try:
		assertThat(list.get(0)).isEqualTo("2");
	}

}
