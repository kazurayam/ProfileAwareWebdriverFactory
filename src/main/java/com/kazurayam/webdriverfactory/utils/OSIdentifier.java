package com.kazurayam.webdriverfactory.utils;

/**
 * This tells you your current OS is Windows, Mac, Unix or Solaris
 *
 * @author kazurayam
 */
public class OSIdentifier {
    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    private static final String OS = System.getProperty("os.name").toLowerCase();
}
