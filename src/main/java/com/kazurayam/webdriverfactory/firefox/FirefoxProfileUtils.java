package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.utils.OSIdentifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FirefoxProfileUtils {
    public static Path getDefaultUserDataDir() {
        if (OSIdentifier.isWindows()) {
            // It is important that this chromeProfilesPath ends with User Data and not with the profile folder
            // %HOME%\AppData\Roaming\Mozilla\Firefox\Profiles
            return Paths.get("C:", "Users", System.getProperty("user.name"), "AppData", "Roaming", "Mozilla", "Firefox", "Profiles");
        } else if (OSIdentifier.isMac()) {
            // ~/Library/Application Support/Firefox/Profiles
            return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("Firefox").resolve("Profiles");
        } else if (OSIdentifier.isUnix()) {
            // ~/.mozilla/firefox
            return Paths.get(System.getProperty("user.home")).resolve(".mozilla").resolve("firefox");
        } else {
            throw new IllegalStateException("Windows, Mac, Linux are supported. Other platforms are not supported.");
        }

    }

    public static Optional<FirefoxUserProfile> findFirefoxUserProfileOf(UserProfile userProfile)
            throws IOException {
        List<FirefoxUserProfile> list = getFirefoxUserProfileList();
        // println "list.size is ${list.size()}"
        for (FirefoxUserProfile fup : list) {
            if (fup.getProfileDirectoryName().getName().endsWith(userProfile.getName())) {
                return Optional.of(fup);
            }
        }
        return Optional.empty();
    }

    public static List<FirefoxUserProfile> getFirefoxUserProfileList() throws IOException {
        return getFirefoxUserProfileList(getDefaultUserDataDir());
    }

    /**
     * $ cd ~/Library/ApplicationSupport/Firefox/Profiles
     * :~/Library/ApplicationSupport/Firefox/Profiles
     * $ ls -la
     * ]total 0
     * drwx------@  5 kazuakiurayama  staff   160  4  1  2021 .
     * drwx------   8 kazuakiurayama  staff   256  1 11 09:17 ..
     * drwx------@ 51 kazuakiurayama  staff  1632  1 11 16:03 0iyozca2.kazurayam
     * drwx------@  4 kazuakiurayama  staff   128  3  5  2020 1kjalvvf.default
     * drwx------@ 48 kazuakiurayama  staff  1536  4  1  2021 yqym488l.default-release
     * :~/Library/ApplicationSupport/Firefox/Profiles
     * $ ls 0iyozca2.kazurayam/ | grep cookie
     * cookies.sqlite
     * cookies.sqlite-wal
     */
    public static List<FirefoxUserProfile> getFirefoxUserProfileList(final Path userDataDir)
            throws IOException {
        Objects.requireNonNull(userDataDir);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.valueOf(userDataDir) + " is not present");
        }

        List<FirefoxUserProfile> userProfiles = new ArrayList<>();
        List<Path> dirs = Files.list(userDataDir).collect(Collectors.toList());
        for (Path dir : dirs) {
            String dirName = dir.getFileName().toString();
            FirefoxUserProfile fup = new FirefoxUserProfile(userDataDir,
                    new ProfileDirectoryName(dirName));
            userProfiles.add(fup);
        }

        return userProfiles;
    }

    /**
     *
     */
    public static String allFirefoxUserProfilesAsString() throws IOException {
        List<FirefoxUserProfile> userProfiles = getFirefoxUserProfileList();
        Collections.sort(userProfiles);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int count = 0;
        for (FirefoxUserProfile up : userProfiles) {
            if (count > 0) {
                sb.append(",");
            }

            sb.append(up.toString());
            count += 1;
        }

        sb.append("]");
        return sb.toString();
    }

}
