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

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>SubclassExceptionClassifierTests</code>
 * <p>The subclass exception classifier tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class SubclassExceptionClassifierTests {

    /**
     * <code>classifier</code>
     * {@link org.springframework.classify.SubclassClassifier} <p>The <code>classifier</code> field.</p>
     * @see  org.springframework.classify.SubclassClassifier
     */
    SubclassClassifier<Throwable, String> classifier = new SubclassClassifier<>();

    /**
     * <code>testClassifyNullIsDefault</code>
     * <p>The test classify null is default method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyNullIsDefault() {
		assertThat(this.classifier.getDefault()).isEqualTo(this.classifier.classify(null));
	}

    /**
     * <code>testClassifyNull</code>
     * <p>The test classify null method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyNull() {
		assertThat(this.classifier.classify(null)).isNull();
	}

    /**
     * <code>testClassifyNullNonDefault</code>
     * <p>The test classify null non default method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyNullNonDefault() {
		this.classifier = new SubclassClassifier<>("foo");
		assertThat(this.classifier.classify(null)).isEqualTo("foo");
	}

    /**
     * <code>testClassifyRandomException</code>
     * <p>The test classify random exception method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyRandomException() {
		assertThat(this.classifier.classify(new IllegalStateException("Foo"))).isNull();
	}

    /**
     * <code>testClassifyExactMatch</code>
     * <p>The test classify exact match method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyExactMatch() {
		this.classifier.setTypeMap(
				Collections.<Class<? extends Throwable>, String>singletonMap(IllegalStateException.class, "foo"));
		assertThat(this.classifier.classify(new IllegalStateException("Foo"))).isEqualTo("foo");
	}

    /**
     * <code>testClassifySubclassMatch</code>
     * <p>The test classify subclass match method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifySubclassMatch() {
		this.classifier
			.setTypeMap(Collections.<Class<? extends Throwable>, String>singletonMap(RuntimeException.class, "foo"));
		assertThat(this.classifier.classify(new IllegalStateException("Foo"))).isEqualTo("foo");
	}

    /**
     * <code>testClassifySuperclassDoesNotMatch</code>
     * <p>The test classify superclass does not match method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifySuperclassDoesNotMatch() {
		this.classifier.setTypeMap(
				Collections.<Class<? extends Throwable>, String>singletonMap(IllegalStateException.class, "foo"));
		assertThat(this.classifier.classify(new RuntimeException("Foo"))).isEqualTo(this.classifier.getDefault());
	}

    /**
     * <code>testClassifyAncestorMatch</code>
     * <p>The test classify ancestor match method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testClassifyAncestorMatch() {
		this.classifier.setTypeMap(new HashMap<Class<? extends Throwable>, String>() {
			{
				put(Exception.class, "foo");
				put(IllegalArgumentException.class, "bar");
				put(RuntimeException.class, "spam");
			}
		});
		assertThat(this.classifier.classify(new IllegalStateException("Foo"))).isEqualTo("spam");
	}

    /**
     * <code>testClassifyAncestorMatch2</code>
     * <p>The test classify ancestor match 2 method.</p>
     * @see  java.lang.SuppressWarnings
     * @see  org.junit.jupiter.api.Test
     */
    @SuppressWarnings("serial")
	@Test
	public void testClassifyAncestorMatch2() {
		this.classifier = new SubclassClassifier<>();
		this.classifier.setTypeMap(new HashMap<Class<? extends Throwable>, String>() {
			{
				put(SocketException.class, "1");
				put(FileNotFoundException.class, "buz");
				put(NoSuchElementException.class, "buz");
				put(ArrayIndexOutOfBoundsException.class, "buz");
				put(IllegalArgumentException.class, "bar");
				put(RuntimeException.class, "spam");
				put(ConnectException.class, "2");
			}
		});
		assertThat(this.classifier.classify(new SubConnectException())).isEqualTo("2");
	}

    /**
     * <code>SubConnectException</code>
     * <p>The sub connect exception class.</p>
     * @see  java.net.ConnectException
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class SubConnectException extends ConnectException {

	}

}
