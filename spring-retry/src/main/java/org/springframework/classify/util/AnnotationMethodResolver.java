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

package org.springframework.classify.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/**
 * <code>AnnotationMethodResolver</code>
 * <p>The annotation method resolver class.</p>
 * @see  org.springframework.classify.util.MethodResolver
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class AnnotationMethodResolver implements MethodResolver {

	private final Class<? extends Annotation> annotationType;

    /**
     * <code>AnnotationMethodResolver</code>
     * <p>Instantiates a new annotation method resolver.</p>
     * @param annotationType {@link java.lang.Class} <p>The annotation type parameter is <code>Class</code> type.</p>
     * @see  java.lang.Class
     */
    public AnnotationMethodResolver(Class<? extends Annotation> annotationType) {
		Assert.notNull(annotationType, "annotationType must not be null");
		Assert.isTrue(
				ObjectUtils.containsElement(annotationType.getAnnotation(Target.class).value(), ElementType.METHOD),
				"Annotation [" + annotationType + "] is not a Method-level annotation.");
		this.annotationType = annotationType;
	}

	public Method findMethod(Object candidate) {
		Assert.notNull(candidate, "candidate object must not be null");
		Class<?> targetClass = AopUtils.getTargetClass(candidate);
		if (targetClass == null) {
			targetClass = candidate.getClass();
		}
		return this.findMethod(targetClass);
	}

	public Method findMethod(final Class<?> clazz) {
		Assert.notNull(clazz, "class must not be null");
		final AtomicReference<Method> annotatedMethod = new AtomicReference<>();
		ReflectionUtils.doWithMethods(clazz, method -> {
			Annotation annotation = AnnotationUtils.findAnnotation(method, annotationType);
			if (annotation != null) {
				Assert.isNull(annotatedMethod.get(), "found more than one method on target class [" + clazz
						+ "] with the annotation type [" + annotationType + "]");
				annotatedMethod.set(method);
			}
		});
		return annotatedMethod.get();
	}

}
