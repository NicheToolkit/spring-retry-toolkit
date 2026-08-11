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

package org.springframework.classify;

import java.util.Collections;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <code>SubclassClassifierTests</code>
 * <p>The subclass classifier tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class SubclassClassifierTests {

    /**
     * <code>testClassifyInterface</code>
     * <p>The test classify interface method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyInterface() {
		SubclassClassifier<Object, String> classifier = new SubclassClassifier<>();
		classifier.setTypeMap(Collections.<Class<?>, String>singletonMap(Supplier.class, "foo"));
		assertThat(classifier.classify(new Foo())).isEqualTo("foo");
	}

    /**
     * <code>testClassifyInterfaceOfParent</code>
     * <p>The test classify interface of parent method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testClassifyInterfaceOfParent() {
		SubclassClassifier<Object, String> classifier = new SubclassClassifier<>();
		classifier.setTypeMap(Collections.<Class<?>, String>singletonMap(Supplier.class, "foo"));
		assertThat(classifier.classify(new Bar())).isEqualTo("foo");
	}

    /**
     * <code>Bar</code>
     * <p>The bar class.</p>
     * @see  org.springframework.classify.SubclassClassifierTests.Foo
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public class Bar extends Foo {

	}

    /**
     * <code>Foo</code>
     * <p>The foo class.</p>
     * @see  java.util.function.Supplier
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    public static class Foo implements Supplier<String> {

		@Override
		public String get() {
			return "foo";
		}

	}

}
