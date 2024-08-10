package org.personal.kafkamavenrepo.Utilities;

public final class TrustedPackages {

    // Prevent instantiation
    private TrustedPackages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Define your trusted packages
    public static final String[] PACKAGES = {"org.personal.kafkatemplate"};
}