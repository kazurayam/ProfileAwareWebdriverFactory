package com.kazurayam.webdriverfactory.chrome;

import java.util.Objects;

public class LocalCacheProfilePair {
    private final String cacheName;
    private final String profileName;

    /**
     *
     * @param cn e.g, "Profile 17"
     * @param pn e.g, "Picasso"
     */
    public LocalCacheProfilePair(String cn, String pn) {
        Objects.requireNonNull(cn);
        if (!cn.equals("Default") && !cn.startsWith("Profile")) {
            throw new IllegalArgumentException("cacheName must be Default or Profile N");
        }
        Objects.requireNonNull(pn);
        if (pn.isEmpty()) {
            throw new IllegalArgumentException("profileName must not be empty string");
        }
        this.cacheName = cn.trim();
        this.profileName = pn.trim();
    }

    public final String getCacheName() {
        return cacheName;
    }

    public final boolean hasCacheName(String cn) { return cn.trim().equals(this.cacheName); };

    public final String getProfileName() {
        return profileName;
    }

    public final boolean hasProfileName(String pn) {
        return pn.trim().equals(this.profileName);
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LocalCacheProfilePair))
            return false;
        LocalCacheProfilePair other = (LocalCacheProfilePair)o;
        boolean cacheNameEquals = this.cacheName.equals(other.cacheName);
        return this.profileName.equals(other.profileName) && cacheNameEquals;
    }

    @Override
    public final int hashCode() {
        int result = 17;
        if (cacheName != null) {
            result = 31 * result + cacheName.hashCode();
        }
        if (profileName != null) {
            result = 31 * result + profileName.hashCode();
        }
        return result;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"cacheName\":\"");
        sb.append(cacheName);
        sb.append("\", \"profileName\":\"");
        sb.append(profileName);
        sb.append("\"");
        sb.append("}");
        return sb.toString();
    }
}
