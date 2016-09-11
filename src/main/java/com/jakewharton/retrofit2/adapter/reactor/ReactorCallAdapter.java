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

import java.lang.reflect.Type;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

import static reactor.core.publisher.FluxSink.OverflowStrategy.LATEST;

final class ReactorCallAdapter implements CallAdapter<Object> {
  private final Type responseType;
  private final Scheduler scheduler;
  private final boolean isResult;
  private final boolean isBody;
  private final boolean isMono;

  ReactorCallAdapter(Type responseType, Scheduler scheduler, boolean isResult, boolean isBody,
      boolean isMono) {
    this.responseType = responseType;
    this.scheduler = scheduler;
    this.isResult = isResult;
    this.isBody = isBody;
    this.isMono = isMono;
  }

  @Override public Type responseType() {
    return responseType;
  }

  @Override public <R> Object adapt(Call<R> call) {
    Flux<Response<R>> responseFlux = Flux.create(new CallSinkConsumer<>(call), LATEST);

    Flux<?> flux;
    if (isResult) {
      flux = new ResultFlux<>(responseFlux);
    } else if (isBody) {
      flux = new BodyFlux<>(responseFlux);
    } else {
      flux = responseFlux;
    }

    if (scheduler != null) {
      flux = flux.subscribeOn(scheduler);
    }

    if (isMono) {
      return flux.single();
    }
    return flux;
  }
}
