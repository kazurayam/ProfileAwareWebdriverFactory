package com.kazurayam.webdriverfactory.chrome;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * FIXME
 * I can move the capability of setting "download.prompt_for_download => false" etc
 * from ChromePreferencesModifiers to ChromeOptionsModifiers
 * and remove ChromePreferencesModifiers.
 * This makes me safely work around the incompatibility between Selenium 3 vs Selenium 4
 * I should remove ChromePreferencesModifier so that I can use
 * this library in both Selenium3 and Selenium4
 * https://stackoverflow.com/questions/36901891/chromeoptions-to-not-prompt-for-download-when-running-remotedriver
 */
public class ChromePreferencesModifiers {

    enum Type {
        downloadWithoutPrompt,
        downloadIntoDirectory,
        disableViewersOfFlashAndPdf,
    }

    static ChromePreferencesModifier downloadWithoutPrompt() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<Map<String,Object>, List<Object>, Map<String,Object>> modifier = (prefs, args) -> {
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("download.prompt_for_download", false);
            return prefs;
        };
        return new Base(Type.downloadWithoutPrompt, modifier, arguments);
    }

    static ChromePreferencesModifier downloadIntoDirectory(Path directory) {
        Objects.requireNonNull(directory);
        List<Object> arguments = Collections.singletonList(directory);
        BiFunction<Map<String,Object>, List<Object>, Map<String,Object>> modifier = (prefs, args) -> {
            if (args.size() < 1) {
                throw new IllegalArgumentException("Path directory argument is required");
            }
            Path dir = (Path) args.get(0);
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("download.default_directory", dir.toAbsolutePath().normalize().toString());
            return prefs;
        };
        return new Base(Type.downloadIntoDirectory, modifier, arguments);
    }

    static ChromePreferencesModifier disableViewersOfFlashAndPdf() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<Map<String,Object>, List<Object>, Map<String,Object>> modifier = (prefs, args) -> {
            prefs.put("plugins.plugins_disabled",
                    Arrays.asList("Adobe Flash Player", "Chrome PDF Viewer"));
            return prefs;
        };
        return new Base(Type.disableViewersOfFlashAndPdf, modifier, arguments);
    }



    /**
     *
     */
    static class Base implements ChromePreferencesModifier {

        private final Type type;
        private final BiFunction<Map<String, Object>, List<Object>, Map<String, Object>> fn;
        private final List<Object> arguments;

        Base(Type type, BiFunction<Map<String, Object>, List<Object>, Map<String, Object>> fn, List<Object> arguments) {
            this.type = type;
            this.fn = fn;
            this.arguments = arguments;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, Object> modify(Map<String, Object> chromePreferences) {
            return fn.apply(chromePreferences, arguments);
        }

        /**
         * if this.getType() == other.getType(), this equals other
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ChromePreferencesModifiers.Base)) {
                return false;
            }
            ChromePreferencesModifiers.Base other = (ChromePreferencesModifiers.Base)obj;
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
