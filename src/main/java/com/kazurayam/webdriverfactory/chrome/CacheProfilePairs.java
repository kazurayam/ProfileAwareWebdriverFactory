package com.kazurayam.webdriverfactory.chrome;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class CacheProfilePairs {

    private List<CacheProfilePair> pairs = null;

    public CacheProfilePairs() {
        pairs = new ArrayList<>();
    }

    public void add(CacheProfilePair cpp) {
        pairs.add(cpp);
    }

    public CacheProfilePair get(int x) {
        return pairs.get(x);
    }

    public Iterator<CacheProfilePair> iterator() {
        return pairs.iterator();
    }

    public int size() {
        return pairs.size();
    }

    public String lookupCacheNameOf(String profileName) {
        List<CacheProfilePair> result = pairs.stream()
                .filter(cpp -> cpp.hasProfileName(profileName))
                .toList();
        if (!result.isEmpty()) {
            return result.get(0).getCacheName();
        } else {
            return null;
        }
    }
}
