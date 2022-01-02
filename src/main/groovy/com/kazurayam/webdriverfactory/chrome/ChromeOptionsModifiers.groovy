package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeOptions

import java.nio.file.Files
import java.nio.file.Path

class ChromeOptionsModifiers {

    static ChromeOptionsModifier headless() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("--headless")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier incognito() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("--incognito")
            return chromeOptions
        })
        return com
    }

    /**
     * set window-size=1027,768
     * @return
     */
    static ChromeOptionsModifier windowSize1024_768() {
        return windowSize(1024, 768)
    }

    static ChromeOptionsModifier windowSize(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width=${width} must be a positive integer")
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height=${height} must be a positive integer")
        }
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("window-size=${width},${height}")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier noSandbox() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("--no-sandbox")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier disableInfobars() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("disable-infobars")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier disableGpu() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("disable-gpu")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier disableDevShmUsage() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("disable-dev-shm-usage")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier disableExtensions() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("disableExtensions")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier singleProcess() {
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("--single-process")
            return chromeOptions
        })
        return com
    }

    static ChromeOptionsModifier withUserProfile(Path userDataDir, String profileDirectoryName) {
        Objects.requireNonNull(userDataDir)
        Objects.requireNonNull(profileDirectoryName)
        if (!Files.exists(userDataDir)) {
            throw new IllegalStateException("${userDataDir} is not found")
        }
        ChromeOptionsModifier com = new Base({ ChromeOptions chromeOptions ->
            chromeOptions.addArguments("user-data-dir=${userDataDir.toString()}")
            chromeOptions.addArguments("profile-directory=${profileDirectoryName}")
            return chromeOptions
        })
        return com

    }

    /**
     *
     */
    private static class Base implements ChromeOptionsModifier {
        private Closure closure
        Base(Closure closure) {
            this.closure = closure
        }
        @Override
        ChromeOptions modify(ChromeOptions chromeOptions) {
            Objects.requireNonNull(chromeOptions)
            return (ChromeOptions)closure.call(chromeOptions)
        }
    }

    private ChromeOptionsModifiers() {}
}
