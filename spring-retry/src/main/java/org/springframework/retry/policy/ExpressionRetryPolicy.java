/*
 * Copyright 2015-2025 the original author or authors.
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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.retry.RetryContext;
import org.springframework.util.Assert;

/**
 * <code>ExpressionRetryPolicy</code>
 * <p>The expression retry policy class.</p>
 * @see  org.springframework.retry.policy.SimpleRetryPolicy
 * @see  org.springframework.beans.factory.BeanFactoryAware
 * @see  java.lang.SuppressWarnings
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
@SuppressWarnings("serial")
public class ExpressionRetryPolicy extends SimpleRetryPolicy implements BeanFactoryAware {

	private static final Log logger = LogFactory.getLog(ExpressionRetryPolicy.class);

	private static final TemplateParserContext PARSER_CONTEXT = new TemplateParserContext();

	private final Expression expression;

	private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

    /**
     * <code>ExpressionRetryPolicy</code>
     * <p>Instantiates a new expression retry policy.</p>
     * @param expression {@link org.springframework.expression.Expression} <p>The expression parameter is <code>Expression</code> type.</p>
     * @see  org.springframework.expression.Expression
     */
    public ExpressionRetryPolicy(Expression expression) {
		Assert.notNull(expression, "'expression' cannot be null");
		this.expression = expression;
	}

    /**
     * <code>ExpressionRetryPolicy</code>
     * <p>Instantiates a new expression retry policy.</p>
     * @param expressionString {@link java.lang.String} <p>The expression string parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     */
    public ExpressionRetryPolicy(String expressionString) {
		Assert.notNull(expressionString, "'expressionString' cannot be null");
		this.expression = getExpression(expressionString);
	}

    /**
     * <code>ExpressionRetryPolicy</code>
     * <p>Instantiates a new expression retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @param retryableExceptions {@link java.util.Map} <p>The retryable exceptions parameter is <code>Map</code> type.</p>
     * @param traverseCauses boolean <p>The traverse causes parameter is <code>boolean</code> type.</p>
     * @param expression {@link org.springframework.expression.Expression} <p>The expression parameter is <code>Expression</code> type.</p>
     * @see  java.util.Map
     * @see  org.springframework.expression.Expression
     */
    public ExpressionRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses, Expression expression) {
		super(maxAttempts, retryableExceptions, traverseCauses);
		Assert.notNull(expression, "'expression' cannot be null");
		this.expression = expression;
	}

    /**
     * <code>ExpressionRetryPolicy</code>
     * <p>Instantiates a new expression retry policy.</p>
     * @param maxAttempts int <p>The max attempts parameter is <code>int</code> type.</p>
     * @param retryableExceptions {@link java.util.Map} <p>The retryable exceptions parameter is <code>Map</code> type.</p>
     * @param traverseCauses boolean <p>The traverse causes parameter is <code>boolean</code> type.</p>
     * @param expressionString {@link java.lang.String} <p>The expression string parameter is <code>String</code> type.</p>
     * @param defaultValue boolean <p>The default value parameter is <code>boolean</code> type.</p>
     * @see  java.util.Map
     * @see  java.lang.String
     */
    public ExpressionRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses, String expressionString, boolean defaultValue) {
		super(maxAttempts, retryableExceptions, traverseCauses, defaultValue);
		Assert.notNull(expressionString, "'expressionString' cannot be null");
		this.expression = getExpression(expressionString);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
	}

    /**
     * <code>withBeanFactory</code>
     * <p>The with bean factory method.</p>
     * @param beanFactory {@link org.springframework.beans.factory.BeanFactory} <p>The bean factory parameter is <code>BeanFactory</code> type.</p>
     * @see  org.springframework.beans.factory.BeanFactory
     * @return  {@link org.springframework.retry.policy.ExpressionRetryPolicy} <p>The with bean factory return object is <code>ExpressionRetryPolicy</code> type.</p>
     */
    public ExpressionRetryPolicy withBeanFactory(BeanFactory beanFactory) {
		setBeanFactory(beanFactory);
		return this;
	}

	@Override
	public boolean canRetry(RetryContext context) {
		Throwable lastThrowable = context.getLastThrowable();
		if (lastThrowable == null) {
			return super.canRetry(context);
		}
		else {
			return super.canRetry(context) && Boolean.TRUE
				.equals(this.expression.getValue(this.evaluationContext, lastThrowable, Boolean.class));
		}
	}

	private static Expression getExpression(String expression) {
		if (isTemplate(expression)) {
			logger.warn("#{...} syntax is not required for run-time expression in this policy "
					+ "and is deprecated in favor of a simple expression string."
					+ "Consider to remove SpEL template tokens around expression: '" + expression + "'");
			return new SpelExpressionParser().parseExpression(expression, PARSER_CONTEXT);
		}
		return new SpelExpressionParser().parseExpression(expression);
	}

    /**
     * <code>isTemplate</code>
     * <p>The is template method.</p>
     * @param expression {@link java.lang.String} <p>The expression parameter is <code>String</code> type.</p>
     * @see  java.lang.String
     * @return  boolean <p>The is template return object is <code>boolean</code> type.</p>
     */
    public static boolean isTemplate(String expression) {
		return expression.contains(PARSER_CONTEXT.getExpressionPrefix())
				&& expression.contains(PARSER_CONTEXT.getExpressionSuffix());
	}

}
