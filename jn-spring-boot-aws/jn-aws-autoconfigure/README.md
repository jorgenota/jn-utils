# Jn AWS Autoconfigure

Base autoconfiguration module that will be used by all the starters provided for use of AWS services. Configures a
bean _awsEnvironment_ (of type `AwsEnvironment`) that provides a base context such as the default AWS region and
credentials provider to use.

## AWS Environment autoconfiguration

`AwsEnvironment` provides also some AWS related utilities, such as helpers to configure AWS clients based on the context
stored in it.

The following properties are used to autoconfigure the `AwsEnvironment`

| Property               | Description                                                                | Default value  |
| ---------------------- | -------------------------------------------------------------------------- | -------------- |
| aws.context.autoDetectRegion | Auto-detect region (getting it from que EC2 instance where the app is running) to set the default region to use. It is only possible if running on an EC2 instance | true |
| aws.context.staticRegion  | Static region name provided to be used as default region. If provided, region auto-detection won't be used.  | |
| aws.context.useDefaultCredentialsChain   | Use `DefaultAWSCredentialsProviderChain` as the default credentials provider | true |
| aws.context.accessKey  | Access key. If provided along with a secret key, a `AWSStaticCredentialsProvider` (containing de given basic credentials) will be used as the default credentials provider | |
| aws.context.secretKey  | Secret key. See description for access key | |

## AWSClientProperties helper DTO

Besides, this module provides the base class `AWSClientProperties`, which is a DTO with properties common to all AWS
client classes, so it can be extended by ConfigurationProperties used in all the starters to configure the client for
the pertinent service. The attributes of `AWSClientProperties` are:

| Attribute               | Description                                                                |
| ---------------------- | -------------------------------------------------------------------------- |
| region | Custom region for the AWS client. |
| endpoint  | Service endpoint to use when building the AWS client, in case the user needs to set a specific one.  |
| config  | `AWSClientProperties.Config` DTO with all the additional properties to configure the AWS client if you need to modify its default values  |

The attributes of <a name="awsclientproperties-doc"></a>`AWSClientProperties.Config` are (for more information
see [javadoc of ClientConfiguration](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/ClientConfiguration.html)):

| Attribute               | Description                                                                |
| ---------------------- | -------------------------------------------------------------------------- |
| cacheResponseMetadata | Whether to cache response metadata |
| clientExecutionTimeout  | Amount of time (in milliseconds) to allow the client to complete the execution of an API call |
| connectionMaxIdleMillis  | Maximum amount of time that an idle connection may sit in the connection pool and still be eligible for reuse   |
| connectionTimeout  | Amount of time to wait (in milliseconds) when initially establishing a connection before giving up and timing out |
| connectionTTL  | Expiration time (in milliseconds) for a connection in the connection pool |
| disableSocketProxy  | Whether to disable proxies at the socket level |
| maxConnections  | Maximum number of allowed open HTTP connections |
| maxConsecutiveRetriesBeforeThrottling  | Maximum number of consecutive failed retries that the client will permit before throttling all subsequent retries of failed requests |
| maxErrorRetry  | Maximum number of retry attempts for failed retryable requests (ex: 5xx error responses from services) |
| nonProxyHosts  | Optional hosts the client will access without going through the proxy |
| preemptiveBasicProxyAuth  | Whether to attempt to authenticate preemptively against proxy servers using basic authentication |
| protocol  | Protocol (http or https) |
| proxyAuthenticationMethods  | List of authentication methods that should be used when authenticating against an HTTP proxy, in the order they should be attempted |
| proxyDomain  | Optional Windows domain name for configuration an NTLM proxy |
| proxyHost  | Optional proxy host the client will connect through |
| proxyPassword  | Optional proxy password to use when connecting through a proxy |
| proxyPort  | Optional proxy port the client will connect through |
| proxyProtocol  | Protocol to use for connecting to the proxy |
| proxyUsername  | Proxy user name to use if connecting through a proxy |
| requestTimeout  | Amount of time to wait (in milliseconds) for the request to complete before giving up and timing out |
| responseMetadataCacheSize  | Response metadata cache size |
| signerOverride  | Name of the signature algorithm to use for signing requests made by this client |
| useGzip  | Whether gzip decompression should be used when receiving HTTP responses |
| useThrottleRetries  | Whether throttled retries should be used |

