package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.utils.OSIdentifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class ChromeProfileUtils {
    /**
     *
     */
    public static Path getDefaultUserDataDir() {
        if (OSIdentifier.isWindows()) {
            // It is important that this chromeProfilesPath ends with User Data and not with the profile folder
            // %HOME%\AppData\Local\Google\Chrome\User Data
            return Paths.get("C:", "Users", System.getProperty("user.name"), "AppData", "Local", "Google", "Chrome", "User Data");
        } else if (OSIdentifier.isMac()) {
            // ~/Library/Application Support/Google/Chrome
            return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("Google").resolve("Chrome");
        } else if (OSIdentifier.isUnix()) {
            // ~/.config/google-chrome
            return Paths.get(System.getProperty("user.home")).resolve(".config").resolve("google-chrome");
        } else {
            throw new IllegalStateException("Windows, Mac, Linux are supported. Other platforms are not supported.");
        }

    }

    /**
     *
     */
    public static List<ChromeUserProfile> getChromeUserProfileList() {
        return getChromeUserProfileList(getDefaultUserDataDir());
    }

    public static List<ChromeUserProfile> getChromeUserProfileList(final Path userDataDir) {
        Objects.requireNonNull(userDataDir);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.valueOf(userDataDir) + " is not present");
        }

        List<ChromeUserProfile> userProfiles = new ArrayList<ChromeUserProfile>();
        List<Path> dirs = Files.list(userDataDir).collect((Collector<? super Path, ?, List<Path>>) Collectors.toList());
        for (Path dir : dirs) {
            if (Files.exists(dir.resolve("Preferences"))) {
                ChromeUserProfile cp = new ChromeUserProfile(userDataDir, new ProfileDirectoryName(dir.getFileName().toString()));
                userProfiles.add(cp);
            }

        }

        return userProfiles;
    }

    /**
     * @param name name of a Chrome Profile. e.g, new UserProfile("Russ Thomas")
     * @return ChromeUserProfile object of the userProfile specified
     */
    public static ChromeUserProfile findChromeUserProfile(UserProfile userProfile) {
        return findChromeUserProfile(getDefaultUserDataDir(), userProfile);
    }

    public static ChromeUserProfile findChromeUserProfile(Path userDataDir, UserProfile userProfile) {
        Objects.requireNonNull(userProfile);
        List<ChromeUserProfile> userProfiles = getChromeUserProfileList(userDataDir);
        for (ChromeUserProfile cUP : userProfiles) {
            //System.out.println("[ChromeProfileFinder#getUserProfile] userProfile.getName()==${userProfile.getName()}, userProfile.getDirectoryName()=${userProfile.getDirectoryName()}")
            if (cUP.getUserProfile().equals(userProfile)) {
                return cUP;
            }

        }

        return null;
    }

    /**
     * if a Profile of the name is defined, return true, otherwise false
     */
    public static boolean hasChromeUserProfile(UserProfile userProfile) {
        return hasChromeUserProfile(getDefaultUserDataDir(), userProfile);
    }

    public static boolean hasChromeUserProfile(Path userDataDir, UserProfile userProfile) {
        return findChromeUserProfile(userDataDir, userProfile) != null;
    }

    /**
     *
     */
    public static ChromeUserProfile findChromeUserProfileByProfileDirectoryName(ProfileDirectoryName profileDirectoryName) {
        return findChromeUserProfileByProfileDirectoryName(getDefaultUserDataDir(), profileDirectoryName);
    }

    public static ChromeUserProfile findChromeUserProfileByProfileDirectoryName(Path userDataDir, ProfileDirectoryName profileDirectoryName) {
        List<ChromeUserProfile> chromeUserProfiles = getChromeUserProfileList(userDataDir);
        for (ChromeUserProfile chromeUserProfile : chromeUserProfiles) {
            if (chromeUserProfile.getProfileDirectoryName().equals(profileDirectoryName)) {
                return chromeUserProfile;
            }

        }

        return null;
    }

    /**
     *
     */
    public static UserProfile findUserProfileByProfileDirectoryName(ProfileDirectoryName profileDirectoryName) {
        return findUserProfileByProfileDirectoryName(getDefaultUserDataDir(), profileDirectoryName);
    }

    public static UserProfile findUserProfileByProfileDirectoryName(Path userDataDir, ProfileDirectoryName profileDirectoryName) {
        return findChromeUserProfileByProfileDirectoryName(userDataDir, profileDirectoryName).getUserProfile();
    }

    /**
     * @return
     */
    public static String allChromeUserProfilesAsString() {
        List<ChromeUserProfile> userProfiles = getChromeUserProfileList();
        Collections.sort(userProfiles);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int count = 0;
        for (ChromeUserProfile up : userProfiles) {
            if (count > 0) {
                sb.append(",");
            }

            sb.append(up.toString());
            count += 1;
        }

        sb.append("]");
        return sb.toString();
    }

    private ChromeProfileUtils() {
    }
}
