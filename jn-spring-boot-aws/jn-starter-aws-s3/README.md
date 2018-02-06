# Jn Starter AWS S3

Starter for Spring Boot applications that provides dependencies to use Amazon S3
and autoconfigures an `AmazonS3` client and a `SimpleS3Client` (abstraction to simplify
the use of S3 with retrying capabilities).

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.s3.region          | Region for the S3 Service. If not informed, the default region will by used |               |
| aws.s3.endpoint  | Service endpoint either with or without the protocol. Only use this if using a non-standard service endpoint.                         |     |
| aws.s3.retryAttempts   | Number of attempts when the `SimpleS3Client` connects to S3                          |   1 |
| aws.s3.retrySleepTime  | Waiting time in milliseconds between attempts                              |    0 |
