package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.ProfileDirectoryName
import com.kazurayam.webdriverfactory.UserProfile
import groovy.json.JsonOutput

import java.nio.file.Files
import java.nio.file.Path

class FirefoxUserProfile implements Comparable<FirefoxUserProfile> {

    private static final String COOKIE_FILE_NAME = 'cookies.sqlite'

    private final Path userDataDir
    private final ProfileDirectoryName profileDirectoryName

    private UserProfile userProfile

    FirefoxUserProfile(Path userDataDir, ProfileDirectoryName profileDirectoryName) {
        Objects.requireNonNull(userDataDir)
        Objects.requireNonNull(profileDirectoryName)
        if (! Files.exists(userDataDir)) {
            throw new IllegalArgumentException("${userDataDir} is not found")
        }
        Path profilePath = userDataDir.resolve(profileDirectoryName.toString())
        if (! Files.exists(profilePath)) {
            throw new IllegalArgumentException("${profilePath} is not found")
        }
        this.userDataDir = userDataDir
        this.profileDirectoryName = profileDirectoryName

        // dir name "0iyozca2.kazurayam" => profile name "kazurayam"
        String name = profileDirectoryName.toString()
                .substring(profileDirectoryName.toString().indexOf(".") + 1)
        this.userProfile = new UserProfile(name)
        assert this.userProfile != null
    }

    Path getUserDataDir() {
        return this.userDataDir
    }

    ProfileDirectoryName getProfileDirectoryName() {
        return this.profileDirectoryName
    }

    Path getProfileDirectory() {
        return this.getUserDataDir()
                .resolve(this.getProfileDirectoryName().getName())
    }

    UserProfile getUserProfile() {
        return this.userProfile
    }

    @Override
    int compareTo(FirefoxUserProfile other) {
        return this.getUserProfile() <=> other.getUserProfile()
    }

    @Override
    boolean equals(Object obj) {
        if (! obj instanceof FirefoxUserProfile) {
            return false
        }
        FirefoxUserProfile other = (FirefoxUserProfile)obj
        return this.getUserDataDir() == other.getUserDataDir() &&
                this.getProfileDirectoryName() == other.getProfileDirectoryName()
    }

    @Override
    int hashCode() {
        int hash = 7
        hash = 31 * hash + (int) this.getUserDataDir().hashCode()
        hash = 31 * hash + (int) this.getProfileDirectoryName().hashCode()
        return hash
    }

    @Override
    String toString() {
        StringBuilder sb = new StringBuilder()
        sb.append("{")
        sb.append("\"userProfile\":\"")
        sb.append(this.getUserProfile().toString())
        sb.append("\"")
        sb.append(",")
        //
        sb.append("\"userDataDir\":\"")
        sb.append(this.getUserDataDir().toString())
        sb.append("\"")
        sb.append(",")
        sb.append("\"profileDirectoryName\":\"")
        sb.append(this.getProfileDirectoryName())
        sb.append("\"")
        sb.append("}")
        //
        return JsonOutput.prettyPrint(sb.toString())
    }
}
