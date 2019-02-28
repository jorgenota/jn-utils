# Jn Starter AWS S3

This starter automatically configures a bean _amazonS3_ (of type `AmazonS3`) that provides S3 client ready to be used.

Depends on [jn-aws-autoconfigure](../jn-aws-autoconfigure/README.md), which sets up a basic common environment via
the bean _awsEnvironment_
(check its documentation to get more information about the configuration properties made available by this module)

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.s3.region | Custom region for S3 client. If not set, default region provided by _awsEnvironment_ will be used (recommended) | |
| aws.s3.endpoint  | Service endpoint to use when building the S3 client, in case the user needs to set a specific one.  | |
