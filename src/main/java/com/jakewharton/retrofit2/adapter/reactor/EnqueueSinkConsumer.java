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

import java.util.function.Consumer;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class EnqueueSinkConsumer<T> implements Consumer<FluxSink<Response<T>>> {
  private final Call<T> originalCall;

  EnqueueSinkConsumer(Call<T> originalCall) {
    this.originalCall = originalCall;
  }

  @Override public void accept(FluxSink<Response<T>> sink) {
    // Since Call is a one-shot type, clone it for each new subscriber.
    Call<T> call = originalCall.clone();

    DisposableCallback<T> callback = new DisposableCallback<>(call, sink);
    sink.onDispose(callback);
    call.enqueue(callback);
  }

  static final class DisposableCallback<T> implements Callback<T>, Disposable {
    private final Call<T> call;
    private final FluxSink<Response<T>> sink;

    DisposableCallback(Call<T> call, FluxSink<Response<T>> sink) {
      this.call = call;
      this.sink = sink;
    }

    @Override public void onResponse(Call<T> call, Response<T> response) {
      sink.next(response);
      sink.complete();
    }

    @Override public void onFailure(Call<T> call, Throwable t) {
      sink.error(t);
    }

    @Override public void dispose() {
      call.cancel();
    }

    @Override public boolean isDisposed() {
      return call.isCanceled();
    }
  }
}
