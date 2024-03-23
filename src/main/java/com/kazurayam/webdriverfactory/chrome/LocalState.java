package com.kazurayam.webdriverfactory.chrome;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

    public static final String LOCAL_STATE_FILENAME = "Local State";

    CacheProfilePairs pairs = new CacheProfilePairs();
    Path localStateFile;

    public LocalState(Path localStateFile) throws IOException {
        this.localStateFile = localStateFile;
        String json = readAllLines(localStateFile);
        construct(json);
    }

    public LocalState(String json) {
        Objects.requireNonNull(json);
        construct(json);
    }

    void construct(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode infoCache = root.get("profile").get("info_cache");
            Iterator<String> iter = infoCache.fieldNames();
            while (iter.hasNext()) {
                String cacheName = iter.next();
                JsonNode cacheAttributes = infoCache.get(cacheName);
                if (cacheAttributes.has("name")) {
                    String profileName = cacheAttributes.get("name").asText();
                    CacheProfilePair cpp = new CacheProfilePair(cacheName, profileName);
                    this.pairs.add(cpp);
                }
            }
        } catch(JsonProcessingException e) {
            throw new RuntimeException(
                    "unable to parse a string as JSON:\n" + json, e);
        }
    }

    public int size() {
        return this.pairs.size();
    }

    public String lookupCacheNameOf(String profileName) {
        return this.pairs.lookupCacheNameOf(profileName);
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

        }
        sb.append("]");
        return sb.toString();
    }
}
