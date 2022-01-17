package com.kazurayam.webdriverfactory.chrome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
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

    private static Logger logger_ = LoggerFactory.getLogger(ChromeUserProfile.class);

    /**
     * @param userDataDir "~/Library/Application Support/Google/Chrome/"
     * @param profileDirectoryName "Default", "Profile 1", "Profile 2", "Profile 3", ...
     * @throws IOException TOOD
     */
    public ChromeUserProfile(final Path userDataDir, ProfileDirectoryName profileDirectoryName) throws IOException {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(
                    String.format("%s is not found", userDataDir));
        }

        Path profilePath = userDataDir.resolve(profileDirectoryName.toString());
        if (!Files.exists(profilePath)) {
            throw new IllegalArgumentException(
                    String.format("%s is not found", profilePath));
        }

        this.userDataDir = userDataDir;
        this.profileDirectoryName = profileDirectoryName;

        logger_.debug("getPreferences(): " + getPreferences());

        Gson gson = new Gson();
        Map m = gson.fromJson(getPreferences(), Map.class);
        Map prof = (Map)m.get("profile");
        assert prof != null;
        String name = (String)prof.get("name");
        assert name != null;
        this.userProfile = new UserProfile(name);
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
                    String.format("%s is not found", preferencesPath));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Reader reader = Files.newBufferedReader(preferencesPath);
        Map m = gson.fromJson(reader, Map.class);
        return gson.toJson(m);
    }

    /**
     * order by UserProfileName
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
        hash = 31 * hash + this.getUserDataDir().hashCode();
        hash = 31 * hash + this.getProfileDirectoryName().hashCode();
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(sb.toString());
    }

    private final Path userDataDir;
    private final ProfileDirectoryName profileDirectoryName;
    private final UserProfile userProfile;
    private String preferences;
    private static final String PREFERENCES_FILE_NAME = "Preferences";
}
