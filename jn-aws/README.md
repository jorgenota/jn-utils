# Js AWS

Helper utils to deal with AWS services, with abstractions to simplify the use of
that services

## Simple Kinesis Client

The helper class `SimpleKinesisClient` is and abstraction to write into Kinesis streams.
This client:
* Can be configured to handle retries.
* Can receive domain objects and `byte[]` content.
* Will throw a `ClienException` in case of error.

## Simple S3 Client

The helper class `SimpleS3Client` is and abstraction to read, write and delete into and from
S3 buckets. This client:
* Can be configured to handle retries.
* Can handle `String` and `byte[]` content.
* Will throw a `ClienException` in case of error.


