package com.jorgenota.utils.aws.support;

/**
 * Provides support for resolving logical resource ids to physical resource ids.
 */
public interface ResourceIdResolver {

    /**
     * Resolves the provided logical resource id to the corresponding physical resource id. If the implementation is
     * unable to resolve the logical resource id to a physical one based on the specific resource information available,
     * the logical resource id is returned as the physical one.
     * <p>
     * This resolving mechanism provides no guarantees on existence of the resource denoted by the resolved physical
     * resource id.
     * </p>
     *
     * @param logicalResourceId the logical resource id to be resolved
     * @return the physical resource id
     */
    String resolveToPhysicalResourceId(String logicalResourceId);

}
