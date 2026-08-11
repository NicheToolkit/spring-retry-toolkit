/*
 * Copyright 2006-2025 the original author or authors.
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

package org.springframework.classify.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.aop.framework.Advised;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/**
 * <code>MethodInvokerUtils</code>
 * <p>The method invoker utils class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class MethodInvokerUtils {

    /**
     * <code>getMethodInvokerByName</code>
     * <p>The get method invoker by name getter method.</p>
     * @param object {@link java.lang.Object} <p>The object parameter is <code>Object</code> type.</p>
     * @param methodName {@link java.lang.String} <p>The method name parameter is <code>String</code> type.</p>
     * @param paramsRequired boolean <p>The params required parameter is <code>boolean</code> type.</p>
     * @param paramTypes {@link java.lang.Class} <p>The param types parameter is <code>Class</code> type.</p>
     * @see  java.lang.Object
     * @see  java.lang.String
     * @see  java.lang.Class
     * @return  {@link org.springframework.classify.util.MethodInvoker} <p>The get method invoker by name return object is <code>MethodInvoker</code> type.</p>
     */
    public static MethodInvoker getMethodInvokerByName(Object object, String methodName, boolean paramsRequired,
			Class<?>... paramTypes) {
		Assert.notNull(object, "Object to invoke must not be null");
		Method method = ClassUtils.getMethodIfAvailable(object.getClass(), methodName, paramTypes);
		if (method == null) {
			String errorMsg = "no method found with name [" + methodName + "] on class ["
					+ object.getClass().getSimpleName() + "] compatible with the signature ["
					+ getParamTypesString(paramTypes) + "].";
			Assert.isTrue(!paramsRequired, errorMsg);
			// if no method was found for the given parameters, and the
			// parameters aren't required, then try with no params
			method = ClassUtils.getMethodIfAvailable(object.getClass(), methodName, new Class[] {});
			Assert.notNull(method, errorMsg);
		}
		return new SimpleMethodInvoker(object, method);
	}

    /**
     * <code>getParamTypesString</code>
     * <p>The get param types string getter method.</p>
     * @param paramTypes {@link java.lang.Class} <p>The param types parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.String
     * @return  {@link java.lang.String} <p>The get param types string return object is <code>String</code> type.</p>
     */
    public static String getParamTypesString(Class<?>... paramTypes) {
		StringBuilder paramTypesList = new StringBuilder("(");
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypesList.append(paramTypes[i].getSimpleName());
			if (i + 1 < paramTypes.length) {
				paramTypesList.append(", ");
			}
		}
		return paramTypesList.append(")").toString();
	}

    /**
     * <code>getMethodInvokerForInterface</code>
     * <p>The get method invoker for interface getter method.</p>
     * @param cls {@link java.lang.Class} <p>The cls parameter is <code>Class</code> type.</p>
     * @param methodName {@link java.lang.String} <p>The method name parameter is <code>String</code> type.</p>
     * @param object {@link java.lang.Object} <p>The object parameter is <code>Object</code> type.</p>
     * @param paramTypes {@link java.lang.Class} <p>The param types parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.String
     * @see  java.lang.Object
     * @return  {@link org.springframework.classify.util.MethodInvoker} <p>The get method invoker for interface return object is <code>MethodInvoker</code> type.</p>
     */
    public static MethodInvoker getMethodInvokerForInterface(Class<?> cls, String methodName, Object object,
			Class<?>... paramTypes) {

		if (cls.isAssignableFrom(object.getClass())) {
			return MethodInvokerUtils.getMethodInvokerByName(object, methodName, true, paramTypes);
		}
		else {
			return null;
		}
	}

    /**
     * <code>getMethodInvokerByAnnotation</code>
     * <p>The get method invoker by annotation getter method.</p>
     * @param annotationType {@link java.lang.Class} <p>The annotation type parameter is <code>Class</code> type.</p>
     * @param target {@link java.lang.Object} <p>The target parameter is <code>Object</code> type.</p>
     * @param expectedParamTypes {@link java.lang.Class} <p>The expected param types parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.Object
     * @return  {@link org.springframework.classify.util.MethodInvoker} <p>The get method invoker by annotation return object is <code>MethodInvoker</code> type.</p>
     */
    public static MethodInvoker getMethodInvokerByAnnotation(final Class<? extends Annotation> annotationType,
			final Object target, final Class<?>... expectedParamTypes) {
		MethodInvoker mi = MethodInvokerUtils.getMethodInvokerByAnnotation(annotationType, target);
		final Class<?> targetClass = (target instanceof Advised) ? ((Advised) target).getTargetSource().getTargetClass()
				: target.getClass();
		if (mi != null) {
			ReflectionUtils.doWithMethods(targetClass, method -> {
				Annotation annotation = AnnotationUtils.findAnnotation(method, annotationType);
				if (annotation != null) {
					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes.length > 0) {
						String errorMsg = "The method [" + method.getName() + "] on target class ["
								+ targetClass.getSimpleName() + "] is compatible with the signature ["
								+ getParamTypesString(expectedParamTypes) + "] expected for the annotation ["
								+ annotationType.getSimpleName() + "].";

						Assert.isTrue(paramTypes.length == expectedParamTypes.length, errorMsg);
						for (int i = 0; i < paramTypes.length; i++) {
							Assert.isTrue(expectedParamTypes[i].isAssignableFrom(paramTypes[i]), errorMsg);
						}
					}
				}
			});
		}
		return mi;
	}

    /**
     * <code>getMethodInvokerByAnnotation</code>
     * <p>The get method invoker by annotation getter method.</p>
     * @param annotationType {@link java.lang.Class} <p>The annotation type parameter is <code>Class</code> type.</p>
     * @param target {@link java.lang.Object} <p>The target parameter is <code>Object</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.Object
     * @return  {@link org.springframework.classify.util.MethodInvoker} <p>The get method invoker by annotation return object is <code>MethodInvoker</code> type.</p>
     */
    public static MethodInvoker getMethodInvokerByAnnotation(final Class<? extends Annotation> annotationType,
			final Object target) {
		Assert.notNull(target, "Target must not be null");
		Assert.notNull(annotationType, "AnnotationType must not be null");
		Assert.isTrue(
				ObjectUtils.containsElement(annotationType.getAnnotation(Target.class).value(), ElementType.METHOD),
				"Annotation [" + annotationType + "] is not a Method-level annotation.");
		final Class<?> targetClass = (target instanceof Advised) ? ((Advised) target).getTargetSource().getTargetClass()
				: target.getClass();
		if (targetClass == null) {
			// Proxy with no target cannot have annotations
			return null;
		}
		final AtomicReference<Method> annotatedMethod = new AtomicReference<>();
		ReflectionUtils.doWithMethods(targetClass, method -> {
			Annotation annotation = AnnotationUtils.findAnnotation(method, annotationType);
			if (annotation != null) {
				Assert.isNull(annotatedMethod.get(),
						"found more than one method on target class [" + targetClass.getSimpleName()
								+ "] with the annotation type [" + annotationType.getSimpleName() + "].");
				annotatedMethod.set(method);
			}
		});
		Method method = annotatedMethod.get();
		if (method == null) {
			return null;
		}
		else {
			return new SimpleMethodInvoker(target, annotatedMethod.get());
		}
	}

    /**
     * <code>getMethodInvokerForSingleArgument</code>
     * <p>The get method invoker for single argument getter method.</p>
     * @param <C>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param <T>  {@link java.lang.Object} <p>The parameter can be of any type.</p>
     * @param target {@link java.lang.Object} <p>The target parameter is <code>Object</code> type.</p>
     * @see  java.lang.Object
     * @return  {@link org.springframework.classify.util.MethodInvoker} <p>The get method invoker for single argument return object is <code>MethodInvoker</code> type.</p>
     */
    public static <C, T> MethodInvoker getMethodInvokerForSingleArgument(Object target) {
		final AtomicReference<Method> methodHolder = new AtomicReference<>();
		ReflectionUtils.doWithMethods(target.getClass(), method -> {
			if ((method.getModifiers() & Modifier.PUBLIC) == 0 || method.isBridge()) {
				return;
			}
			if (method.getParameterTypes() == null || method.getParameterTypes().length != 1) {
				return;
			}
			if (method.getReturnType().equals(Void.TYPE) || ReflectionUtils.isEqualsMethod(method)) {
				return;
			}
			Assert.state(methodHolder.get() == null,
					"More than one non-void public method detected with single argument.");
			methodHolder.set(method);
		});
		Method method = methodHolder.get();
		return new SimpleMethodInvoker(target, method);
	}

}
