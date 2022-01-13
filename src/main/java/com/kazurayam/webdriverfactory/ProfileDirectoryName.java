package com.kazurayam.webdriverfactory;

public class ProfileDirectoryName implements Comparable<ProfileDirectoryName> {
    public ProfileDirectoryName(String name) {
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
        if (! (obj instanceof ProfileDirectoryName)) {
            return false;
        }

        ProfileDirectoryName other = (ProfileDirectoryName) obj;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public int compareTo(ProfileDirectoryName other) {
        return this.getName().compareTo(other.getName());
    }

    private final String name;
}
