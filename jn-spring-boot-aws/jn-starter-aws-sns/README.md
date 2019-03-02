# Jn Starter AWS SNS

This starter automatically configures a bean _amazonSns_ (of type `AmazonSNSAsync `) that
provides a SNS client ready to be used.

Depends on [jn-aws-autoconfigure](../jn-aws-autoconfigure/README.md), which sets up a basic common environment via
the bean _awsEnvironment_
(check its documentation to get more information about the configuration properties made available by this module)

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.sns.region | Custom region for the SNS client. If not set, default region provided by _awsEnvironment_ will be used (recommended) | |
| aws.sns.endpoint  | Service endpoint to use when building the SNS client, in case the user needs to set a specific one.  | |
