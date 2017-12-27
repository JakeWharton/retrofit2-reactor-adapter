/*
 * Copyright (C) 2015 Square, Inc.
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

import com.google.common.reflect.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public final class ReactorCallAdapterFactoryTest {
  private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

  private final CallAdapter.Factory factory = ReactorCallAdapterFactory.create();
  private Retrofit retrofit;

  @Before public void setUp() {
    retrofit = new Retrofit.Builder()
        .baseUrl("http://localhost:1")
        .addConverterFactory(new StringConverterFactory())
        .addCallAdapterFactory(factory)
        .build();
  }

  @Test public void nullSchedulerThrows() {
    try {
      ReactorCallAdapterFactory.createWithScheduler(null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e).hasMessageThat().isEqualTo("scheduler == null");
    }
  }

  @Test public void nonReactorTypeReturnsNull() {
    CallAdapter<?, ?> adapter = factory.get(String.class, NO_ANNOTATIONS, retrofit);
    assertThat(adapter).isNull();
  }

  @Test public void responseTypes() {
    Type oBodyClass = new TypeToken<Flux<String>>() {}.getType();
    assertThat(factory.get(oBodyClass, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
    Type sBodyClass = new TypeToken<Mono<String>>() {}.getType();
    assertThat(factory.get(sBodyClass, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);

    Type oBodyWildcard = new TypeToken<Flux<? extends String>>() {}.getType();
    assertThat(factory.get(oBodyWildcard, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
    Type sBodyWildcard = new TypeToken<Mono<? extends String>>() {}.getType();
    assertThat(factory.get(sBodyWildcard, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);

    Type oBodyGeneric = new TypeToken<Flux<List<String>>>() {}.getType();
    assertThat(factory.get(oBodyGeneric, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(new TypeToken<List<String>>() {}.getType());
    Type sBodyGeneric = new TypeToken<Mono<List<String>>>() {}.getType();
    assertThat(factory.get(sBodyGeneric, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(new TypeToken<List<String>>() {}.getType());

    Type oResponseClass = new TypeToken<Flux<Response<String>>>() {}.getType();
    assertThat(factory.get(oResponseClass, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
    Type sResponseClass = new TypeToken<Mono<Response<String>>>() {}.getType();
    assertThat(factory.get(sResponseClass, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);

    Type oResponseWildcard = new TypeToken<Flux<Response<? extends String>>>() {}.getType();
    assertThat(factory.get(oResponseWildcard, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
    Type sResponseWildcard = new TypeToken<Mono<Response<? extends String>>>() {}.getType();
    assertThat(factory.get(sResponseWildcard, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);

    Type oResultClass = new TypeToken<Flux<Result<String>>>() {}.getType();
    assertThat(factory.get(oResultClass, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
    Type sResultClass = new TypeToken<Mono<Result<String>>>() {}.getType();
    assertThat(factory.get(sResultClass, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);

    Type oResultWildcard = new TypeToken<Flux<Result<? extends String>>>() {}.getType();
    assertThat(factory.get(oResultWildcard, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
    Type sResultWildcard = new TypeToken<Mono<Result<? extends String>>>() {}.getType();
    assertThat(factory.get(sResultWildcard, NO_ANNOTATIONS, retrofit).responseType())
        .isEqualTo(String.class);
  }

  @Test public void rawBodyTypeThrows() {
    Type observableType = new TypeToken<Flux>() {}.getType();
    try {
      factory.get(observableType, NO_ANNOTATIONS, retrofit);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Flux return type must be parameterized as Flux<Foo> or Flux<? extends Foo>");
    }

    Type singleType = new TypeToken<Mono>() {}.getType();
    try {
      factory.get(singleType, NO_ANNOTATIONS, retrofit);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Mono return type must be parameterized as Mono<Foo> or Mono<? extends Foo>");
    }
  }

  @Test public void rawResponseTypeThrows() {
    Type observableType = new TypeToken<Flux<Response>>() {}.getType();
    try {
      factory.get(observableType, NO_ANNOTATIONS, retrofit);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>");
    }

    Type singleType = new TypeToken<Mono<Response>>() {}.getType();
    try {
      factory.get(singleType, NO_ANNOTATIONS, retrofit);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Response must be parameterized as Response<Foo> or Response<? extends Foo>");
    }
  }

  @Test public void rawResultTypeThrows() {
    Type observableType = new TypeToken<Flux<Result>>() {}.getType();
    try {
      factory.get(observableType, NO_ANNOTATIONS, retrofit);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>");
    }

    Type singleType = new TypeToken<Mono<Result>>() {}.getType();
    try {
      factory.get(singleType, NO_ANNOTATIONS, retrofit);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Result must be parameterized as Result<Foo> or Result<? extends Foo>");
    }
  }
}
