/*
 * Copyright 2016-2020 the original author or authors.
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

package org.springframework.amqp.rabbit.test.mockito;

import org.mockito.internal.stubbing.defaultanswers.ForwardsInvocations;
import org.mockito.invocation.InvocationOnMock;

import org.springframework.lang.Nullable;

/**
 * An Answer to optionally call the real method and allow returning a
 * custom result.
 *
 * @param <T> the return type.
 *
 * @author Gary Russell
 * @since 1.6
 *
 */
@SuppressWarnings("serial")
public class LambdaAnswer<T> extends ForwardsInvocations {

	private final boolean callRealMethod;

	private final ValueToReturn<T> callback;

	private final boolean hasDelegate;

	/**
	 * Deprecated.
	 * @param callRealMethod true to call the real method.
	 * @param callback the callback.
	 * @deprecated in favor of {@link #LambdaAnswer(boolean, ValueToReturn, Object)}.
	 */
	@Deprecated
	public LambdaAnswer(boolean callRealMethod, ValueToReturn<T> callback) {
		this(callRealMethod, callback, null);
	}

	/**
	 * Construct an instance with the provided properties. Use the test harness to get an
	 * instance with the proper delegate.
	 * @param callRealMethod true to call the real method.
	 * @param callback the call back to receive the result.
	 * @param delegate the delegate.
	 */
	public LambdaAnswer(boolean callRealMethod, ValueToReturn<T> callback, @Nullable Object delegate) {
		super(delegate);
		this.callRealMethod = callRealMethod;
		this.callback = callback;
		this.hasDelegate = delegate != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T answer(InvocationOnMock invocation) throws Throwable {
		T result = null;
		if (this.callRealMethod) {
			if (this.hasDelegate) {
				result = (T) super.answer(invocation);
			}
			else {
				result = (T) invocation.callRealMethod();
			}
		}
		return this.callback.apply(invocation, result);
	}

	public interface ValueToReturn<T> {

		T apply(InvocationOnMock invocation, T result);

	}

}
