package com.jorgenota.utils.messaging;

public class IdentityDestinationResolver implements DestinationResolver<String> {

    public IdentityDestinationResolver() {
    }

    @Override
    public String resolveDestination(String name) throws DestinationResolutionException {
        return name;
    }
}
