# Jn Starter AWS Kinesis Producer

Starter for Spring Boot applications that provides dependencies to use Amazon Kinesis
and autoconfigures an `AmazonKinesis` client and a `SimpleKinesisClient` (abstraction
to simplify the use of Kinesis with retrying capabilities).

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.kinesis.producer.region          | Region for the Kinesis Service. If not informed, the default region will by used |     |
| aws.kinesis.producer.endpoint  | Service endpoint either with or without the protocol. Only use this if using a non-standard service endpoint.                         |     |
| aws.kinesis.producer.retryAttempts   | Number of attempts when the `SimpleKinesisClient` connects to Kinesis                |   1 |
| aws.kinesis.producer.retrySleepTime  | Waiting time in milliseconds between attempts                         |    0 |
