/*
 * Copyright 2024-2025 the original author or authors.
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

import java.util.*;
import java.util.function.Function;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import org.springframework.lang.Nullable;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.util.Assert;

/**
 * <code>MetricsRetryListener</code>
 * <p>The metrics retry listener class.</p>
 * @see  org.springframework.retry.RetryListener
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class MetricsRetryListener implements RetryListener {

    /**
     * <code>TIMER_NAME</code>
     * {@link java.lang.String} <p>The constant <code>TIMER_NAME</code> field.</p>
     * @see  java.lang.String
     */
    public static final String TIMER_NAME = "spring.retry";

	private final MeterRegistry meterRegistry;

	private final Map<RetryContext, Timer.Sample> retryContextToSample = Collections
		.synchronizedMap(new IdentityHashMap<>());

	private Tags customTags = Tags.empty();

	private Function<RetryContext, Iterable<Tag>> customTagsProvider = retryContext -> Tags.empty();

    /**
     * <code>MetricsRetryListener</code>
     * <p>Instantiates a new metrics retry listener.</p>
     * @param meterRegistry {@link io.micrometer.core.instrument.MeterRegistry} <p>The meter registry parameter is <code>MeterRegistry</code> type.</p>
     * @see  io.micrometer.core.instrument.MeterRegistry
     */
    public MetricsRetryListener(MeterRegistry meterRegistry) {
		Assert.notNull(meterRegistry, "'meterRegistry' must not be null");
		this.meterRegistry = meterRegistry;
	}

    /**
     * <code>setCustomTags</code>
     * <p>The set custom tags setter method.</p>
     * @param customTags {@link java.lang.Iterable} <p>The custom tags parameter is <code>Iterable</code> type.</p>
     * @see  java.lang.Iterable
     * @see  org.springframework.lang.Nullable
     */
    public void setCustomTags(@Nullable Iterable<Tag> customTags) {
		this.customTags = this.customTags.and(customTags);
	}

    /**
     * <code>setCustomTagsProvider</code>
     * <p>The set custom tags provider setter method.</p>
     * @param customTagsProvider {@link java.util.function.Function} <p>The custom tags provider parameter is <code>Function</code> type.</p>
     * @see  java.util.function.Function
     */
    public void setCustomTagsProvider(Function<RetryContext, Iterable<Tag>> customTagsProvider) {
		Assert.notNull(customTagsProvider, "'customTagsProvider' must not be null");
		this.customTagsProvider = customTagsProvider;
	}

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		this.retryContextToSample.put(context, Timer.start(this.meterRegistry));
		return true;
	}

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			@Nullable Throwable throwable) {

		Timer.Sample sample = this.retryContextToSample.remove(context);

		Assert.state(sample != null,
				() -> String.format("No 'Timer.Sample' registered for '%s'. Was the 'open()' called?", context));

		String label = Optional.ofNullable(callback.getLabel()).orElse(callback.getClass().getName());
		Tags retryTags = Tags.of("name", label)
			.and("retry.count", "" + context.getRetryCount())
			.and(this.customTags)
			.and(this.customTagsProvider.apply(context))
			.and("exception", throwable != null ? throwable.getClass().getSimpleName() : "none");

		Timer.Builder timeBuilder = Timer.builder(TIMER_NAME)
			.description("Metrics for Spring RetryTemplate")
			.tags(retryTags);

		sample.stop(timeBuilder.register(this.meterRegistry));
	}

}
