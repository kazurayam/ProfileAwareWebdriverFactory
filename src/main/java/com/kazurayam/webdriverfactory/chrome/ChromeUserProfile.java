package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * A representation of a Chrome Profile instance.
 *
 * @author kazurayam
 */
public class ChromeUserProfile implements Comparable<ChromeUserProfile> {
    /**
     * @param userDataDir "~/Library/Application Support/Google/Chrome/"
     * @param profileDirectoryName "Default", "Profile 1", "Profile 2", "Profile 3", ...
     */
    public ChromeUserProfile(final Path userDataDir, ProfileDirectoryName profileDirectoryName) throws IOException {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(
                    String.format("%s is not found", userDataDir.toString()));
        }

        Path profilePath = userDataDir.resolve(profileDirectoryName.toString());
        if (!Files.exists(profilePath)) {
            throw new IllegalArgumentException(
                    String.format("%s is not found", profilePath.toString()));
        }

        this.userDataDir = userDataDir;
        this.profileDirectoryName = profileDirectoryName;

        Map m = (Map) new JsonSlurper().parseText(getPreferences());
        String name = (String) DefaultGroovyMethods.getAt(m.get("profile"), "name");
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

    public String getPreferences() throws IOException {
        Path profilePath = userDataDir.resolve(profileDirectoryName.toString());
        final Path preferencesPath = profilePath.resolve(PREFERENCES_FILE_NAME);
        if (!Files.exists(preferencesPath)) {
            throw new IOException(
                    String.format("%s is not found", preferencesPath.toString()));
        }
        return JsonOutput.prettyPrint(ResourceGroovyMethods.getText(preferencesPath.toFile()));
    }

    /**
     * order by UserProfileName
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(ChromeUserProfile other) {
        return this.getUserProfile().compareTo(other.getUserProfile());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChromeUserProfile)) {
            return false;
        }

        ChromeUserProfile other = (ChromeUserProfile) obj;
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

    private final Path userDataDir;
    private final ProfileDirectoryName profileDirectoryName;
    private UserProfile userProfile;
    private String preferences;
    private static final String PREFERENCES_FILE_NAME = "Preferences";
}
