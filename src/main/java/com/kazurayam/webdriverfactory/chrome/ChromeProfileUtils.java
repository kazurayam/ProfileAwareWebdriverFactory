package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
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

public final class ChromeProfileUtils {

    private static Logger logger_ = LoggerFactory.getLogger(ChromeProfileUtils.class);

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

    public static List<ChromeUserProfile> getChromeUserProfileList()
            throws IOException
    {
        return getChromeUserProfileList(getDefaultUserDataDir());
    }

    public static List<ChromeUserProfile> getChromeUserProfileList(final Path userDataDir)
            throws IOException
    {
        Objects.requireNonNull(userDataDir);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.format("%s is not present", userDataDir));
        }
        List<ChromeUserProfile> userProfiles = new ArrayList<>();
        List<Path> dirs = Files.list(userDataDir).collect(Collectors.toList());
        for (Path dir : dirs) {
            if (Files.exists(dir.resolve("Preferences"))) {
                ChromeUserProfile cp = new ChromeUserProfile(userDataDir, new CacheDirectoryName(dir.getFileName().toString()));
                userProfiles.add(cp);
            }
        }
        return userProfiles;
    }

    public static ChromeUserProfile findChromeUserProfile(UserProfile userProfile)
            throws IOException
    {
        return findChromeUserProfile(getDefaultUserDataDir(), userProfile);
    }

    public static ChromeUserProfile findChromeUserProfile(Path userDataDir, UserProfile userProfile)
            throws IOException
    {
        Objects.requireNonNull(userProfile);
        List<ChromeUserProfile> chromeUserProfiles = getChromeUserProfileList(userDataDir);
        for (ChromeUserProfile cup : chromeUserProfiles) {
            //System.out.println("[ChromeProfileFinder#getUserProfile] userProfile.getName()==${userProfile.getName()}, userProfile.getDirectoryName()=${userProfile.getDirectoryName()}")
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
    public static boolean hasChromeUserProfile(UserProfile userProfile) throws IOException {
        return hasChromeUserProfile(getDefaultUserDataDir(), userProfile);
    }

    public static boolean hasChromeUserProfile(Path userDataDir, UserProfile userProfile) throws IOException {
        return findChromeUserProfile(userDataDir, userProfile) != null;
    }

    public static ChromeUserProfile findChromeUserProfileByCacheDirectoryName(
            CacheDirectoryName cacheDirectoryName) throws IOException {
        return findChromeUserProfileByCacheDirectoryName(
                getDefaultUserDataDir(),
                cacheDirectoryName);
    }

    public static ChromeUserProfile findChromeUserProfileByCacheDirectoryName(
            Path userDataDir,
            CacheDirectoryName cacheDirectoryName) throws IOException {
        List<ChromeUserProfile> chromeUserProfiles = getChromeUserProfileList(userDataDir);
        for (ChromeUserProfile chromeUserProfile : chromeUserProfiles) {
            if (chromeUserProfile.getCacheDirectoryName().equals(cacheDirectoryName)) {
                return chromeUserProfile;
            }

        }

        return null;
    }

    public static UserProfile findUserProfileByCacheDirectoryName(CacheDirectoryName cacheDirectoryName)
            throws IOException {
        return findUserProfileByCacheDirectoryName(getDefaultUserDataDir(), cacheDirectoryName);
    }

    public static UserProfile findUserProfileByCacheDirectoryName(
            Path userDataDir,
            CacheDirectoryName cacheDirectoryName)
            throws IOException {
        return findChromeUserProfileByCacheDirectoryName(
                userDataDir, cacheDirectoryName).getUserProfile();
    }

    /**
     * @return String representaion in JSON of all ChromeUserProfiles found
     * @throws IOException TODO
     */
    public static String allChromeUserProfilesAsString() throws IOException {
        List<ChromeUserProfile> userProfiles = getChromeUserProfileList();
        Collections.sort(userProfiles);
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        int count = 0;
        for (ChromeUserProfile up : userProfiles) {
            if (count > 0) {
                sb.append(",\n");
            }
            sb.append("\t");
            sb.append(up.toString());
            count += 1;
        }

        sb.append("\n]");
        return sb.toString();
    }

    private ChromeProfileUtils() {
    }
}
