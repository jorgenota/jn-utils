# Jn Utils

## Libraries

Some utility libraries to deal with objects, strings, functions and retries:
* [jn-base](jn-base/README.md)
* [jn-function](jn-function/README.md)

A BOM is provided to manage all your dependencies on any Jn Utils library and to make
sure Maven picks the compatible versions when depending on multiple Jn Utils libraries.
* [jn-bom](jn-bom/README.md)

## Acknowledgements

I've taken some code and ideas from different Github repositories so I'm linking to that
sources and I'd like to thank the authors.

In jn-base:
* Preconditions is a simpler version of Guava's 
[Preconditions](https://github.com/google/guava/blob/master/guava/src/com/google/common/base/Preconditions.java) class.
* StringUtils and ObjectUtils contain minimal functionality from Spring's 
[StringUtils](https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/StringUtils.java)
and [ObjectUtils](https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/ObjectUtils.java).

In jn-function:
* The XXXWithException clases are inspired by this gist:
[LambdaExceptionUtil.java](https://gist.github.com/jomoespe/ea5c21722b693c09c38bf6286226cd92)
* The retrying features borrow a lot from the library [guava-retrying](https://github.com/rholder/guava-retrying).
  * I've made an important rework and reduced some functionality, assuming that a result without exceptions is 
  always a valid result and won't need to be retried, that I won't need block strategies or retry
  listeners, etc.
  * I've added features, like retries of functions, consumers, suppliers, runnables, ...