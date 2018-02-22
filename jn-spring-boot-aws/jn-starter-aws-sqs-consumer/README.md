# Jn Starter AWS SQS Consumer

Starter for Spring Boot applications that provides dependencies to use Amazon SQS
and autoconfigures an `AmazonSQSAsync` client and a `SimpleMessageListenerContainer`.

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.sqs.consumer.region          | Region for the Kinesis Service. If not informed, the default region will by used |     |
| aws.sqs.consumer.endpoint  | Service endpoint either with or without the protocol. Only use this if using a non-standard service endpoint.                         |     |
| aws.sqs.consumer.maxNumberOfMessages   | Maximum number of messages that should be retrieved during one poll to the Amazon SQS system               |   10 |
| aws.sqs.consumer.backOffTime  | Number of milliseconds the polling thread must wait before trying to recover when an error occurs                         |    10000 |
