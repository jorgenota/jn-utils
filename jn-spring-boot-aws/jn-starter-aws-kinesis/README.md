# Jn Starter AWS Kinesis

Starter for Spring Boot applications that provides dependencies to use Amazon Kinesis
and autoconfigures an `AmazonKinesis` client and a `SimpleKinesisClient` (abstraction
to simplify the use of Kinesis with retrying capabilities).

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.kinesis.region          | Region for the Kinesis Service. If not informed, the default region will by used |     |
| aws.kinesis.retryAttempts   | Number of attempts when the `SimpleKinesisClient` connects to Kinesis                |   1 |
| aws.kinesis.retrySleepTime  | Waiting time in milliseconds between attempts                         |    0 |
