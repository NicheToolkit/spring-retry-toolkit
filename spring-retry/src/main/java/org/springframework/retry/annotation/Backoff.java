/*
 * Copyright 2012-2022 the original author or authors.
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
import org.springframework.retry.backoff.BackOffPolicy;

/**
 * <code>Backoff</code>
 * <p>The backoff interface.</p>
 * @see  java.lang.annotation.Annotation
 * @see  java.lang.annotation.Target
 * @see  java.lang.annotation.Retention
 * @see  java.lang.annotation.Documented
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Backoff {

    /**
     * <code>value</code>
     * <p>The value method.</p>
     * @return  long <p>The value return object is <code>long</code> type.</p>
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor("delay")
	long value() default 1000;

    /**
     * <code>delay</code>
     * <p>The delay method.</p>
     * @return  long <p>The delay return object is <code>long</code> type.</p>
     * @see  org.springframework.core.annotation.AliasFor
     */
    @AliasFor("value")
	long delay() default 1000;

    /**
     * <code>maxDelay</code>
     * <p>The max delay method.</p>
     * @return  long <p>The max delay return object is <code>long</code> type.</p>
     */
    long maxDelay() default 0;

    /**
     * <code>multiplier</code>
     * <p>The multiplier method.</p>
     * @return  double <p>The multiplier return object is <code>double</code> type.</p>
     */
    double multiplier() default 0;

    /**
     * <code>delayExpression</code>
     * <p>The delay expression method.</p>
     * @return  {@link java.lang.String} <p>The delay expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String delayExpression() default "";

    /**
     * <code>maxDelayExpression</code>
     * <p>The max delay expression method.</p>
     * @return  {@link java.lang.String} <p>The max delay expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String maxDelayExpression() default "";

    /**
     * <code>multiplierExpression</code>
     * <p>The multiplier expression method.</p>
     * @return  {@link java.lang.String} <p>The multiplier expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String multiplierExpression() default "";

    /**
     * <code>random</code>
     * <p>The random method.</p>
     * @return  boolean <p>The random return object is <code>boolean</code> type.</p>
     */
    boolean random() default false;

    /**
     * <code>randomExpression</code>
     * <p>The random expression method.</p>
     * @return  {@link java.lang.String} <p>The random expression return object is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    String randomExpression() default "";

}
