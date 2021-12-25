package com.kazurayam.webdriverfactory.chrome

class ProfileDirectoryName implements Comparable<ProfileDirectoryName> {

    private final String name

    ProfileDirectoryName(String name) {
        this.name = name
    }

    String getName() {
        return this.name
    }

    @Override
    String toString() {
        return this.getName()
    }

    @Override
    boolean equals(Object obj) {
        if (! obj instanceof ProfileDirectoryName) {
            return false
        }
        ProfileDirectoryName other = (ProfileDirectoryName)obj
        return this.getName() == other.getName()
    }

    @Override
    int hashCode() {
        return this.getName().hashCode()
    }

    @Override
    int compareTo(ProfileDirectoryName other) {
        return this.getName() <=> other.getName()
    }

}
