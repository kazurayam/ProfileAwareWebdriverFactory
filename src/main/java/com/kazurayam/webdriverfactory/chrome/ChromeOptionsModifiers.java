package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class ChromeOptionsModifiers {

    public enum Type {
        disableDevShmUsage,
        disableExtensions,
        disableGpu,
        disableInfobars,
        headless,
        incognito,
        noSandbox,
        singleProcess,
        windowSize,
        withProfileDirectoryName,
    }

    public static ChromeOptionsModifier disableDevShmUsage() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("disable-dev-shm-usage");
            return opts;
        };
        return new Base(Type.disableDevShmUsage, modifier, arguments);
    }

    public static ChromeOptionsModifier disableExtensions() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("disableExtensions");
            return opts;
        };
        return new Base(Type.disableExtensions, modifier, arguments);
    }

    public static ChromeOptionsModifier disableGpu() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("disable-gpu");
            return opts;
        };
        return new Base(Type.disableGpu, modifier, arguments);
    }

    public static ChromeOptionsModifier disableInfobars() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("disable-infobars");
            return opts;
        };
        return new Base(Type.disableInfobars, modifier, arguments);
    }

    public static ChromeOptionsModifier headless() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("--headless");
            return opts;
        };
        return new Base(Type.headless, modifier, arguments);
    }

    public static ChromeOptionsModifier incognito() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("--incognito");
            return opts;
        };
        return new Base(Type.incognito, modifier, arguments);
    }

    public static ChromeOptionsModifier noSandbox() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("--no-sandbox");
            return opts;
        };
        return new Base(Type.noSandbox, modifier, arguments);
    }

    public static ChromeOptionsModifier singleProcess() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            opts.addArguments("--single-process");
            return opts;
        };
        return new Base(Type.singleProcess, modifier, arguments);
    }

    public static ChromeOptionsModifier windowSize1024_768() {
        return windowSize(1024, 768);
    }

    public static ChromeOptionsModifier windowSize(
            final Integer width,
            final Integer height)
    {
        Objects.requireNonNull(width);
        Objects.requireNonNull(height);
        if (width <= 0) {
            throw new IllegalArgumentException("width=" + width + " must be a positive integer");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height=" + height + " must be a positive integer");
        }
        List<Object> arguments = Arrays.asList(width, height);
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            if (args.size() < 2) {
                throw new IllegalArgumentException("2 arguments (width, height) are requires");
            }
            Integer w = (Integer)args.get(0);
            Integer h = (Integer)args.get(1);
            opts.addArguments("window-size=" + w + "," + h);
            return opts;
        };
        return new Base(Type.windowSize, modifier, arguments);
    }

    public static ChromeOptionsModifier withProfileDirectoryName(
            final Path userDataDir,
            final ProfileDirectoryName profileDirectoryName)
    {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        List<Object> arguments = Arrays.asList(userDataDir, profileDirectoryName);
        BiFunction<ChromeOptions, List<Object>, ChromeOptions> modifier = (opts, args) -> {
            assert args.size() >= 2;
            assert args.get(0) instanceof Path;
            assert args.get(1) instanceof ProfileDirectoryName;
            Path udd = (Path)args.get(0);
            ProfileDirectoryName pdn = (ProfileDirectoryName)args.get(1);
            opts.addArguments("user-data-dir=" + udd);
            opts.addArguments("profile-directory=" + pdn.getName());
            return opts;
        };
        return new Base(Type.withProfileDirectoryName, modifier, arguments);
    }




    /**
     *
     */
    public static class Base implements ChromeOptionsModifier {

        private final Type type;
        private final BiFunction<ChromeOptions, List<Object>, ChromeOptions> fn;
        private final List<Object> arguments;

        Base(Type type,
             BiFunction<ChromeOptions, List<Object>, ChromeOptions> fn,
             List<Object> arguments) {
            this.type = type;
            this.fn = fn;
            this.arguments = arguments;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public ChromeOptions modify(ChromeOptions chromeOptions) {
            return fn.apply(chromeOptions, arguments);
        }

        /**
         * if this.getType() == other.getType(), this equals other
         * @param obj the other instance
         * @return true if the Type is equal
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Base)) {
                return false;
            }
            Base other = (Base)obj;
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
