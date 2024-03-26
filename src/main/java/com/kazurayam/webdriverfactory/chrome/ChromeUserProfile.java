package com.kazurayam.webdriverfactory.chrome;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * A representation of a Chrome Profile instance.
 *
 * @author kazurayam
 */
public class ChromeUserProfile implements Comparable<ChromeUserProfile> {

    private static Logger logger_ = LoggerFactory.getLogger(ChromeUserProfile.class);

    private Path userDataDir;
    private CacheDirectoryName cacheDirectoryName;
    private UserProfile userProfile;
    private boolean present;
    private String preferences;
    private static final String PREFERENCES_FILE_NAME = "Preferences";

    private static final String PSEUDO_USER_PROFILE = "";

    /**
     * @param userDataDir "~/Library/Application Support/Google/Chrome/"
     * @param cacheDirectoryName "Default", "Profile 1", "Profile 2", "Profile 3", ...
     * @throws IOException TOOD
     */
    public ChromeUserProfile(final Path userDataDir,
                             CacheDirectoryName cacheDirectoryName)
            throws IOException {
        Path localStateFile = userDataDir.resolve(LocalState.LOCAL_STATE_FILENAME);
        LocalState localState = new LocalState(localStateFile);
        Optional<UserProfile> userProfile =
                localState.lookupUserProfileOf(cacheDirectoryName);
        if (userProfile.isPresent()) {
            initialize(userDataDir, cacheDirectoryName, userProfile.get());
        } else {
            logger_.warn(String.format(
                    "unable to find a user profile that corresponds to the cache directory '%s'." +
                            " '%s' is assumed", cacheDirectoryName, PSEUDO_USER_PROFILE));
            initialize(userDataDir, cacheDirectoryName, new UserProfile(PSEUDO_USER_PROFILE));
        }
    }

    public ChromeUserProfile(final Path userDataDir,
                             CacheDirectoryName cacheDirectoryName,
                             UserProfile userProfile)
            throws IOException {
        initialize(userDataDir, cacheDirectoryName, userProfile);
    }

    void initialize(final Path userDataDir,
                    CacheDirectoryName cacheDirectoryName,
                    UserProfile userProfile) throws IOException {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(cacheDirectoryName);
        Objects.requireNonNull(userProfile);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(
                    String.format("userDataDir '%s' is not found", userDataDir ));
        }
        //
        Path cacheDir = userDataDir.resolve(cacheDirectoryName.toString());
        if (!Files.exists(cacheDir)) {
            logger_.warn(String.format("cache directory '%s' is not found. ", cacheDir));
        }
        //
        this.userDataDir = userDataDir;
        this.cacheDirectoryName = cacheDirectoryName;
        this.userProfile = userProfile;
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Files.newBufferedReader(preferencesPath));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }

    public boolean isPresent() {
        Path cacheDir = userDataDir.resolve(cacheDirectoryName.toString());
        return Files.exists(cacheDir);
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
        //
        sb.append("\"userDataDir\":\"");
        sb.append(this.getUserDataDir().toString());
        sb.append("\"");
        sb.append(",");
        //
        sb.append("\"cacheDirectoryName\":\"");
        sb.append(this.getCacheDirectoryName());
        sb.append("\"");
        sb.append(",");
        //
        sb.append("\"userProfile\":\"");
        sb.append(this.getUserProfile().toString());
        sb.append("\"");
        sb.append(",");
        //
        sb.append("\"isPresent\":");
        sb.append(this.isPresent());
        sb.append("}");
        return sb.toString();
    }

}
