package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A Data object that wraps the information contained in the
 * '/Users/(username)/Library/Application Support/Google/Chrome/Local State' file.
 * The file is a text in JSON syntax.
 * Amongst all, I am interested in the "profile" section
 * which contains CacheName & ProfileName pairs.
 *
 * <pre>
 *   {
 *   ...
 *     "profile": {
 *     "info_cache": {
 *       "Default": {
 *         ...
 *         "name": "Kazuaki",
 *         ...
 *       },
 *       "Profile 17": {
 *         ...
 *         "name": "Picasso",                              <=== THIS IS IT!!
 *         ...
 *       }
 *     },
 * </pre>
 * With this information, I can look up the Profile Path of each User Profiles.
 * For example, the User Profile "Picasso" corresponds to the Profile Path
 * "/Users/(username)/Library/Application Support/Google/Chrome/Profile 17"
 */
public class LocalState {

    private static final Logger logger = LoggerFactory.getLogger(LocalState.class);
    public static final String LOCAL_STATE_FILENAME = "Local State";

    LocalCacheProfilePairs pairs = new LocalCacheProfilePairs();
    Path localStateFile;

    public LocalState(Path localStateFile) throws IOException {
        if (Files.isRegularFile(localStateFile)) {
            this.localStateFile = localStateFile;
            String json = readAllLines(localStateFile);
            logger.info("[LocalState] localStateFile: " + localStateFile);
            initialize(json);
        } else {
            throw new IOException(localStateFile + " is not a regular file, is a directory");
        }
    }

    public LocalState(String json) {
        Objects.requireNonNull(json);
        initialize(json);
    }

    void initialize(String json) {
        logger.info("[construct] json: " + json);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to parse the content as JSON", e);
        }
        if (root.get("profile") == null) {
            throw new RuntimeException("the key profile is not found in the content: " + json);
        }
        JsonNode infoCache = root.get("profile").get("info_cache");
        Iterator<String> iter = infoCache.fieldNames();
        while (iter.hasNext()) {
            String cacheName = iter.next();
            JsonNode cacheAttributes = infoCache.get(cacheName);
            if (cacheAttributes.has("name")) {
                String profileName = cacheAttributes.get("name").asText();
                LocalCacheProfilePair cpp = new LocalCacheProfilePair(cacheName, profileName);
                this.pairs.add(cpp);
            }
        }
    }

    public LocalCacheProfilePairs getCacheProfilePairs() {
        return this.pairs;
    }

    public int size() {
        return this.pairs.size();
    }

    public Optional<String> lookupCacheNameOf(String profileName) {
        return this.pairs.lookupCacheNameOf(profileName);
    }

    public Optional<CacheDirectoryName> lookupCacheDirectoryNameOf(UserProfile userProfile) {
        Optional<String> cacheName = this.pairs.lookupCacheNameOf(userProfile.getName());
        if (cacheName.isPresent()) {
            CacheDirectoryName cdn = new CacheDirectoryName(cacheName.get());
            return Optional.of(cdn);
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> lookupProfileNameOf(String cacheName) {
        return this.pairs.lookupProfileNameOf(cacheName);
    }

    public Optional<UserProfile> lookupUserProfileOf(CacheDirectoryName cacheDirectoryName) {
        Optional<String> profileName = this.pairs.lookupProfileNameOf(cacheDirectoryName.getName());
        if (profileName.isPresent()) {
            UserProfile up = new UserProfile(profileName.get());
            return Optional.of(up);
        } else {
            return Optional.empty();
        }
    }

    /**
     * read a text file, return as a long text string
     */
    private String readAllLines(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        return String.join("\n", lines);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < pairs.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(pairs.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
