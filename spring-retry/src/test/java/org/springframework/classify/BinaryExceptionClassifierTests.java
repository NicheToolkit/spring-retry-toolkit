/*
 * Copyright 2006-2023 the original author or authors.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.beans.DirectFieldAccessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>BinaryExceptionClassifierTests</code>
 * <p>The binary exception classifier tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class BinaryExceptionClassifierTests {

    /**
     * <code>classifier</code>
     * {@link org.springframework.classify.BinaryExceptionClassifier} <p>The <code>classifier</code> field.</p>
     */
    BinaryExceptionClassifier classifier = new BinaryExceptionClassifier(false);

    /**
     * <code>testClassifyNullIsDefault</code>
     * <p>The test classify null is default method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyNullIsDefault() {
		assertThat(classifier.classify(null)).isFalse();
	}

    /**
     * <code>testFalseIsDefault</code>
     * <p>The test false is default method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testFalseIsDefault() {
		assertThat(classifier.getDefault()).isFalse();
	}

    /**
     * <code>testDefaultProvided</code>
     * <p>The test default provided method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testDefaultProvided() {
		classifier = new BinaryExceptionClassifier(true);
		assertThat(classifier.getDefault()).isTrue();
	}

    /**
     * <code>testClassifyRandomException</code>
     * <p>The test classify random exception method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyRandomException() {
		assertThat(classifier.classify(new IllegalStateException("foo"))).isFalse();
	}

    /**
     * <code>testClassifyExactMatch</code>
     * <p>The test classify exact match method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyExactMatch() {
		Collection<Class<? extends Throwable>> set = Collections
			.<Class<? extends Throwable>>singleton(IllegalStateException.class);
		assertThat(new BinaryExceptionClassifier(set).classify(new IllegalStateException("Foo"))).isTrue();
	}

    /**
     * <code>testClassifyExactMatchInCause</code>
     * <p>The test classify exact match in cause method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyExactMatchInCause() {
		Collection<Class<? extends Throwable>> set = Collections
			.<Class<? extends Throwable>>singleton(IllegalStateException.class);
		BinaryExceptionClassifier binaryExceptionClassifier = new BinaryExceptionClassifier(set);
		binaryExceptionClassifier.setTraverseCauses(true);
		assertThat(binaryExceptionClassifier.classify(new RuntimeException(new IllegalStateException("Foo")))).isTrue();
	}

    /**
     * <code>testClassifySubclassMatchInCause</code>
     * <p>The test classify subclass match in cause method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifySubclassMatchInCause() {
		Collection<Class<? extends Throwable>> set = Collections
			.<Class<? extends Throwable>>singleton(IllegalStateException.class);
		BinaryExceptionClassifier binaryExceptionClassifier = new BinaryExceptionClassifier(set);
		binaryExceptionClassifier.setTraverseCauses(true);
		assertThat(binaryExceptionClassifier.classify(new RuntimeException(new FooException("Foo")))).isTrue();
	}

    /**
     * <code>testClassifySubclassMatchInCauseFalse</code>
     * <p>The test classify subclass match in cause false method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifySubclassMatchInCauseFalse() {
		Map<Class<? extends Throwable>, Boolean> map = new HashMap<>();
		map.put(IllegalStateException.class, true);
		map.put(BarException.class, false);
		BinaryExceptionClassifier binaryExceptionClassifier = new BinaryExceptionClassifier(map, true);
		binaryExceptionClassifier.setTraverseCauses(true);
		assertThat(
				binaryExceptionClassifier.classify(new RuntimeException(new FooException("Foo", new BarException()))))
			.isTrue();
		assertThat(((Map<?, ?>) new DirectFieldAccessor(binaryExceptionClassifier).getPropertyValue("classified"))
			.containsKey(FooException.class)).isTrue();
	}

    /**
     * <code>testTypesProvidedInConstructor</code>
     * <p>The test types provided in constructor method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTypesProvidedInConstructor() {
		classifier = new BinaryExceptionClassifier(
				Collections.<Class<? extends Throwable>>singleton(IllegalStateException.class));
		assertThat(classifier.classify(new IllegalStateException("Foo"))).isTrue();
	}

    /**
     * <code>testTypesProvidedInConstructorWithNonDefault</code>
     * <p>The test types provided in constructor with non default method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTypesProvidedInConstructorWithNonDefault() {
		classifier = new BinaryExceptionClassifier(
				Collections.<Class<? extends Throwable>>singleton(IllegalStateException.class), false);
		assertThat(classifier.classify(new IllegalStateException("Foo"))).isFalse();
	}

    /**
     * <code>testTypesProvidedInConstructorWithNonDefaultInCause</code>
     * <p>The test types provided in constructor with non default in cause method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testTypesProvidedInConstructorWithNonDefaultInCause() {
		classifier = new BinaryExceptionClassifier(
				Collections.<Class<? extends Throwable>>singleton(IllegalStateException.class), false);
		classifier.setTraverseCauses(true);
		assertThat(classifier.classify(new RuntimeException(new RuntimeException(new IllegalStateException("Foo")))))
			.isFalse();
	}

	@SuppressWarnings("serial")
	private class FooException extends IllegalStateException {

		private FooException(String s) {
			super(s);
		}

		private FooException(String s, Throwable t) {
			super(s, t);
		}

	}

	@SuppressWarnings("serial")
	private class BarException extends RuntimeException {

		private BarException() {
			super();
		}

	}

}
