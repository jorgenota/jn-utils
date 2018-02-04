# Jn Base

Some simple base utils to help build libraries and apps with out the need
of adding dependencies to larger libraries (such as _spring-core_ o _guava_).

The main functionality is represented by these classes:
* ObjectUtils: utils to deal with objects (check for empty objects,
a default jackson ObjectMapper, ...)
* StringUtils: several utils related to String
* Preconditions: helpers to check for method arguments and state
* ClientException: general runtime exception to be used by client components that connect
to services