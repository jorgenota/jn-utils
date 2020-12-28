# Jn Starter AWS S3

This starter automatically configures a bean _amazonS3_ (of type `AmazonS3`) that provides a S3 client ready to be used.

Depends on [jn-aws-autoconfigure](../jn-aws-autoconfigure/README.md), which sets up a basic common environment via the
bean _awsEnvironment_
(check its documentation to get more information about the configuration properties made available by this module)

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.s3.region | Custom region for the S3 client. If not set, default region provided by `awsEnvironment` will be used (recommended) | |
| aws.s3.endpoint | Service endpoint to use when building the S3 client, in case the user needs to set a specific one | |
| aws.s3.pathStyleAccess  | S3 configuration option to set path-style access | |
| aws.s3.chunkedEncodingDisabled | S3 configuration option for use of chunked encoding | |
| aws.s3.accelerateModeEnabled | S3 configuration option for use of S3 accelerate | |
| aws.s3.payloadSigningEnabled | S3 configuration option for use of payload signing | |
| aws.s3.dualstackEnabled | S3 configuration option to enable dualstack endpoint | |
| aws.s3.config.&lt;property&gt; | All the additional properties to configure the AWS client if you need to modify its default values. Read [documentation of AWSClientProperties.Config](../jn-aws-autoconfigure/README.md#awsclientproperties-doc) | |
