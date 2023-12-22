package com.kazurayam.webdriverfactory.edge;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.CapabilityType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class EdgeOptionsModifiers {

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

    /*
    https://stackoverflow.com/questions/58062629/how-to-add-arguments-to-edgeoptions-in-using-edgedriver-selenium
     */
    private EdgeOptions addEdgeOptions(EdgeOptions options, String value) {
        Object givenArgs = options.getCapability("args");
        if (givenArgs != null) {
        }
        //
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        List<String> args = Arrays.asList(value);
        Map<String, Object> map = new HashMap<>();
        map.put("args", args);
    }

    public static EdgeOptionsModifier disableDevShmUsage() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("disable-dev-shm-usage");
            return opts;
        };
        return new Base(Type.disableDevShmUsage, modifier, arguments);
    }

    public static EdgeOptionsModifier disableExtensions() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("disableExtensions");
            return opts;
        };
        return new Base(Type.disableExtensions, modifier, arguments);
    }

    public static EdgeOptionsModifier disableGpu() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("disable-gpu");
            return opts;
        };
        return new Base(Type.disableGpu, modifier, arguments);
    }

    public static EdgeOptionsModifier disableInfobars() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("disable-infobars");
            return opts;
        };
        return new Base(Type.disableInfobars, modifier, arguments);
    }

    public static EdgeOptionsModifier headless() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("--headless");
            return opts;
        };
        return new Base(Type.headless, modifier, arguments);
    }

    public static EdgeOptionsModifier incognito() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("--incognito");
            return opts;
        };
        return new Base(Type.incognito, modifier, arguments);
    }

    public static EdgeOptionsModifier noSandbox() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("--no-sandbox");
            return opts;
        };
        return new Base(Type.noSandbox, modifier, arguments);
    }

    public static EdgeOptionsModifier singleProcess() {
        List<Object> arguments = Collections.emptyList();
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
            opts.addArguments("--single-process");
            return opts;
        };
        return new Base(Type.singleProcess, modifier, arguments);
    }

    public static EdgeOptionsModifier windowSize1024_768() {
        return windowSize(1024, 768);
    }

    public static EdgeOptionsModifier windowSize(
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
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
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

    public static EdgeOptionsModifier withProfileDirectoryName(
            final Path userDataDir,
            final ProfileDirectoryName profileDirectoryName)
    {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        List<Object> arguments = Arrays.asList(userDataDir, profileDirectoryName);
        BiFunction<EdgeOptions, List<Object>, EdgeOptions> modifier = (opts, args) -> {
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
    public static class Base implements EdgeOptionsModifier {

        private final Type type;
        private final BiFunction<EdgeOptions, List<Object>, EdgeOptions> fn;
        private final List<Object> arguments;

        Base(Type type,
             BiFunction<EdgeOptions, List<Object>, EdgeOptions> fn,
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
        public EdgeOptions modify(EdgeOptions edgeOptions) {
            return fn.apply(edgeOptions, arguments);
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
