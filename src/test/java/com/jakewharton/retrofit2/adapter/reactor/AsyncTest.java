/*
 * Copyright (C) 2017 Square, Inc.
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

import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import static okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AFTER_REQUEST;

public final class AsyncTest {
  @Rule public final MockWebServer server = new MockWebServer();

  interface Service {
    @GET("/") Flux<String> flux();
  }

  private Service service;

  @Before public void setUp() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(new StringConverterFactory())
        .addCallAdapterFactory(ReactorCallAdapterFactory.createAsync())
        .build();
    service = retrofit.create(Service.class);
  }

  @Test public void success() {
    server.enqueue(new MockResponse().setBody("hey"));

    StepVerifier.create(service.flux())
        .expectNext("hey")
        .expectComplete()
        .verify(Duration.ofSeconds(1));
  }


  @Test public void failure() {
    server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AFTER_REQUEST));

    StepVerifier.create(service.flux())
        .expectError(IOException.class)
        .verify(Duration.ofSeconds(1));
  }
}
