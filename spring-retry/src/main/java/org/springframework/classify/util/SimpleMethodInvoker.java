/*
 * Copyright 2014 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.aop.framework.Advised;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * <code>SimpleMethodInvoker</code>
 * <p>The simple method invoker class.</p>
 * @see  org.springframework.classify.util.MethodInvoker
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class SimpleMethodInvoker implements MethodInvoker {

	private final Object object;

	private final Method method;

	private final Class<?>[] parameterTypes;

	private volatile Object target;

    /**
     * <code>SimpleMethodInvoker</code>
     * <p>Instantiates a new simple method invoker.</p>
     * @param object {@link java.lang.Object} <p>The object parameter is <code>Object</code> type.</p>
     * @param method {@link java.lang.reflect.Method} <p>The method parameter is <code>Method</code> type.</p>
     * @see  java.lang.Object
     * @see  java.lang.reflect.Method
     */
    public SimpleMethodInvoker(Object object, Method method) {
		Assert.notNull(object, "Object to invoke must not be null");
		Assert.notNull(method, "Method to invoke must not be null");
		this.method = method;
		method.setAccessible(true);
		this.object = object;
		this.parameterTypes = method.getParameterTypes();
	}

    /**
     * <code>SimpleMethodInvoker</code>
     * <p>Instantiates a new simple method invoker.</p>
     * @param object {@link java.lang.Object} <p>The object parameter is <code>Object</code> type.</p>
     * @param methodName {@link java.lang.String} <p>The method name parameter is <code>String</code> type.</p>
     * @param paramTypes {@link java.lang.Class} <p>The param types parameter is <code>Class</code> type.</p>
     * @see  java.lang.Object
     * @see  java.lang.String
     * @see  java.lang.Class
     */
    public SimpleMethodInvoker(Object object, String methodName, Class<?>... paramTypes) {
		Assert.notNull(object, "Object to invoke must not be null");
		Method method = ClassUtils.getMethodIfAvailable(object.getClass(), methodName, paramTypes);
		if (method == null) {
			// try with no params
			method = ClassUtils.getMethodIfAvailable(object.getClass(), methodName, new Class[] {});
		}

		Assert.notNull(method, "No methods found for name: [" + methodName + "] in class: [" + object.getClass()
				+ "] with arguments of type: [" + Arrays.toString(paramTypes) + "]");

		this.object = object;
		this.method = method;
		method.setAccessible(true);
		this.parameterTypes = method.getParameterTypes();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.core.configuration.util.MethodInvoker#invokeMethod
	 * (java.lang.Object[])
	 */
	@Override
	public Object invokeMethod(Object... args) {
		Assert.state(this.parameterTypes.length == args.length,
				"Wrong number of arguments, expected no more than: [" + this.parameterTypes.length + "]");

		try {
			// Extract the target from an Advised as late as possible
			// in case it contains a lazy initialization
			Object target = extractTarget(this.object, this.method);
			return method.invoke(target, args);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to invoke method: [" + this.method + "] on object: ["
					+ this.object + "] with arguments: [" + Arrays.toString(args) + "]", e);
		}
	}

	private Object extractTarget(Object target, Method method) {
		if (this.target == null) {
			if (target instanceof Advised) {
				Object source;
				try {
					source = ((Advised) target).getTargetSource().getTarget();
				}
				catch (Exception e) {
					throw new IllegalStateException("Could not extract target from proxy", e);
				}
				if (source instanceof Advised) {
					source = extractTarget(source, method);
				}
				if (method.getDeclaringClass().isAssignableFrom(source.getClass())) {
					target = source;
				}
			}
			this.target = target;

		}
		return this.target;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleMethodInvoker)) {
			return false;
		}

		if (obj == this) {
			return true;
		}
		SimpleMethodInvoker rhs = (SimpleMethodInvoker) obj;
		return (rhs.method.equals(this.method)) && (rhs.object.equals(this.object));
	}

	@Override
	public int hashCode() {
		int result = 25;
		result = 31 * result + this.object.hashCode();
		result = 31 * result + this.method.hashCode();
		return result;
	}

}
