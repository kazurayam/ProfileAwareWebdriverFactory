package com.kazurayam.webdriverfactory;

public class UserProfile implements Comparable<UserProfile> {
    public UserProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof UserProfile)) {
            return false;
        }

        UserProfile other = (UserProfile) obj;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(UserProfile other) {
        return this.getName().compareTo(other.getName());
    }

    public static final UserProfile NULL = new UserProfile(null);

    private final String name;
}
