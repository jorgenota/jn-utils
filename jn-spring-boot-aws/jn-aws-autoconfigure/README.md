# Jn AWS Autoconfigure

Base autoconfiguration module that will be used by all of the starters provided for use of AWS services.
Configures a bean _awsEnvironment_ (of type `AwsEnvironment`) that provides a base context such as the default AWS region and
credentials provider to use.

`AwsEnvironment` provides also some AWS related utilities, such as helpers to configure AWS
clients based on the context stored in it.

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.context.autoDetectRegion | Auto-detect region (getting it from que EC2 instance where the app is running) to set the default region to use. It is only possible if running on an EC2 instance | true |
| aws.context.staticRegion  | Static region name provided to be used as default region. If provided, region auto-detection won't be used.  | |
| aws.context.useDefaultCredentialsChain   | Use `DefaultAWSCredentialsProviderChain` as the default credentials provider | true |
| aws.context.accessKey  | Access key. If provided along with a secret key, a `AWSStaticCredentialsProvider` (containing de given basic credentials) will be used as the default credentials provider | |
| aws.context.secretKey  | Secret key. See description for access key | |
