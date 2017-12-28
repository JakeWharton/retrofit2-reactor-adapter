Change Log
==========

Version 2.1.0 *(2017-12-28)*
----------------------------

 * New: `createAsync()` factory function produces `Flux` and `Mono` instances which use OkHttp's native async threading to execute requests. Calling `subscribeOn` on the returned instances will have no effect.


Version 2.0.0 *(2017-12-27)*
----------------------------

 * New: Update to Reactor 3.1.2. Note: This is incompatible with Reactor 3.0.x!
 * New: Update to Retrofit 2.3.0. This drops the custom `HttpException` in favor of the standard one inside Retrofit.


Version 1.0.0 *(2016-09-14)*
----------------------------

Initial version. Supports Project Reactor 3.0.2.RELEASE.
