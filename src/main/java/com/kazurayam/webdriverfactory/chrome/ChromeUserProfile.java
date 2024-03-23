package com.kazurayam.webdriverfactory.chrome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param cacheDirectoryName "Default", "Profile 1", "Profile 2", "Profile 3", ...
     * @throws IOException TOOD
     */
    public ChromeUserProfile(final Path userDataDir, CacheDirectoryName cacheDirectoryName)
            throws IOException {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(cacheDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(
                    String.format("%s is not found", userDataDir));
        }

        Path profilePath = userDataDir.resolve(cacheDirectoryName.toString());
        if (!Files.exists(profilePath)) {
            throw new IllegalArgumentException(
                    String.format("%s is not found", profilePath));
        }

        this.userDataDir = userDataDir;
        this.cacheDirectoryName = cacheDirectoryName;

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

    public CacheDirectoryName getCacheDirectoryName() {
        return this.cacheDirectoryName;
    }

    public Path getCacheDirectory() {
        return this.getUserDataDir().resolve(this.getCacheDirectoryName().getName());
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public String getPreferences() throws IOException {
        Path cachePath = userDataDir.resolve(cacheDirectoryName.toString());
        final Path preferencesPath = cachePath.resolve(PREFERENCES_FILE_NAME);
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
        return this.getUserDataDir().equals(other.getUserDataDir()) && this.getCacheDirectoryName().equals(other.getCacheDirectoryName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.getUserDataDir().hashCode();
        hash = 31 * hash + this.getCacheDirectoryName().hashCode();
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
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //return gson.toJson(sb.toString());
        return sb.toString();
    }

    private final Path userDataDir;
    private final CacheDirectoryName cacheDirectoryName;
    private final UserProfile userProfile;
    private String preferences;
    private static final String PREFERENCES_FILE_NAME = "Preferences";
}
