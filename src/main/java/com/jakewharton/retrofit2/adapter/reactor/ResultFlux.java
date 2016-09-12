/*
 * Copyright (C) 2016 Jake Wharton
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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Operators;
import retrofit2.Response;

final class ResultFlux<T> extends Flux<Result<T>> {
  private final Publisher<Response<T>> upstream;

  ResultFlux(Publisher<Response<T>> upstream) {
    this.upstream = upstream;
  }

  @Override public void subscribe(Subscriber<? super Result<T>> subscriber) {
    upstream.subscribe(new ResultSubscriber<>(subscriber));
  }

  private static class ResultSubscriber<R> implements Subscriber<Response<R>> {
    private final Subscriber<? super Result<R>> subscriber;

    ResultSubscriber(Subscriber<? super Result<R>> subscriber) {
      this.subscriber = subscriber;
    }

    @Override public void onSubscribe(Subscription s) {
      subscriber.onSubscribe(s);
    }

    @Override public void onNext(Response<R> response) {
      subscriber.onNext(Result.response(response));
    }

    @Override public void onError(Throwable throwable) {
      try {
        subscriber.onNext(Result.<R>error(throwable));
      } catch (Throwable t) {
        try {
          subscriber.onError(t);
        } catch (Throwable inner) {
          Operators.onErrorDropped(inner, t);
        }
        return;
      }
      subscriber.onComplete();
    }

    @Override public void onComplete() {
      subscriber.onComplete();
    }
  }
}
