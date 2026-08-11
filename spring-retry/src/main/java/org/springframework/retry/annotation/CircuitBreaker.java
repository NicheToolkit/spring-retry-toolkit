/*
 * Copyright 2016-2024 the original author or authors.
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

import org.springframework.core.annotation.AliasFor;

/**
 * <code>CircuitBreaker</code>
 * <p>The circuit breaker interface.</p>
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
@Retryable(stateful = true)
public @interface CircuitBreaker {

    /**
     * <code>value</code>
     * <p>The value method.</p>
     * @deprecated  <p>The value method has be deprecated.</p>
     * @return  {@link java.lang.Class} <p>The value return object is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  org.springframework.core.annotation.AliasFor
     * @see  java.lang.Deprecated
     */
    @AliasFor(annotation = Retryable.class)
	@Deprecated
	Class<? extends Throwable>[] value() default {};

    /**
     * <code>include</code>
     * <p>The include method.</p>
     * @deprecated  <p>The include method has be deprecated.</p>
     * @return  {@link java.lang.Class} <p>The include return object is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  org.springframework.core.annotation.AliasFor
     * @see  java.lang.Deprecated
     */
    @AliasFor(annotation = Retryable.class)
	@Deprecated
	Class<? extends Throwable>[] include() default {};

    /**
     * <code>retryFor</code>
     * <p>The retry for method.</p>
     * @return  {@link java.lang.Class} <p>The retry for return object is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	Class<? extends Throwable>[] retryFor() default {};

    /**
     * <code>exclude</code>
     * <p>The exclude method.</p>
     * @deprecated  <p>The exclude method has be deprecated.</p>
     * @return  {@link java.lang.Class} <p>The exclude return object is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  java.lang.Deprecated
     * @see  org.springframework.core.annotation.AliasFor
     */
    @Deprecated
	@AliasFor(annotation = Retryable.class)
	Class<? extends Throwable>[] exclude() default {};

    /**
     * <code>noRetryFor</code>
     * <p>The no retry for method.</p>
     * @return  {@link java.lang.Class} <p>The no retry for return object is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	Class<? extends Throwable>[] noRetryFor() default {};

    /**
     * <code>notRecoverable</code>
     * <p>The not recoverable method.</p>
     * @return  {@link java.lang.Class} <p>The not recoverable return object is <code>Class</code> type.</p>
     * @see  java.lang.Class
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	Class<? extends Throwable>[] notRecoverable() default {};

    /**
     * <code>maxAttempts</code>
     * <p>The max attempts method.</p>
     * @return  int <p>The max attempts return object is <code>int</code> type.</p>
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	int maxAttempts() default 3;

    /**
     * <code>maxAttemptsExpression</code>
     * <p>The max attempts expression method.</p>
     * @return  {@link java.lang.String} <p>The max attempts expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	String maxAttemptsExpression() default "";

    /**
     * <code>label</code>
     * <p>The label method.</p>
     * @return  {@link java.lang.String} <p>The label return object is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	String label() default "";

    /**
     * <code>resetTimeout</code>
     * <p>The reset timeout method.</p>
     * @return  long <p>The reset timeout return object is <code>long</code> type.</p>
     */
    long resetTimeout() default 20000;

    /**
     * <code>resetTimeoutExpression</code>
     * <p>The reset timeout expression method.</p>
     * @return  {@link java.lang.String} <p>The reset timeout expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String resetTimeoutExpression() default "";

    /**
     * <code>openTimeout</code>
     * <p>The open timeout method.</p>
     * @return  long <p>The open timeout return object is <code>long</code> type.</p>
     */
    long openTimeout() default 5000;

    /**
     * <code>openTimeoutExpression</code>
     * <p>The open timeout expression method.</p>
     * @return  {@link java.lang.String} <p>The open timeout expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String openTimeoutExpression() default "";

    /**
     * <code>exceptionExpression</code>
     * <p>The exception expression method.</p>
     * @return  {@link java.lang.String} <p>The exception expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	String exceptionExpression() default "";

    /**
     * <code>throwLastExceptionOnExhausted</code>
     * <p>The throw last exception on exhausted method.</p>
     * @return  boolean <p>The throw last exception on exhausted return object is <code>boolean</code> type.</p>
     */
    boolean throwLastExceptionOnExhausted() default false;

    /**
     * <code>recover</code>
     * <p>The recover method.</p>
     * @return  {@link java.lang.String} <p>The recover return object is <code>String</code> type.</p>
     * @see  java.lang.String
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor(annotation = Retryable.class)
	String recover() default "";

}
