# Jn Starter AWS SES

This starter automatically configures a bean _amazonSes_ (of type `AmazonSimpleEmailServiceAsync `)
that provides a SES client ready to be used.

Depends on [jn-aws-autoconfigure](../jn-aws-autoconfigure/README.md), which sets up a basic common environment via
the bean _awsEnvironment_
(check its documentation to get more information about the configuration properties made available by this module)

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.ses.region | Custom region for the SES client. If not set, default region provided by _awsEnvironment_ will be used (recommended) | |
| aws.ses.endpoint  | Service endpoint to use when building the SES client, in case the user needs to set a specific one.  | |
| aws.ses.config.&lt;property&gt;  |  All the additional properties to configure the AWS client if you need to modify its default values. Read [documentarion of AWSClientProperties.Config](../jn-aws-autoconfigure/README.md#awsclientproperties-helper-dto)  | |
