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
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Operators;
import retrofit2.Response;

final class BodyFlux<T> extends Flux<T> {
  private final Publisher<Response<T>> upstream;

  BodyFlux(Publisher<Response<T>> upstream) {
    this.upstream = upstream;
  }

  @Override
  public void subscribe(CoreSubscriber<? super T> coreSubscriber) {
    upstream.subscribe(new BodySubscriber<>(coreSubscriber));
  }

  private static class BodySubscriber<R> implements Subscriber<Response<R>> {
    private final Subscriber<? super R> subscriber;
    /** Indicates whether a terminal event has been sent to {@link #subscriber}. */
    private boolean subscriberTerminated;

    BodySubscriber(Subscriber<? super R> subscriber) {
      this.subscriber = subscriber;
    }

    @Override public void onSubscribe(Subscription subscription) {
      subscriber.onSubscribe(subscription);
    }

    @Override public void onNext(Response<R> response) {
      if (response.isSuccessful()) {
        subscriber.onNext(response.body());
      } else {
        subscriberTerminated = true;
        Throwable t = new HttpException(response);
        try {
          subscriber.onError(t);
        } catch (Throwable inner) {
          Operators.onErrorDropped(inner, t);
        }
      }
    }

    @Override public void onError(Throwable throwable) {
      if (!subscriberTerminated) {
        subscriber.onError(throwable);
      } else {
        // This should never happen! onNext handles and forwards errors automatically.
        Throwable broken = new AssertionError(
            "This should never happen! Report as a Retrofit bug with the full stacktrace.",
            throwable);
        Operators.onErrorDropped(broken);
      }
    }

    @Override public void onComplete() {
      if (!subscriberTerminated) {
        subscriber.onComplete();
      }
    }
  }
}
