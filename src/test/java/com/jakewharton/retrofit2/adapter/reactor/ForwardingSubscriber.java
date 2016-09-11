/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jakewharton.retrofit2.adapter.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

abstract class ForwardingSubscriber<T> implements Subscriber<T> {
  private final Subscriber<T> delegate;

  ForwardingSubscriber(Subscriber<T> delegate) {
    this.delegate = delegate;
  }

  @Override public void onSubscribe(Subscription s) {
    delegate.onSubscribe(s);
  }

  @Override public void onNext(T value) {
    delegate.onNext(value);
  }

  @Override public void onComplete() {
    delegate.onComplete();
  }

  @Override public void onError(Throwable throwable) {
    delegate.onError(throwable);
  }
}
