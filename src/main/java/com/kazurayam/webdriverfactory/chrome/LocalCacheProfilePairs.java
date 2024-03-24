package com.kazurayam.webdriverfactory.chrome;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalCacheProfilePairs {

    private List<LocalCacheProfilePair> pairs = null;

    public LocalCacheProfilePairs() {
        pairs = new ArrayList<>();
    }

    public void add(LocalCacheProfilePair cpp) {
        pairs.add(cpp);
    }

    public LocalCacheProfilePair get(int x) {
        return pairs.get(x);
    }

    public Iterator<LocalCacheProfilePair> iterator() {
        return pairs.iterator();
    }

    public int size() {
        return pairs.size();
    }

    public Optional<String> lookupCacheNameOf(String profileName) {
        List<LocalCacheProfilePair> result = pairs.stream()
                .filter(cpp -> cpp.hasProfileName(profileName))
                .collect(Collectors.toList());
        if (!result.isEmpty()) {
            return Optional.of(result.get(0).getCacheName());
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> lookupProfileNameOf(String cacheName) {
        List<LocalCacheProfilePair> result = pairs.stream()
                .filter(cpp -> cpp.hasCacheName(cacheName))
                .collect(Collectors.toList());
        if (!result.isEmpty()) {
            return Optional.of(result.get(0).getProfileName());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(pairs.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
