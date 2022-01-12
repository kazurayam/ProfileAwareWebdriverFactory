package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.ProfileDirectoryName
import org.openqa.selenium.firefox.FirefoxOptions

import java.nio.file.Path

enum FirefoxOptionsModifiers {

    static FirefoxOptionsModifier headless() {
        FirefoxOptionsModifier fom = new Base(
                FirefoxOptionsModifier.Type.headless,
                { FirefoxOptions firefoxOptions ->
                    firefoxOptions.addArguments("-headless")
                    return firefoxOptions
                }
        )
        return fom
    }

    static FirefoxOptionsModifier windowSize1024_768() {
        return windowSize(1024, 768)
    }

    static FirefoxOptionsModifier windowSize(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width=${width} must be a positive integer")
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height=${height} must be a positive integer")
        }
        FirefoxOptionsModifier fom = new Base(
                FirefoxOptionsModifier.Type.windowSize,
                { FirefoxOptions firefoxOptions ->
                    firefoxOptions.addArguments("--window-size=${width},${height}")
                    return firefoxOptions
                }
        )
        return fom
    }

    /**
     *
     */
    static FirefoxOptionsModifier withProfileDirectoryName(Path userDataDir, ProfileDirectoryName profileDirectoryName) {
        Objects.requireNonNull(userDataDir)
        Objects.requireNonNull(profileDirectoryName)
        FirefoxOptionsModifier fom = new Base(
                FirefoxOptionsModifier.Type.withProfileDirectoryName,
                { FirefoxOptions firefoxOptions ->
                    Path profileDir = userDataDir.resolve(profileDirectoryName.getName())
                    // http://kb.mozillazine.org/Command_line_arguments
                    firefoxOptions.addPreference("-profile", profileDir.toString())
                    return firefoxOptions
                })
        return fom

    }

    /**
     *
     */
    private static class Base implements FirefoxOptionsModifier {
        private final Type type
        private final Closure closure
        Base(Type type, Closure closure) {
            this.type = type
            this.closure = closure
        }
        @Override
        FirefoxOptions modify(FirefoxOptions chromeOptions) {
            Objects.requireNonNull(chromeOptions)
            return (FirefoxOptions)closure.call(chromeOptions)
        }
        @Override
        Type getType() {
            return this.type
        }
        @Override
        boolean equals(Object obj) {
            if (! obj instanceof Base) {
                return false
            }
            Base other = (Base)obj
            return this.getType() == other.getType()
        }
        @Override
        int hashCode() {
            return this.getType().hashCode()
        }
    }

}
