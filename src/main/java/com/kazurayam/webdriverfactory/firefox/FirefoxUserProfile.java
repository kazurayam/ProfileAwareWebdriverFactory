package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import groovy.json.JsonOutput;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FirefoxUserProfile implements Comparable<FirefoxUserProfile> {
    public FirefoxUserProfile(final Path userDataDir, ProfileDirectoryName profileDirectoryName) {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.valueOf(userDataDir) + " is not found");
        }

        final Path profilePath = userDataDir.resolve(profileDirectoryName.toString());
        if (!Files.exists(profilePath)) {
            throw new IllegalArgumentException(String.valueOf(profilePath) + " is not found");
        }

        this.userDataDir = userDataDir;
        this.profileDirectoryName = profileDirectoryName;

        // dir name "0iyozca2.kazurayam" => profile name "kazurayam"
        String name = profileDirectoryName.toString().substring(profileDirectoryName.toString().indexOf(".") + 1);
        this.userProfile = new UserProfile(name);
        assert this.userProfile != null;
    }

    public Path getUserDataDir() {
        return this.userDataDir;
    }

    public ProfileDirectoryName getProfileDirectoryName() {
        return this.profileDirectoryName;
    }

    public Path getProfileDirectory() {
        return this.getUserDataDir().resolve(this.getProfileDirectoryName().getName());
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
        return this.getUserDataDir().equals(other.getUserDataDir()) && this.getProfileDirectoryName().equals(other.getProfileDirectoryName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) this.getUserDataDir().hashCode();
        hash = 31 * hash + (int) this.getProfileDirectoryName().hashCode();
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
        sb.append("\"profileDirectoryName\":\"");
        sb.append(this.getProfileDirectoryName());
        sb.append("\"");
        sb.append("}");
        //
        return JsonOutput.prettyPrint(sb.toString());
    }

    private static final String COOKIE_FILE_NAME = "cookies.sqlite";
    private final Path userDataDir;
    private final ProfileDirectoryName profileDirectoryName;
    private UserProfile userProfile;
}
