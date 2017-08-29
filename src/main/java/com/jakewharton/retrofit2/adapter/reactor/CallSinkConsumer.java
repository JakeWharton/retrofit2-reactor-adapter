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

import reactor.core.publisher.FluxSink;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.function.Consumer;

final class CallSinkConsumer<T> implements Consumer<FluxSink<Response<T>>> {
  private final Call<T> originalCall;

  CallSinkConsumer(Call<T> originalCall) {
    this.originalCall = originalCall;
  }

  @Override public void accept(FluxSink<Response<T>> sink) {
    // Since Call is a one-shot type, clone it for each new subscriber.
    Call<T> call = originalCall.clone();

    sink.onDispose(call::cancel);

    Response<T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      sink.error(e);
      return;
    }
    sink.next(response);
    sink.complete();
  }
}
