package com.kazurayam.webdriverfactory

class UserProfile implements Comparable<UserProfile> {

    private String name

    UserProfile(String name) {
        this.name = name
    }

    String getName() {
        return this.name
    }

    @Override
    boolean equals(Object obj) {
        if (! obj instanceof UserProfile) {
            return false
        }
        UserProfile other = (UserProfile)obj
        return this.getName() == other.getName()
    }

    @Override
    int hashCode() {
        return this.name.hashCode()
    }

    @Override
    String toString() {
        return this.name
    }

    @Override
    int compareTo(UserProfile other) {
        return this.getName() <=> other.getName()
    }

}
