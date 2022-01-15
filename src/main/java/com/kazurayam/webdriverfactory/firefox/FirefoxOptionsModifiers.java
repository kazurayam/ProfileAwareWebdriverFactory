package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class FirefoxOptionsModifiers {

    public enum Type {
        headless,
        windowSize,
        windowSize1024x768,
        withProfile,
        withProfileDirectoryName,
    }

    public static FirefoxOptionsModifier headless() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<FirefoxOptions, List<Object>, FirefoxOptions> modifier = (opts, args) -> {
            opts.setHeadless(true);
            return opts;
        };
        return new Base(Type.headless, modifier, arguments);
    }

    public static FirefoxOptionsModifier windowSize1024_768() {
        return windowSize(1024, 768);
    }

    public static FirefoxOptionsModifier windowSize(
            final Integer width,
            final Integer height)
    {
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);
        if (width <= 0) {
            throw new IllegalArgumentException(String.format("width=%d must be a positive integer", width));
        }
        if (height <= 0) {
            throw new IllegalArgumentException(String.format("height=%d must be a positive integer", height));
        }
        List<Object> arguments = Arrays.asList(width, height);
        BiFunction<FirefoxOptions, List<Object>, FirefoxOptions> modifier = (opts, args) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("2 arguments (width, height) are required");
            }
            Integer w = (Integer)args.get(0);
            Integer h = (Integer)args.get(1);
            opts.addArguments(String.format("--width=%d", w));
            opts.addArguments(String.format("--height=%d", h));
            return opts;
        };
        return new Base(Type.windowSize, modifier, arguments);
    }

    public static FirefoxOptionsModifier withProfile(final String profileName) {
        Objects.requireNonNull(profileName);
        List<Object> arguments = Collections.singletonList(profileName);
        BiFunction<FirefoxOptions, List<Object>, FirefoxOptions> modifier = (opts, args) -> {
            assert args.size() > 1;
            assert args.get(0) instanceof String;
            String pn = (String)args.get(0);
            FirefoxProfile profile = new ProfilesIni().getProfile(pn);
            opts.setProfile(profile);
            return opts;
        };
        return new Base(Type.withProfile, modifier, arguments);
    }

    public static FirefoxOptionsModifier withProfileDirectoryName(
            final Path userDataDir,
            final ProfileDirectoryName profileDirectoryName)
    {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        List<Object> arguments = Arrays.asList(userDataDir, profileDirectoryName);
        BiFunction<FirefoxOptions, List<Object>, FirefoxOptions> modifier = (opts, args) -> {
            assert args.size() >= 2;
            assert args.get(0) instanceof Path;
            assert args.get(1) instanceof ProfileDirectoryName;
            Path udd = (Path)args.get(0);
            ProfileDirectoryName pdn = (ProfileDirectoryName)args.get(1);
            Path profileDir = udd.resolve(pdn.getName());
            // http://kb.mozillazine.org/Command_line_arguments
            FirefoxProfile profile = new FirefoxProfile(profileDir.toFile());
            opts.setProfile(profile);
            return opts;
        };
        return new Base(Type.withProfileDirectoryName, modifier, arguments);
    }

    /**
     *
     */
    public static class Base implements FirefoxOptionsModifier {

        private final Type type;
        private final BiFunction<FirefoxOptions, List<Object>, FirefoxOptions> fn;
        private final List<Object> arguments;

        Base(Type type,
             BiFunction<FirefoxOptions, List<Object>, FirefoxOptions> fn,
             List<Object> arguments) {
            this.type = type;
            this.fn = fn;
            this.arguments = arguments;
        }

        @Override
        public Type getType() {
            return this.type;
        }

        @Override
        public FirefoxOptions modify(FirefoxOptions firefoxOptions) {
            return fn.apply(firefoxOptions, arguments);

        }


        @Override
        public boolean equals(Object obj) {
            if (! (obj instanceof Base)) {
                return false;
            }

            Base other = (Base) obj;
            return this.getType().equals(other.getType());
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
