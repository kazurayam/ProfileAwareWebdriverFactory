package com.kazurayam.webdriverfactory.edge;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.utils.OSIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EdgeUserProfileUtils {

    private static Logger logger_ = LoggerFactory.getLogger(EdgeUserProfileUtils.class);

    public static Path getDefaultUserDataDir() {
        if (OSIdentifier.isWindows()) {
            // It is important that this chromeProfilesPath ends with User Data and not with the profile folder
            // %HOME%\AppData\Local\Google\Chrome\User Data
            throw new UnsupportedOperationException("TODO");
            //return Paths.get("C:", "Users", System.getProperty("user.name"), "AppData", "Local", "Google", "Chrome", "User Data");
        } else if (OSIdentifier.isMac()) {
            // ~/Library/Application Support/Google/Chrome
            return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("Microsoft Edge").resolve("Default");
        } else if (OSIdentifier.isUnix()) {
            // ~/.config/google-chrome
            throw new UnsupportedOperationException("TODO");
            //return Paths.get(System.getProperty("user.home")).resolve(".config").resolve("google-chrome");
        } else {
            throw new IllegalStateException("Windows, Mac, Linux are supported. Other platforms are not supported.");
        }

    }

    public static List<EdgeUserProfile> getEdgeUserProfileList()
            throws IOException
    {
        return getEdgeUserProfileList(getDefaultUserDataDir());
    }

    public static List<EdgeUserProfile> getEdgeUserProfileList(final Path userDataDir)
            throws IOException
    {
        Objects.requireNonNull(userDataDir);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.format("%s is not present", userDataDir));
        }
        List<EdgeUserProfile> userProfiles = new ArrayList<>();
        List<Path> dirs = Files.list(userDataDir).collect(Collectors.toList());
        for (Path dir : dirs) {
            if (Files.exists(dir.resolve("Preferences"))) {
                EdgeUserProfile cp = new EdgeUserProfile(userDataDir, new ProfileDirectoryName(dir.getFileName().toString()));
                userProfiles.add(cp);
            }
        }
        return userProfiles;
    }

    public static EdgeUserProfile findEdgeUserProfile(com.kazurayam.webdriverfactory.UserProfile userProfile)
            throws IOException
    {
        return findEdgeUserProfile(getDefaultUserDataDir(), userProfile);
    }

    public static EdgeUserProfile findEdgeUserProfile(Path userDataDir, com.kazurayam.webdriverfactory.UserProfile userProfile)
            throws IOException
    {
        Objects.requireNonNull(userProfile);
        List<EdgeUserProfile> edgeUserProfiles = getEdgeUserProfileList(userDataDir);
        for (EdgeUserProfile cup : edgeUserProfiles) {
            //System.out.println("[EdgeProfileFinder#getUserProfile] userProfile.getName()==${userProfile.getName()}, userProfile.getDirectoryName()=${userProfile.getDirectoryName()}")
            if (cup.getUserProfile().equals(userProfile)) {
                return cup;
            }
        }
        return null;
    }

    /**
     * if a Profile of the name is defined, return true, otherwise false
     *
     * @param userProfile TODO
     * @return TODO
     * @throws IOException TODO
     */
    public static boolean hasChromeUserProfile(com.kazurayam.webdriverfactory.UserProfile userProfile) throws IOException {
        return hasChromeUserProfile(getDefaultUserDataDir(), userProfile);
    }

    public static boolean hasChromeUserProfile(Path userDataDir, com.kazurayam.webdriverfactory.UserProfile userProfile) throws IOException {
        return findEdgeUserProfile(userDataDir, userProfile) != null;
    }

    public static EdgeUserProfile findEdgeUserProfileByProfileDirectoryName(ProfileDirectoryName profileDirectoryName) throws IOException {
        return findEdgeUserProfileByProfileDirectoryName(getDefaultUserDataDir(), profileDirectoryName);
    }

    public static EdgeUserProfile findEdgeUserProfileByProfileDirectoryName(Path userDataDir, ProfileDirectoryName profileDirectoryName) throws IOException {
        List<EdgeUserProfile> edgeUserProfiles = getEdgeUserProfileList(userDataDir);
        for (EdgeUserProfile eup : edgeUserProfiles) {
            if (eup.getProfileDirectoryName().equals(profileDirectoryName)) {
                return eup;
            }

        }

        return null;
    }

    public static com.kazurayam.webdriverfactory.UserProfile findUserProfileByProfileDirectoryName(ProfileDirectoryName profileDirectoryName)
            throws IOException {
        return findUserProfileByProfileDirectoryName(getDefaultUserDataDir(), profileDirectoryName);
    }

    public static com.kazurayam.webdriverfactory.UserProfile findUserProfileByProfileDirectoryName(Path userDataDir, ProfileDirectoryName profileDirectoryName)
            throws IOException {
        return findEdgeUserProfileByProfileDirectoryName(userDataDir, profileDirectoryName).getUserProfile();
    }

    /**
     * @return String representaion in JSON of all ChromeUserProfiles found
     * @throws IOException TODO
     */
    public static String allEdgeUserProfilesAsString() throws IOException {
        List<EdgeUserProfile> userProfiles = getEdgeUserProfileList();
        Collections.sort(userProfiles);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int count = 0;
        for (EdgeUserProfile up : userProfiles) {
            if (count > 0) {
                sb.append(",");
            }
            sb.append(up.toString());
            count += 1;
        }

        sb.append("]");
        return sb.toString();
    }

    private EdgeUserProfileUtils() {
    }
}
