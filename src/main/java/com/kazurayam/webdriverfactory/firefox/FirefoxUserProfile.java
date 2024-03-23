package com.kazurayam.webdriverfactory.firefox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FirefoxUserProfile implements Comparable<FirefoxUserProfile> {
    public FirefoxUserProfile(final Path userDataDir, CacheDirectoryName cacheDirectoryName) {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(cacheDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.valueOf(userDataDir) + " is not found");
        }

        final Path profilePath = userDataDir.resolve(cacheDirectoryName.toString());
        if (!Files.exists(profilePath)) {
            throw new IllegalArgumentException(String.valueOf(profilePath) + " is not found");
        }

        this.userDataDir = userDataDir;
        this.cacheDirectoryName = cacheDirectoryName;

        // dir name "0iyozca2.kazurayam" => profile name "kazurayam"
        String name = cacheDirectoryName.toString().substring(cacheDirectoryName.toString().indexOf(".") + 1);
        this.userProfile = new UserProfile(name);
        assert this.userProfile != null;
    }

    public Path getUserDataDir() {
        return this.userDataDir;
    }

    public CacheDirectoryName getCacheDirectoryName() {
        return this.cacheDirectoryName;
    }

    public Path getProfileDirectory() {
        return this.getUserDataDir().resolve(this.getCacheDirectoryName().getName());
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    @Override
    public int compareTo(FirefoxUserProfile other) {
        return this.getUserProfile().compareTo(other.getUserProfile());
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof FirefoxUserProfile)) {
            return false;
        }
        FirefoxUserProfile other = (FirefoxUserProfile) obj;
        return this.getUserDataDir().equals(other.getUserDataDir()) && this.getCacheDirectoryName().equals(other.getCacheDirectoryName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) this.getUserDataDir().hashCode();
        hash = 31 * hash + (int) this.getCacheDirectoryName().hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"userProfile\":\"");
        sb.append(this.getUserProfile().toString());
        sb.append("\"");
        sb.append(",");
        //
        sb.append("\"userDataDir\":\"");
        sb.append(this.getUserDataDir().toString());
        sb.append("\"");
        sb.append(",");
        sb.append("\"cacheDirectoryName\":\"");
        sb.append(this.getCacheDirectoryName());
        sb.append("\"");
        sb.append("}");
        //
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(sb.toString());
    }

    private static final String COOKIE_FILE_NAME = "cookies.sqlite";
    private final Path userDataDir;
    private final CacheDirectoryName cacheDirectoryName;
    private UserProfile userProfile;
}
