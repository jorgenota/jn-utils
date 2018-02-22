# Jn Starter AWS Kinesis Consumer

Starter for Spring Boot applications that provides dependencies to use Amazon Kinesis
and autoconfigures an `AmazonKinesis` client and a `SimpleKinesisClient` (abstraction
to simplify the use of Kinesis with retrying capabilities).

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.kinesis.consumer.region          | Region for the Kinesis Service. If not informed, the default region will by used |     |
| aws.kinesis.consumer.endpoint  | Service endpoint either with or without the protocol. Only use this if using a non-standard service endpoint.                         |     |
| aws.kinesis.consumer.retryAttempts   | Number of attempts when the `SimpleKinesisClient` connects to Kinesis                |   1 |
| aws.kinesis.consumer.retrySleepTime  | Waiting time in milliseconds between attempts                         |    0 |
| aws.kinesis.consumer.checkPointInterval  | Interval between checkpoints in milliseconds                         |    30000 |
| aws.kinesis.consumer.streams[].name  | Name of the Kinesis stream                         |    |
| aws.kinesis.consumer.streams[].maxRecords  | Max records to fetch in a Kinesis getRecords() call  |    100 |
