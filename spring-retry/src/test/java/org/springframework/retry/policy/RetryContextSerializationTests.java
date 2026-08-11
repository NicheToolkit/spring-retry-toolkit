/*
 * Copyright 2006-2024 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.classify.SubclassClassifier;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.util.ClassUtils;
import org.springframework.util.SerializationUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>RetryContextSerializationTests</code>
 * <p>The retry context serialization tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RetryContextSerializationTests {

	private static final Log logger = LogFactory.getLog(RetryContextSerializationTests.class);

    /**
     * <code>policies</code>
     * <p>The policies method.</p>
     * @return  {@link java.util.List} <p>The policies return object is <code>List</code> type.</p>
     * @see  java.util.List
     */
    public static List<Object[]> policies() {
		List<Object[]> result = new ArrayList<>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		scanner.addIncludeFilter(new AssignableTypeFilter(RetryPolicy.class));
		scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
		scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Mock.*")));
		Set<BeanDefinition> candidates = scanner.findCandidateComponents("org.springframework.retry.policy");
		for (BeanDefinition beanDefinition : candidates) {
			try {
				result.add(new Object[] { BeanUtils
					.instantiateClass(ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), null)) });
			}
			catch (Exception e) {
				logger.info("Cannot create instance of " + beanDefinition.getBeanClassName(), e);
			}
		}
		ExceptionClassifierRetryPolicy extra = new ExceptionClassifierRetryPolicy();
		extra.setExceptionClassifier(new SubclassClassifier<>(new AlwaysRetryPolicy()));
		result.add(new Object[] { extra });
		return result;
	}

    /**
     * <code>testSerializationCycleForContext</code>
     * <p>The test serialization cycle for context method.</p>
     * @param policy {@link org.springframework.retry.RetryPolicy} <p>The policy parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.junit.jupiter.params.ParameterizedTest
     * @see  org.junit.jupiter.params.provider.MethodSource
     */
    @ParameterizedTest
	@MethodSource("policies")
	public void testSerializationCycleForContext(RetryPolicy policy) {
		RetryContext context = policy.open(null);
		assertThat(context.getRetryCount()).isEqualTo(0);
		policy.registerThrowable(context, new RuntimeException());
		assertThat(context.getRetryCount()).isEqualTo(1);
		assertThat(deserialize(SerializationUtils.serialize(context))).extracting("retryCount").isEqualTo(1);
	}

    /**
     * <code>testSerializationCycleForPolicy</code>
     * <p>The test serialization cycle for policy method.</p>
     * @param policy {@link org.springframework.retry.RetryPolicy} <p>The policy parameter is <code>RetryPolicy</code> type.</p>
     * @see  org.springframework.retry.RetryPolicy
     * @see  org.junit.jupiter.params.ParameterizedTest
     * @see  org.junit.jupiter.params.provider.MethodSource
     */
    @ParameterizedTest
	@MethodSource("policies")
	public void testSerializationCycleForPolicy(RetryPolicy policy) {
		assertThat(deserialize(SerializationUtils.serialize(policy)) instanceof RetryPolicy).isTrue();
	}

	private static Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
			return ois.readObject();
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to deserialize object", ex);
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Failed to deserialize object type", ex);
		}
	}

}
