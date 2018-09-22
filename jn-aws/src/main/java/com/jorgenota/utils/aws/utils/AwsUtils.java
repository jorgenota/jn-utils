package com.jorgenota.utils.aws.utils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.util.EC2MetadataUtils;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
public final class AwsUtils {
    private static final String EC2_METADATA_ROOT = "/latest/meta-data";

    private AwsUtils() {
    }

    @Nullable
    private static Boolean isCloudEnvironment;

    public static boolean isRunningOnCloudEnvironment() {
        if (isCloudEnvironment == null) {
            try {
                isCloudEnvironment = EC2MetadataUtils.getData(EC2_METADATA_ROOT + "/instance-id", 1) != null;
            } catch (AmazonClientException e) {
                isCloudEnvironment = false;
            }
        }
        return isCloudEnvironment;
    }

}
