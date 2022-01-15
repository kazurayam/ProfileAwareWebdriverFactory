package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifiers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class FirefoxPreferencesModifiers {

    enum Type {
        downloadIntoUserHomeDownloadsDirectory,
        downloadIntoDirectory,
        downloadWithoutPrompt,
    }

    downloadIntoUserHomeDownloadsDirectory((preferences, arguments) -> {
        Path p = Paths.get(System.getProperty("user.home"), "Downloads");
        Map<String, Object> r = downloadIntoDirectory.apply(
                preferences, Collections.singletonList(p));
        return r;
    }),

    static FirefoxPreferencesModifier downloadIntoDirectory((preferences, arguments) -> {
        preferences.put("browser.download.useDownloadDir", true);
        preferences.put("browser.download.folderList", 2);
        Path downloads = Paths.get(System.getProperty("user.home"), "Downloads");
        preferences.put("browser.download.dir", downloads.toString());
        return preferences;
    }),

    /**
     * Don't show the dialong to confirm if you really want to download a file
     */
    static FirefoxPreferencesModifier downloadWithoutPrompt() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<Map<String, Object>, List<Object>, Map<String, Object>> modifier = (prefs, args) -> {
            prefs.put("browser.helperApps.neverAsk.saveToDisk", getAllMimeTypesAsString());
            prefs.put("browser.helperApps.neverAsk.openFile", getAllMimeTypesAsString());
            return prefs;
        }
        return new Base(FirefoxPreferencesModifiers.Type.downloadWithoutPrompt, modifier, arguments);
    }

    private static String getAllMimeTypesAsString() {
        List<String> mimeTypes = Arrays.asList(
                "application/gzip",
                "application/java-archive",
                "application/json",
                "application/msexcel",
                "application/msword",
                "application/octet-stream",
                "application/pdf",
                "application/vnd-ms-office",
                "application/vnd-xls",
                "application/vnd.ms-excel",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/x-dos_mx_excel",
                "application/x-excel",
                "application/x-ms-excel",
                "application/x-msexcel",
                "application/x-tar",
                "application/x-xls",
                "application/x-zip-compressed",
                "application/xls",
                "application/xml",
                "application/zip",
                "application/zlib",
                "image/bmp",
                "image/gif",
                "image/jpeg",
                "image/png",
                "image/svg+xml",
                "text/csv",
                "text/plain",
                "text/xml");
        return String.join(",", mimeTypes);
    }


    /**
     *
     */
    static class Base implements FirefoxPreferencesModifier {

        private final Type type;
        private final BiFunction<Map<String, Object>, List<Object>, Map<String, Object>> fn;
        private final List<Object> arguments;

        Base(FirefoxPreferencesModifiers.Type type, BiFunction<Map<String, Object>, List<Object>, Map<String, Object>> fn, List<Object> arguments) {
            this.type = type;
            this.fn = fn;
            this.arguments = arguments;
        }

        @Override
        public FirefoxPreferencesModifiers.Type getType() {
            return type;
        }

        @Override
        public Map<String, Object> modify(Map<String, Object> firefoxPreferences) {
            return fn.apply(firefoxPreferences, arguments);
        }

        /**
         * if this.getType() == other.getType(), this equals other
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FirefoxPreferencesModifiers.Base)) {
                return false;
            }
            FirefoxPreferencesModifiers.Base other = (FirefoxPreferencesModifiers.Base)obj;
            return this.getType() == other.getType();
        }

        @Override
        public int hashCode() {
            return this.getType().hashCode();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"");
            sb.append(getType().toString());
            sb.append("\",\"argument\":[");
            for (int i = 0; i < arguments.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                Object obj = arguments.get(i);
                sb.append("\"");
                sb.append(obj.toString());
                sb.append("\"");
            }
            sb.append("]");
            sb.append("}");
            return sb.toString();
        }
    }


}
