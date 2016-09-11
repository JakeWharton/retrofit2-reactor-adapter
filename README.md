Retrofit 2 Project Reactor Adapter
==================================

An Project Reactor `CallAdapter.Factory` implementation for Retrofit 2.



Usage
-----

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.example.com")
    .addCallAdapterFactory(ReactorCallAdapterFactory.create())
    .build();
```

Available types:

 * `Flux<T>`, `Flux<Response<T>>`, and `Flux<Result<T>>` where `T` is the body type.
 * `Mono<T>`, `Mono<Response<T>>` and `Mono<Result<T>>` where `T` is the body type.



Download
--------

Note: No stable release is available at this time.

Gradle:
```groovy
compile 'com.jakewharton.retrofit:retrofit2-reactor-adapter:1.0.0-SNAPSHOT'
```
or Maven:
```xml
<dependency>
  <groupId>com.jakewharton.retrofit</groupId>
  <artifactId>retrofit2-reactor-adapter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Snapshot versions are available in the Sonatype 'snapshots' repository: https://oss.sonatype.org/content/repositories/snapshots/



License
-------

    Copyright 2016 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
