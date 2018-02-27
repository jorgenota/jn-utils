# Jn Base

Some simple base utils to help build libraries and apps with out the need
of adding dependencies to larger libraries (such as _spring-core_ o _guava_).

The main functionality is represented by these classes:
* `ObjectMappingUtils`: utils to deal with mappings and conversions (a default jackson ObjectMapper, ...)
* `Preconditions`: helpers to check for method arguments and state
* `ServiceException`: general runtime exception to be used by client components that connect
to services