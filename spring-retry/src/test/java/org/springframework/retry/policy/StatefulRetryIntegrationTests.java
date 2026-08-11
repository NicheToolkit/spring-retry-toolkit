/*
 * Copyright 2006-2026 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryState;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>StatefulRetryIntegrationTests</code>
 * <p>The stateful retry integration tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class StatefulRetryIntegrationTests {

    /**
     * <code>testExternalRetryWithFailAndNoRetry</code>
     * <p>The test external retry with fail and no retry method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testExternalRetryWithFailAndNoRetry() throws Throwable {
		MockRetryCallback callback = new MockRetryCallback();

		RetryState retryState = new DefaultRetryState("foo");

		RetryTemplate retryTemplate = new RetryTemplate();
		MapRetryContextCache cache = new MapRetryContextCache();
		retryTemplate.setRetryContextCache(cache);
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(1));

		assertThat(cache.containsKey("foo")).isFalse();

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> retryTemplate.execute(callback, retryState))
			.withMessage(null);

		assertThat(cache.containsKey("foo")).isTrue();

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> retryTemplate.execute(callback, retryState))
			.withMessageContaining("exhausted");

		assertThat(cache.containsKey("foo")).isFalse();

		// Callback is called once: the recovery path should be called in
		// handleRetryExhausted (so not in this test)...
		assertThat(callback.attempts).isEqualTo(1);
	}

    /**
     * <code>testExternalRetryWithSuccessOnRetry</code>
     * <p>The test external retry with success on retry method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testExternalRetryWithSuccessOnRetry() throws Throwable {
		MockRetryCallback callback = new MockRetryCallback();

		RetryState retryState = new DefaultRetryState("foo");

		RetryTemplate retryTemplate = new RetryTemplate();
		MapRetryContextCache cache = new MapRetryContextCache();
		retryTemplate.setRetryContextCache(cache);
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2));

		assertThat(cache.containsKey("foo")).isFalse();

		Object result = "start_foo";
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> retryTemplate.execute(callback, retryState))
			.withMessage(null);

		assertThat(cache.containsKey("foo")).isTrue();

		result = retryTemplate.execute(callback, retryState);

		assertThat(cache.containsKey("foo")).isFalse();

		assertThat(callback.attempts).isEqualTo(2);
		assertThat(callback.context.getRetryCount()).isEqualTo(1);
		assertThat(result).isEqualTo("bar");
	}

    /**
     * <code>testExternalRetryWithSuccessOnRetryAndSerializedContext</code>
     * <p>The test external retry with success on retry and serialized context method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testExternalRetryWithSuccessOnRetryAndSerializedContext() throws Throwable {
		MockRetryCallback callback = new MockRetryCallback();

		RetryState retryState = new DefaultRetryState("foo");

		RetryTemplate retryTemplate = new RetryTemplate();
		RetryContextCache cache = new SerializedMapRetryContextCache();
		retryTemplate.setRetryContextCache(cache);
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2));

		assertThat(cache.containsKey("foo")).isFalse();

		Object result = "start_foo";
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> retryTemplate.execute(callback, retryState))
			.withMessage(null);

		assertThat(cache.containsKey("foo")).isTrue();

		result = retryTemplate.execute(callback, retryState);

		assertThat(cache.containsKey("foo")).isFalse();

		assertThat(callback.attempts).isEqualTo(2);
		assertThat(callback.context.getRetryCount()).isEqualTo(1);
		assertThat(result).isEqualTo("bar");
	}

    /**
     * <code>testExponentialBackOffIsExponential</code>
     * <p>The test exponential back off is exponential method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testExponentialBackOffIsExponential() {
		ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
		policy.setInitialInterval(100);
		policy.setMultiplier(1.5);
		RetryTemplate template = new RetryTemplate();
		template.setBackOffPolicy(policy);
		final List<Long> times = new ArrayList<>();
		RetryState retryState = new DefaultRetryState("bar");
		for (int i = 0; i < 3; i++) {
			try {
				template.execute(context -> {
					times.add(System.currentTimeMillis());
					throw new Exception("Fail");
				}, context -> null, retryState);
			}
			catch (Exception e) {
				assertThat(e.getMessage().equals("Fail")).isTrue();
			}
		}
		assertThat(times).hasSize(3);
		assertThat(times.get(1) - times.get(0) >= 100).isTrue();
		assertThat(times.get(2) - times.get(1) >= 150).isTrue();
	}

    /**
     * <code>testExternalRetryWithFailAndNoRetryWhenKeyIsNull</code>
     * <p>The test external retry with fail and no retry when key is null method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.Throwable
     * @throws Throwable {@link java.lang.Throwable} <p>The throwable is <code>Throwable</code> type.</p>
     */
    @Test
	public void testExternalRetryWithFailAndNoRetryWhenKeyIsNull() throws Throwable {
		MockRetryCallback callback = new MockRetryCallback();

		RetryState retryState = new DefaultRetryState(null);

		RetryTemplate retryTemplate = new RetryTemplate();
		MapRetryContextCache cache = new MapRetryContextCache();
		retryTemplate.setRetryContextCache(cache);
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy(1));

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> retryTemplate.execute(callback, retryState))
			.withMessage(null);

		retryTemplate.execute(callback, retryState);
		// The second attempt is successful by design...

		// Callback is called twice because its state is null: the recovery path should
		// not be called...
		assertThat(callback.attempts).isEqualTo(2);
	}

	private static final class MockRetryCallback implements RetryCallback<String, Exception> {

        /**
         * <code>attempts</code>
         * <p>The <code>attempts</code> field.</p>
         */
        int attempts = 0;

        /**
         * <code>context</code>
         * {@link org.springframework.retry.RetryContext} <p>The <code>context</code> field.</p>
         * @see  org.springframework.retry.RetryContext
         */
        RetryContext context;

		public String doWithRetry(RetryContext context) {
			attempts++;
			this.context = context;
			if (attempts < 2) {
				throw new RuntimeException();
			}
			return "bar";
		}

	}

    /**
     * <code>SerializedMapRetryContextCache</code>
     * <p>The serialized map retry context cache class.</p>
     * @see  org.springframework.retry.policy.AbstractMapRetryContextCache
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    static class SerializedMapRetryContextCache extends AbstractMapRetryContextCache<byte[]> {

        /**
         * <code>SerializedMapRetryContextCache</code>
         * <p>Instantiates a new serialized map retry context cache.</p>
         */
        public SerializedMapRetryContextCache() {
			super(DEFAULT_CAPACITY, true);
		}

		@Override
		protected byte[] toValue(RetryContext context) {
			return org.springframework.util.SerializationUtils.serialize(context);
		}

		@Override
		protected RetryContext fromValue(byte[] value) {
			try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
					new java.io.ByteArrayInputStream(value))) {
				return (RetryContext) ois.readObject();
			}
			catch (java.io.IOException ex) {
				throw new IllegalArgumentException("Failed to deserialize object", ex);
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException("Failed to deserialize object type", ex);
			}
		}

	}

}
