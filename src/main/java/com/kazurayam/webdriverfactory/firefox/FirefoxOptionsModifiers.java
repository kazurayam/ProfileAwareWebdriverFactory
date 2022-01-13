package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;

import java.nio.file.Path;
import java.util.Objects;

public enum FirefoxOptionsModifiers {
    ;

    public static FirefoxOptionsModifier headless() {
        FirefoxOptionsModifier fom = new Base(FirefoxOptionsModifier.Type.headless, new Closure<FirefoxOptions>(null, null) {
            public FirefoxOptions doCall(FirefoxOptions firefoxOptions) {
                firefoxOptions.setHeadless(true);
                return firefoxOptions;
            }

        });
        return fom;
    }

    public static FirefoxOptionsModifier windowSize1024_768() {
        return windowSize(1024, 768);
    }

    public static FirefoxOptionsModifier windowSize(final int width, final int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width=" + String.valueOf(width) + " must be a positive integer");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("height=" + String.valueOf(height) + " must be a positive integer");
        }

        FirefoxOptionsModifier fom = new Base(FirefoxOptionsModifier.Type.windowSize, new Closure<FirefoxOptions>(null, null) {
            public FirefoxOptions doCall(FirefoxOptions firefoxOptions) {
                firefoxOptions.addArguments("--width=" + String.valueOf(width));
                firefoxOptions.addArguments("--height=" + String.valueOf(height));
                return firefoxOptions;
            }

        });
        return fom;
    }

    public static FirefoxOptionsModifier withProfile(final String profileName) {
        Objects.requireNonNull(profileName);
        FirefoxOptionsModifier fom = new Base(FirefoxOptionsModifier.Type.withProfile, new Closure<FirefoxOptions>(null, null) {
            public FirefoxOptions doCall(FirefoxOptions firefoxOptions) {
                FirefoxProfile profile = new ProfilesIni().getProfile(profileName);
                firefoxOptions.setProfile(profile);
                return firefoxOptions;
            }

        });
        return fom;
    }

    public static FirefoxOptionsModifier withProfileDirectoryName(final Path userDataDir, final ProfileDirectoryName profileDirectoryName) {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        FirefoxOptionsModifier fom = new Base(FirefoxOptionsModifier.Type.withProfileDirectoryName, new Closure<FirefoxOptions>(null, null) {
            public FirefoxOptions doCall(FirefoxOptions firefoxOptions) {
                Path profileDir = userDataDir.resolve(profileDirectoryName.getName());
                // http://kb.mozillazine.org/Command_line_arguments
                FirefoxProfile profile = new FirefoxProfile(profileDir.toFile());
                firefoxOptions.setProfile(profile);
                return firefoxOptions;
            }

        });
        return fom;
    }

    /**
     *
     */
    private static class Base implements FirefoxOptionsModifier {
        public Base(Type type, Closure closure) {
            this.type = type;
            this.closure = closure;
        }

        @Override
        public FirefoxOptions modify(FirefoxOptions chromeOptions) {
            Objects.requireNonNull(chromeOptions);
            return (FirefoxOptions) closure.call(chromeOptions);
        }

        @Override
        public Type getType() {
            return this.type;
        }

        @Override
        public boolean equals(Object obj) {
            if (!DefaultGroovyMethods.asBoolean(obj) instanceof Base) {
                return false;
            }

            Base other = (Base) obj;
            return this.getType().equals(other.getType());
        }

        @Override
        public int hashCode() {
            return this.getType().hashCode();
        }

        private final Type type;
        private final Closure closure;
    }
}
