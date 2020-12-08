# Jn Starter AWS SQS

This starter automatically configures a bean _amazonSqs_ (of type `AmazonSQSAsync`) that provides SQS client ready to be
used.

Depends on [jn-aws-autoconfigure](../jn-aws-autoconfigure/README.md), which sets up a basic common environment via the
bean _awsEnvironment_
(check its documentation to get more information about the configuration properties made available by this module)

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.sqs.region | Custom region for the SQS client. If not set, default region provided by `awsEnvironment` will be used (recommended) | |
| aws.sqs.endpoint  | Service endpoint to use when building the SQS client, in case the user needs to set a specific one.  | |
| aws.sqs.config.&lt;property&gt;  |  All the additional properties to configure the AWS client if you need to modify its default values. Read [documentarion of AWSClientProperties.Config](../jn-aws-autoconfigure/README.md#awsclientproperties-doc)  | |
