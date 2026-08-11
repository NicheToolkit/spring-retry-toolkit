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

package org.springframework.retry.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.core.annotation.AliasFor;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>RecoverAnnotationRecoveryHandlerTests</code>
 * <p>The recover annotation recovery handler tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class RecoverAnnotationRecoveryHandlerTests {

    /**
     * <code>genericReturnTypesMatch</code>
     * <p>The generic return types match method.</p>
     * @see  org.junit.jupiter.api.Test
     * @see  java.lang.reflect.InvocationTargetException
     * @see  java.lang.IllegalAccessException
     * @throws InvocationTargetException {@link java.lang.reflect.InvocationTargetException} <p>The invocation target exception is <code>InvocationTargetException</code> type.</p>
     * @throws IllegalAccessException {@link java.lang.IllegalAccessException} <p>The illegal access exception is <code>IllegalAccessException</code> type.</p>
     */
    @Test
	public void genericReturnTypesMatch() throws InvocationTargetException, IllegalAccessException {
		Method isParameterizedTypeAssignable = ReflectionUtils.findMethod(RecoverAnnotationRecoveryHandler.class,
				"isParameterizedTypeAssignable", ParameterizedType.class, ParameterizedType.class);
		isParameterizedTypeAssignable.setAccessible(true);

		assertThat(isParameterizedTypeAssignable.invoke(null, getGenericReturnTypeByName("m1"),
				getGenericReturnTypeByName("m2")))
			.isEqualTo(Boolean.TRUE);
		assertThat(isParameterizedTypeAssignable.invoke(null, getGenericReturnTypeByName("m2"),
				getGenericReturnTypeByName("m2_1")))
			.isEqualTo(Boolean.FALSE);
		assertThat(isParameterizedTypeAssignable.invoke(null, getGenericReturnTypeByName("m3"),
				getGenericReturnTypeByName("m4")))
			.isEqualTo(Boolean.FALSE);
		assertThat(isParameterizedTypeAssignable.invoke(null, getGenericReturnTypeByName("m5"),
				getGenericReturnTypeByName("m6")))
			.isEqualTo(Boolean.TRUE);
	}

	private static ParameterizedType getGenericReturnTypeByName(String name) {
		return (ParameterizedType) ReflectionUtils.findMethod(ParameterTest.class, name).getGenericReturnType();
	}

    /**
     * <code>defaultRecoverMethod</code>
     * <p>The default recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void defaultRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new DefaultRecover(), ReflectionUtils.findMethod(DefaultRecover.class, "foo", String.class));
		assertThat(handler.recover(new Object[] { "Dave" }, new RuntimeException("Planned"))).isEqualTo(1);
	}

    /**
     * <code>fewerArgs</code>
     * <p>The fewer args method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void fewerArgs() {
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(new FewerArgs(),
				ReflectionUtils.findMethod(FewerArgs.class, "foo", String.class, int.class));
		assertThat(handler.recover(new Object[] { "Dave" }, new RuntimeException("Planned"))).isEqualTo(1);
	}

    /**
     * <code>noArgs</code>
     * <p>The no args method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void noArgs() {
		NoArgs target = new NoArgs();
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(target,
				ReflectionUtils.findMethod(NoArgs.class, "foo"));
		handler.recover(new Object[0], new RuntimeException("Planned"));
		assertThat(target.getCause().getMessage()).isEqualTo("Planned");
	}

    /**
     * <code>noMatch</code>
     * <p>The no match method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void noMatch() {
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new SpecificException(), ReflectionUtils.findMethod(SpecificException.class, "foo", String.class));
		assertThatExceptionOfType(ExhaustedRetryException.class)
			.isThrownBy(() -> handler.recover(new Object[] { "Dave" }, new Error("Planned")));
	}

    /**
     * <code>specificRecoverMethod</code>
     * <p>The specific recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void specificRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new SpecificRecover(), ReflectionUtils.findMethod(SpecificRecover.class, "foo", String.class));
		assertThat(handler.recover(new Object[] { "Dave" }, new RuntimeException("Planned"))).isEqualTo(2);
	}

    /**
     * <code>inAccessibleRecoverMethods</code>
     * <p>The in accessible recover methods method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void inAccessibleRecoverMethods() {
		Method foo = ReflectionUtils.findMethod(InAccessibleRecover.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new InAccessibleRecover(), foo);
		assertThat(handler.recover(new Object[] { "Dave" }, new RuntimeException("Planned"))).isEqualTo(1);

	}

    /**
     * <code>specificReturnTypeRecoverMethod</code>
     * <p>The specific return type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void specificReturnTypeRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> fooHandler = new RecoverAnnotationRecoveryHandler<Integer>(
				new InheritanceReturnTypeRecover(),
				ReflectionUtils.findMethod(InheritanceReturnTypeRecover.class, "foo", String.class));
		assertThat(fooHandler.recover(new Object[] { "Aldo" }, new RuntimeException("Planned"))).isEqualTo(1);
		assertThat(fooHandler.recover(new Object[] { "Aldo" }, new IllegalStateException("Planned"))).isEqualTo(2);

	}

    /**
     * <code>parentReturnTypeRecoverMethod</code>
     * <p>The parent return type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void parentReturnTypeRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> barHandler = new RecoverAnnotationRecoveryHandler<Double>(
				new InheritanceReturnTypeRecover(),
				ReflectionUtils.findMethod(InheritanceReturnTypeRecover.class, "bar", String.class));
		assertThat(barHandler.recover(new Object[] { "Aldo" }, new RuntimeException("Planned"))).isEqualTo(3);

	}

    /**
     * <code>genericReturnStringValueTypeParentThrowableRecoverMethod</code>
     * <p>The generic return string value type parent throwable recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericReturnStringValueTypeParentThrowableRecoverMethod() {

		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<List<String>>(
				new GenericReturnTypeRecover(),
				ReflectionUtils.findMethod(GenericReturnTypeRecover.class, "foo", String.class));

		@SuppressWarnings("unchecked")
		Map<String, String> recoverResponseMap = (Map<String, String>) handler.recover(new Object[] { "Aldo" },
				new RuntimeException("Planned"));
		assertThat(CollectionUtils.isEmpty(recoverResponseMap)).isFalse();
		assertThat(recoverResponseMap.get("foo")).isEqualTo("fooRecoverValue1");
	}

    /**
     * <code>genericReturnStringValueTypeChildThrowableRecoverMethod</code>
     * <p>The generic return string value type child throwable recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericReturnStringValueTypeChildThrowableRecoverMethod() {

		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<List<String>>(
				new GenericReturnTypeRecover(),
				ReflectionUtils.findMethod(GenericReturnTypeRecover.class, "foo", String.class));

		@SuppressWarnings("unchecked")
		Map<String, String> recoverResponseMap = (Map<String, String>) handler.recover(new Object[] { "Aldo" },
				new IllegalStateException("Planned"));
		assertThat(CollectionUtils.isEmpty(recoverResponseMap)).isFalse();
		assertThat(recoverResponseMap.get("foo")).isEqualTo("fooRecoverValue2");
	}

    /**
     * <code>genericReturnOneValueTypeRecoverMethod</code>
     * <p>The generic return one value type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericReturnOneValueTypeRecoverMethod() {

		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<List<String>>(
				new GenericReturnTypeRecover(),
				ReflectionUtils.findMethod(GenericReturnTypeRecover.class, "bar", String.class));

		@SuppressWarnings("unchecked")
		Map<String, GenericReturnTypeRecover.One> recoverResponseMap = (Map<String, GenericReturnTypeRecover.One>) handler
			.recover(new Object[] { "Aldo" }, new RuntimeException("Planned"));
		assertThat(CollectionUtils.isEmpty(recoverResponseMap)).isFalse();
		assertThat(recoverResponseMap.get("bar")).isNotNull();
		assertThat(recoverResponseMap.get("bar").name).isEqualTo("barRecoverValue");
	}

    /**
     * <code>genericSpecifiedReturnTypeRecoverMethod</code>
     * <p>The generic specified return type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericSpecifiedReturnTypeRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> fooHandler = new RecoverAnnotationRecoveryHandler<Integer>(
				new GenericInheritanceReturnTypeRecover(),
				ReflectionUtils.findMethod(GenericInheritanceReturnTypeRecover.class, "foo", String.class));
		@SuppressWarnings("unchecked")
		Map<String, Integer> recoverResponseMapRe = (Map<String, Integer>) fooHandler.recover(new Object[] { "Aldo" },
				new RuntimeException("Planned"));
		assertThat(recoverResponseMapRe.get("foo").intValue()).isEqualTo(1);
		@SuppressWarnings("unchecked")
		Map<String, Integer> recoverResponseMapIse = (Map<String, Integer>) fooHandler.recover(new Object[] { "Aldo" },
				new IllegalStateException("Planned"));
		assertThat(recoverResponseMapIse.get("foo").intValue()).isEqualTo(2);
	}

    /**
     * <code>genericDirectMatchReturnTypeRecoverMethod</code>
     * <p>The generic direct match return type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericDirectMatchReturnTypeRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> barHandler = new RecoverAnnotationRecoveryHandler<Double>(
				new GenericInheritanceReturnTypeRecover(),
				ReflectionUtils.findMethod(GenericInheritanceReturnTypeRecover.class, "bar", String.class));
		@SuppressWarnings("unchecked")
		Map<String, Number> recoverResponseMapRe = (Map<String, Number>) barHandler.recover(new Object[] { "Aldo" },
				new RuntimeException("Planned"));
		assertThat(recoverResponseMapRe.get("bar")).isEqualTo(0.2);
	}

    /**
     * <code>genericNestedMapIntegerStringReturnTypeRecoverMethod</code>
     * <p>The generic nested map integer string return type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericNestedMapIntegerStringReturnTypeRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> fooHandler = new RecoverAnnotationRecoveryHandler<Integer>(
				new NestedGenericInheritanceReturnTypeRecover(),
				ReflectionUtils.findMethod(NestedGenericInheritanceReturnTypeRecover.class, "foo", String.class));
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Map<Integer, String>>> recoverResponseMapRe = (Map<String, Map<String, Map<Integer, String>>>) fooHandler
			.recover(new Object[] { "Aldo" }, new RuntimeException("Planned"));
		assertThat(recoverResponseMapRe.get("foo").get("foo").get(0)).isEqualTo("fooRecoverReValue");
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Map<Integer, String>>> recoverResponseMapIe = (Map<String, Map<String, Map<Integer, String>>>) fooHandler
			.recover(new Object[] { "Aldo" }, new IllegalStateException("Planned"));
		assertThat(recoverResponseMapIe.get("foo").get("foo").get(0)).isEqualTo("fooRecoverIeValue");
	}

    /**
     * <code>genericNestedMapNumberStringReturnTypeRecoverMethod</code>
     * <p>The generic nested map number string return type recover method method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void genericNestedMapNumberStringReturnTypeRecoverMethod() {
		RecoverAnnotationRecoveryHandler<?> barHandler = new RecoverAnnotationRecoveryHandler<Double>(
				new NestedGenericInheritanceReturnTypeRecover(),
				ReflectionUtils.findMethod(NestedGenericInheritanceReturnTypeRecover.class, "bar", String.class));
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Map<Number, String>>> recoverResponseMapRe = (Map<String, Map<String, Map<Number, String>>>) barHandler
			.recover(new Object[] { "Aldo" }, new RuntimeException("Planned"));
		assertThat(recoverResponseMapRe.get("bar").get("bar").get(0.0)).isEqualTo("barRecoverNumberValue");

	}

    /**
     * <code>multipleQualifyingRecoverMethods</code>
     * <p>The multiple qualifying recover methods method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void multipleQualifyingRecoverMethods() {
		Method foo = ReflectionUtils.findMethod(MultipleQualifyingRecovers.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new MultipleQualifyingRecovers(), foo);
		assertThat(handler.recover(new Object[] { "Randell" }, new RuntimeException("Planned"))).isEqualTo(1);

	}

    /**
     * <code>multipleQualifyingRecoverMethodsWithNull</code>
     * <p>The multiple qualifying recover methods with null method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void multipleQualifyingRecoverMethodsWithNull() {
		Method foo = ReflectionUtils.findMethod(MultipleQualifyingRecovers.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new MultipleQualifyingRecovers(), foo);
		assertThat(handler.recover(new Object[] { null }, new RuntimeException("Planned"))).isEqualTo(1);

	}

    /**
     * <code>multipleQualifyingRecoverMethodsWithNoThrowable</code>
     * <p>The multiple qualifying recover methods with no throwable method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void multipleQualifyingRecoverMethodsWithNoThrowable() {
		Method foo = ReflectionUtils.findMethod(MultipleQualifyingRecoversNoThrowable.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new MultipleQualifyingRecoversNoThrowable(), foo);
		assertThat(handler.recover(new Object[] { null }, new RuntimeException("Planned"))).isEqualTo(1);

	}

    /**
     * <code>multipleQualifyingRecoverMethodsReOrdered</code>
     * <p>The multiple qualifying recover methods re ordered method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void multipleQualifyingRecoverMethodsReOrdered() {
		Method foo = ReflectionUtils.findMethod(MultipleQualifyingRecoversReOrdered.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new MultipleQualifyingRecoversReOrdered(), foo);
		assertThat(handler.recover(new Object[] { "Randell" }, new RuntimeException("Planned"))).isEqualTo(3);

	}

    /**
     * <code>multipleQualifyingRecoverMethodsExtendsThrowable</code>
     * <p>The multiple qualifying recover methods extends throwable method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void multipleQualifyingRecoverMethodsExtendsThrowable() {
		Method foo = ReflectionUtils.findMethod(MultipleQualifyingRecoversExtendsThrowable.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new MultipleQualifyingRecoversExtendsThrowable(), foo);
		assertThat(handler.recover(new Object[] { "Kevin" }, new IllegalArgumentException("Planned"))).isEqualTo(2);
		assertThat(handler.recover(new Object[] { "Kevin" }, new UnsupportedOperationException("Planned")))
			.isEqualTo(3);

	}

    /**
     * <code>inheritanceOnArgumentClass</code>
     * <p>The inheritance on argument class method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void inheritanceOnArgumentClass() {
		Method foo = ReflectionUtils.findMethod(InheritanceOnArgumentClass.class, "foo", List.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new InheritanceOnArgumentClass(), foo);
		assertThat(handler.recover(new Object[] { new ArrayList<String>() }, new IllegalArgumentException("Planned")))
			.isEqualTo(1);
	}

    /**
     * <code>recoverByRetryableName</code>
     * <p>The recover by retryable name method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void recoverByRetryableName() {
		Method foo = ReflectionUtils.findMethod(RecoverByRetryableName.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new RecoverByRetryableName(), foo);
		assertThat(handler.recover(new Object[] { "Kevin" }, new RuntimeException("Planned"))).isEqualTo(2);
	}

    /**
     * <code>recoverByRetryableNameWithPrimitiveArgs</code>
     * <p>The recover by retryable name with primitive args method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void recoverByRetryableNameWithPrimitiveArgs() {
		Method foo = ReflectionUtils.findMethod(RecoverByRetryableNameWithPrimitiveArgs.class, "foo", int.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new RecoverByRetryableNameWithPrimitiveArgs(), foo);
		assertThat(handler.recover(new Object[] { 2 }, new RuntimeException("Planned"))).isEqualTo(2);
	}

    /**
     * <code>recoverByComposedRetryableAnnotationName</code>
     * <p>The recover by composed retryable annotation name method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void recoverByComposedRetryableAnnotationName() {
		Method foo = ReflectionUtils.findMethod(RecoverByComposedRetryableAnnotationName.class, "foo", String.class);
		RecoverAnnotationRecoveryHandler<?> handler = new RecoverAnnotationRecoveryHandler<Integer>(
				new RecoverByComposedRetryableAnnotationName(), foo);
		assertThat(handler.recover(new Object[] { "Kevin" }, new RuntimeException("Planned"))).isEqualTo(4);
	}

	private static class ParameterTest<T, M> {

        /**
         * <code>m1</code>
         * <p>The m 1 method.</p>
         * @return  {@link java.util.List} <p>The m 1 return object is <code>List</code> type.</p>
         * @see  java.util.List
         */
        List<T> m1() {
			return null;
		}

        /**
         * <code>m2</code>
         * <p>The m 2 method.</p>
         * @return  {@link java.util.List} <p>The m 2 return object is <code>List</code> type.</p>
         * @see  java.util.List
         */
        List<T> m2() {
			return null;
		}

        /**
         * <code>m2_1</code>
         * <p>The m 2 1 method.</p>
         * @return  {@link java.util.List} <p>The m 2 1 return object is <code>List</code> type.</p>
         * @see  java.util.List
         */
        List<M> m2_1() {
			return null;
		}

        /**
         * <code>m3</code>
         * <p>The m 3 method.</p>
         * @return  {@link java.util.Map} <p>The m 3 return object is <code>Map</code> type.</p>
         * @see  java.util.Map
         */
        Map<List<String>, Byte> m3() {
			return null;
		}

        /**
         * <code>m4</code>
         * <p>The m 4 method.</p>
         * @return  {@link java.util.Map} <p>The m 4 return object is <code>Map</code> type.</p>
         * @see  java.util.Map
         */
        Map<List<String>, Integer> m4() {
			return null;
		}

        /**
         * <code>m5</code>
         * <p>The m 5 method.</p>
         * @return  {@link java.util.Map} <p>The m 5 return object is <code>Map</code> type.</p>
         * @see  java.util.Map
         */
        Map<List<Integer>, Byte> m5() {
			return null;
		}

        /**
         * <code>m6</code>
         * <p>The m 6 method.</p>
         * @return  {@link java.util.Map} <p>The m 6 return object is <code>Map</code> type.</p>
         * @see  java.util.Map
         */
        Map<List<Integer>, Byte> m6() {
			return null;
		}

	}

	private static class InAccessibleRecover {

		@Retryable
		private int foo(String n) {
			throw new RuntimeException("error trying to foo('" + n + "')");
		}

		@Recover
		private int bar(String n) {
			return 1;
		}

	}

    /**
     * <code>DefaultRecover</code>
     * <p>The default recover class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class DefaultRecover {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @return  int <p>The bar return object is <code>int</code> type.</p>
         */
        @Recover
		public int bar(String name) {
			return 1;
		}

	}

    /**
     * <code>NoArgs</code>
     * <p>The no args class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class NoArgs {

		private Throwable cause;

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable
		public void foo() {
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param cause {@link java.lang.Throwable} <p>The cause parameter is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         */
        @Recover
		public void bar(Throwable cause) {
			this.cause = cause;
		}

        /**
         * <code>getCause</code>
         * <p>The get cause getter method.</p>
         * @return  {@link java.lang.Throwable} <p>The get cause return object is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         */
        public Throwable getCause() {
			return this.cause;
		}

	}

    /**
     * <code>SpecificRecover</code>
     * <p>The specific recover class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class SpecificRecover {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @return  int <p>The bar return object is <code>int</code> type.</p>
         */
        @Recover
		public int bar(String name) {
			return 1;
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param e {@link java.lang.RuntimeException} <p>The e parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @return  int <p>The bar return object is <code>int</code> type.</p>
         */
        @Recover
		public int bar(RuntimeException e, String name) {
			return 2;
		}

	}

    /**
     * <code>FewerArgs</code>
     * <p>The fewer args class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class FewerArgs {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @param value int <p>The value parameter is <code>int</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name, int value) {
			return 0;
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param e {@link java.lang.RuntimeException} <p>The e parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @return  int <p>The bar return object is <code>int</code> type.</p>
         */
        @Recover
		public int bar(RuntimeException e, String name) {
			return 1;
		}

	}

    /**
     * <code>SpecificException</code>
     * <p>The specific exception class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class SpecificException {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param e {@link java.lang.RuntimeException} <p>The e parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @return  int <p>The bar return object is <code>int</code> type.</p>
         */
        @Recover
		public int bar(RuntimeException e, String name) {
			return 1;
		}

	}

    /**
     * <code>InheritanceReturnTypeRecover</code>
     * <p>The inheritance return type recover class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class InheritanceReturnTypeRecover {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.lang.Integer
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.lang.Integer} <p>The foo return object is <code>Integer</code> type.</p>
         */
        @Retryable
		public Integer foo(String name) {
			return 0;
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.lang.Double
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.lang.Double} <p>The bar return object is <code>Double</code> type.</p>
         */
        @Retryable
		public Double bar(String name) {
			return 0.0;
		}

        /**
         * <code>baz</code>
         * <p>The baz method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.lang.Integer
         * @return  {@link java.lang.Integer} <p>The baz return object is <code>Integer</code> type.</p>
         */
        @Recover
		public Integer baz(RuntimeException re, String name) {
			return 1;
		}

        /**
         * <code>qux</code>
         * <p>The qux method.</p>
         * @param re {@link java.lang.IllegalStateException} <p>The re parameter is <code>IllegalStateException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.IllegalStateException
         * @see  java.lang.String
         * @see  java.lang.Integer
         * @return  {@link java.lang.Integer} <p>The qux return object is <code>Integer</code> type.</p>
         */
        @Recover
		public Integer qux(IllegalStateException re, String name) {
			return 2;
		}

        /**
         * <code>quux</code>
         * <p>The quux method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.lang.Number
         * @return  {@link java.lang.Number} <p>The quux return object is <code>Number</code> type.</p>
         */
        @Recover
		public Number quux(RuntimeException re, String name) {
			return 3;
		}

	}

    /**
     * <code>GenericReturnTypeRecover</code>
     * <p>The generic return type recover class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class GenericReturnTypeRecover {

		private static class One {

            /**
             * <code>name</code>
             * {@link java.lang.String} <p>The <code>name</code> field.</p>
             * @see  java.lang.String
             */
            String name;

            /**
             * <code>One</code>
             * <p>Instantiates a new one.</p>
             * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
             * @see  java.lang.String
             */
            public One(String name) {
				this.name = name;
			}

		}

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.util.Map
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.util.Map} <p>The foo return object is <code>Map</code> type.</p>
         */
        @Retryable
		public Map<String, String> foo(String name) {
			return Collections.singletonMap("foo", "fooValue");
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.util.Map
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.util.Map} <p>The bar return object is <code>Map</code> type.</p>
         */
        @Retryable
		public Map<String, One> bar(String name) {
			return Collections.singletonMap("bar", new One("barValue"));
		}

        /**
         * <code>fooRecoverRe</code>
         * <p>The foo recover re method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The foo recover re return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, String> fooRecoverRe(RuntimeException re, String name) {
			return Collections.singletonMap("foo", "fooRecoverValue1");
		}

        /**
         * <code>fooRecoverIe</code>
         * <p>The foo recover ie method.</p>
         * @param re {@link java.lang.IllegalStateException} <p>The re parameter is <code>IllegalStateException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.IllegalStateException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The foo recover ie return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, String> fooRecoverIe(IllegalStateException re, String name) {
			return Collections.singletonMap("foo", "fooRecoverValue2");
		}

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The bar recover return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, One> barRecover(RuntimeException re, String name) {
			return Collections.singletonMap("bar", new One("barRecoverValue"));
		}

	}

    /**
     * <code>GenericInheritanceReturnTypeRecover</code>
     * <p>The generic inheritance return type recover class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class GenericInheritanceReturnTypeRecover {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.util.Map
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.util.Map} <p>The foo return object is <code>Map</code> type.</p>
         */
        @Retryable
		public Map<String, Integer> foo(String name) {
			return Collections.singletonMap("foo", 0);
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.util.Map
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.util.Map} <p>The bar return object is <code>Map</code> type.</p>
         */
        @Retryable
		public Map<String, Number> bar(String name) {
			return Collections.singletonMap("bar", (Number) 0.0);
		}

        /**
         * <code>fooRecoverRe</code>
         * <p>The foo recover re method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The foo recover re return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Integer> fooRecoverRe(RuntimeException re, String name) {
			return Collections.singletonMap("foo", 1);
		}

        /**
         * <code>fooRecoverIe</code>
         * <p>The foo recover ie method.</p>
         * @param re {@link java.lang.IllegalStateException} <p>The re parameter is <code>IllegalStateException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.IllegalStateException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The foo recover ie return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Integer> fooRecoverIe(IllegalStateException re, String name) {
			return Collections.singletonMap("foo", 2);
		}

        /**
         * <code>barRecoverDouble</code>
         * <p>The bar recover double method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The bar recover double return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Double> barRecoverDouble(RuntimeException re, String name) {
			return Collections.singletonMap("bar", 0.1);
		}

        /**
         * <code>barRecoverNumber</code>
         * <p>The bar recover number method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The bar recover number return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Number> barRecoverNumber(RuntimeException re, String name) {
			return Collections.singletonMap("bar", (Number) 0.2);
		}

	}

    /**
     * <code>NestedGenericInheritanceReturnTypeRecover</code>
     * <p>The nested generic inheritance return type recover class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class NestedGenericInheritanceReturnTypeRecover {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.util.Map
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.util.Map} <p>The foo return object is <code>Map</code> type.</p>
         */
        @Retryable
		public Map<String, Map<String, Map<Integer, String>>> foo(String name) {
			return Collections.singletonMap("foo",
					Collections.singletonMap("foo", Collections.singletonMap(0, "fooValue")));
		}

        /**
         * <code>bar</code>
         * <p>The bar method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  java.util.Map
         * @see  org.springframework.retry.annotation.Retryable
         * @return  {@link java.util.Map} <p>The bar return object is <code>Map</code> type.</p>
         */
        @Retryable
		public Map<String, Map<String, Map<Number, String>>> bar(String name) {
			return Collections.singletonMap("bar",
					Collections.singletonMap("bar", Collections.singletonMap((Number) 0.0, "barValue")));
		}

        /**
         * <code>fooRecoverRe</code>
         * <p>The foo recover re method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The foo recover re return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Map<String, Map<Integer, String>>> fooRecoverRe(RuntimeException re, String name) {
			return Collections.singletonMap("foo",
					Collections.singletonMap("foo", Collections.singletonMap(0, "fooRecoverReValue")));
		}

        /**
         * <code>fooRecoverIe</code>
         * <p>The foo recover ie method.</p>
         * @param re {@link java.lang.IllegalStateException} <p>The re parameter is <code>IllegalStateException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.IllegalStateException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The foo recover ie return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Map<String, Map<Integer, String>>> fooRecoverIe(IllegalStateException re, String name) {
			return Collections.singletonMap("foo",
					Collections.singletonMap("foo", Collections.singletonMap(0, "fooRecoverIeValue")));
		}

        /**
         * <code>barRecoverNumber</code>
         * <p>The bar recover number method.</p>
         * @param re {@link java.lang.RuntimeException} <p>The re parameter is <code>RuntimeException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.RuntimeException
         * @see  java.lang.String
         * @see  java.util.Map
         * @return  {@link java.util.Map} <p>The bar recover number return object is <code>Map</code> type.</p>
         */
        @Recover
		public Map<String, Map<String, Map<Number, String>>> barRecoverNumber(RuntimeException re, String name) {
			return Collections.singletonMap("bar",
					Collections.singletonMap("bar", Collections.singletonMap((Number) 0.0, "barRecoverNumberValue")));
		}

	}

    /**
     * <code>MultipleQualifyingRecoversNoThrowable</code>
     * <p>The multiple qualifying recovers no throwable class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class MultipleQualifyingRecoversNoThrowable {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @param nullable {@link java.lang.String} <p>The nullable parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(String name, String nullable) {
			return 1;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param other int <p>The other parameter is <code>int</code> type.</p>
         * @param nullable {@link java.lang.String} <p>The nullable parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(int other, String nullable) {
			return 2;
		}

	}

    /**
     * <code>MultipleQualifyingRecovers</code>
     * <p>The multiple qualifying recovers class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class MultipleQualifyingRecovers {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable e, String name) {
			return 1;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable e) {
			return 2;
		}

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
         * @param number int <p>The number parameter is <code>int</code> type.</p>
         * @see  java.lang.Throwable
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(Throwable e, int number) {
			return 3;
		}

	}

    /**
     * <code>MultipleQualifyingRecoversReOrdered</code>
     * <p>The multiple qualifying recovers re ordered class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class MultipleQualifyingRecoversReOrdered {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
         * @see  java.lang.Throwable
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable e) {
			return 1;
		}

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
         * @param number int <p>The number parameter is <code>int</code> type.</p>
         * @see  java.lang.Throwable
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(Throwable e, int number) {
			return 2;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param e {@link java.lang.Throwable} <p>The e parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable e, String name) {
			return 3;
		}

	}

    /**
     * <code>MultipleQualifyingRecoversExtendsThrowable</code>
     * <p>The multiple qualifying recovers extends throwable class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class MultipleQualifyingRecoversExtendsThrowable {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(String name) {
			return 0;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param e {@link java.lang.IllegalArgumentException} <p>The e parameter is <code>IllegalArgumentException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.IllegalArgumentException
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(IllegalArgumentException e, String name) {
			return 1;
		}

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param e {@link java.lang.IllegalArgumentException} <p>The e parameter is <code>IllegalArgumentException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.IllegalArgumentException
         * @see  java.lang.String
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(IllegalArgumentException e, String name) {
			return 2;
		}

        /**
         * <code>bazRecover</code>
         * <p>The baz recover method.</p>
         * @param e {@link java.lang.UnsupportedOperationException} <p>The e parameter is <code>UnsupportedOperationException</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.UnsupportedOperationException
         * @see  java.lang.String
         * @return  int <p>The baz recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int bazRecover(UnsupportedOperationException e, String name) {
			return 3;
		}

	}

    /**
     * <code>InheritanceOnArgumentClass</code>
     * <p>The inheritance on argument class class.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class InheritanceOnArgumentClass {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param list {@link java.util.List} <p>The list parameter is <code>List</code> type.</p>
         * @see  java.util.List
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable
		public int foo(List<String> list) {
			return 0;
		}

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param t {@link java.lang.Throwable} <p>The t parameter is <code>Throwable</code> type.</p>
         * @param list {@link java.util.List} <p>The list parameter is <code>List</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.util.List
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable t, List<String> list) {
			return 1;
		}

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param t {@link java.lang.Throwable} <p>The t parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(Throwable t, String name) {
			return 2;
		}

	}

    /**
     * <code>RecoverByRetryableName</code>
     * <p>The recover by retryable name class.</p>
     * @see  org.springframework.retry.annotation.RecoverAnnotationRecoveryHandlerTests.RecoverByRetryableNameInterface
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class RecoverByRetryableName implements RecoverByRetryableNameInterface {

		public int foo(String name) {
			return 0;
		}

		public int fooRecover(Throwable throwable, String name) {
			return 1;
		}

		public int barRecover(Throwable throwable, String name) {
			return 2;
		}

	}

    /**
     * <code>RecoverByComposedRetryableAnnotationName</code>
     * <p>The recover by composed retryable annotation name class.</p>
     * @see  org.springframework.retry.annotation.RecoverAnnotationRecoveryHandlerTests.RecoverByComposedRetryableAnnotationNameInterface
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class RecoverByComposedRetryableAnnotationName
			implements RecoverByComposedRetryableAnnotationNameInterface {

		public int foo(String name) {
			return 0;
		}

		public int fooRecover(Throwable throwable, String name) {
			return 1;
		}

		public int barRecover(Throwable throwable, String name) {
			return 4;
		}

	}

    /**
     * <code>RecoverByRetryableNameInterface</code>
     * <p>The recover by retryable name interface interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected interface RecoverByRetryableNameInterface {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.Retryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @Retryable(recover = "barRecover")
		public int foo(String name);

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable throwable, String name);

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(Throwable throwable, String name);

	}

    /**
     * <code>RecoverByRetryableNameWithPrimitiveArgs</code>
     * <p>The recover by retryable name with primitive args class.</p>
     * @see  org.springframework.retry.annotation.RecoverAnnotationRecoveryHandlerTests.RecoverByRetryableNameWithPrimitiveArgsInterface
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected static class RecoverByRetryableNameWithPrimitiveArgs
			implements RecoverByRetryableNameWithPrimitiveArgsInterface {

		public int foo(int number) {
			return 0;
		}

		public int fooRecover(Throwable throwable, int number) {
			return 0;
		}

		public int barRecover(Throwable throwable, int number) {
			return number;
		}

	}

    /**
     * <code>RecoverByRetryableNameWithPrimitiveArgsInterface</code>
     * <p>The recover by retryable name with primitive args interface interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected interface RecoverByRetryableNameWithPrimitiveArgsInterface {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param number int <p>The number parameter is <code>int</code> type.</p>
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         * @see  org.springframework.retry.annotation.Retryable
         */
        @Retryable(recover = "barRecover")
		public int foo(int number);

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
         * @param number int <p>The number parameter is <code>int</code> type.</p>
         * @see  java.lang.Throwable
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable throwable, int number);

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
         * @param number int <p>The number parameter is <code>int</code> type.</p>
         * @see  java.lang.Throwable
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(Throwable throwable, int number);

	}

    /**
     * <code>RecoverByComposedRetryableAnnotationNameInterface</code>
     * <p>The recover by composed retryable annotation name interface interface.</p>
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    protected interface RecoverByComposedRetryableAnnotationNameInterface {

        /**
         * <code>foo</code>
         * <p>The foo method.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.retry.annotation.RecoverAnnotationRecoveryHandlerTests.ComposedRetryable
         * @return  int <p>The foo return object is <code>int</code> type.</p>
         */
        @ComposedRetryable(recover = "barRecover")
		public int foo(String name);

        /**
         * <code>fooRecover</code>
         * <p>The foo recover method.</p>
         * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The foo recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int fooRecover(Throwable throwable, String name);

        /**
         * <code>barRecover</code>
         * <p>The bar recover method.</p>
         * @param throwable {@link java.lang.Throwable} <p>The throwable parameter is <code>Throwable</code> type.</p>
         * @param name {@link java.lang.String} <p>The name parameter is <code>String</code> type.</p>
         * @see  java.lang.Throwable
         * @see  java.lang.String
         * @return  int <p>The bar recover return object is <code>int</code> type.</p>
         */
        @Recover
		public int barRecover(Throwable throwable, String name);

	}

    /**
     * <code>ComposedRetryable</code>
     * <p>The composed retryable interface.</p>
     * @see  java.lang.annotation.Annotation
     * @see  java.lang.annotation.Target
     * @see  java.lang.annotation.Retention
     * @see  java.lang.annotation.Documented
     * @see  org.springframework.retry.annotation.Retryable
     * @author  Cyan (snow22314@outlook.com)
     * @since Jdk1.8
     */
    @Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Retryable(maxAttempts = 4)
	public @interface ComposedRetryable {

        /**
         * <code>recover</code>
         * <p>The recover method.</p>
         * @return  {@link java.lang.String} <p>The recover return object is <code>String</code> type.</p>
         * @see  java.lang.String
         * @see  org.springframework.core.annotation.AliasFor
         */
        @AliasFor(annotation = Retryable.class, attribute = "recover")
		String recover() default "";

	}

}
