package com.kazurayam.webdriverfactory;

public class CacheDirectoryName implements Comparable<CacheDirectoryName> {
    public CacheDirectoryName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof CacheDirectoryName)) {
            return false;
        }

        CacheDirectoryName other = (CacheDirectoryName) obj;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public int compareTo(CacheDirectoryName other) {
        return this.getName().compareTo(other.getName());
    }

    private final String name;
}
