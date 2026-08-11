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

package org.springframework.retry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <code>AnyThrowTests</code>
 * <p>The any throw tests class.</p>
 * @author  Cyan (snow22314@outlook.com)
 * @since Jdk1.8
 */
public class AnyThrowTests {

    /**
     * <code>testRuntimeException</code>
     * <p>The test runtime exception method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testRuntimeException() {
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> AnyThrow.throwAny(new RuntimeException("planned")));
	}

    /**
     * <code>testUncheckedRuntimeException</code>
     * <p>The test unchecked runtime exception method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testUncheckedRuntimeException() {
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> AnyThrow.throwUnchecked(new RuntimeException("planned")));
	}

    /**
     * <code>testCheckedException</code>
     * <p>The test checked exception method.</p>
     * @see  org.junit.jupiter.api.Test
     */
    @Test
	public void testCheckedException() {
		assertThatExceptionOfType(Exception.class).isThrownBy(() -> AnyThrow.throwAny(new Exception("planned")));
	}

	private static class AnyThrow {

		private static void throwUnchecked(Throwable e) {
			AnyThrow.<RuntimeException>throwAny(e);
		}

		@SuppressWarnings("unchecked")
		private static <E extends Throwable> void throwAny(Throwable e) throws E {
			throw (E) e;
		}

	}

}
