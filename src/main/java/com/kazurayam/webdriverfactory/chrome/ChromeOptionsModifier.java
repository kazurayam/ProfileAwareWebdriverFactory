package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.util.List;

public enum ChromeOptionsModifier {

    disableDevShmUsage((options, arguments) -> {
        options.addArguments("disable-dev-shm-usage");
        return options;
    }),

    disableExtensions((options, arguments) -> {
        options.addArguments("disableExtensions");
        return options;
    }),

    disableGpu((options, arguments) -> {
        options.addArguments("disable-gpu");
        return options;
    }),

    disableInfobars((options, arguments) -> {
        options.addArguments("disable-infobars");
        return options;
    }),

    headless((options, arguments) -> {
        options.addArguments("--headless");
        return options;
    }),

    incognito((options, arguments) -> {
        options.addArguments("--incognito");
        return options;
    }),

    noSandbox((options, arguments) -> {
        options.addArguments("--no-sandbox");
        return options;
    }),

    singleProcess((options, arguments) -> {
        options.addArguments("--single-process");
        return options;
    }),

    windowSize1024_768((options, arguments) -> {
        options.addArguments("window-size=1024,768");
        return options;
    }),

    windowSize((options, arguments) -> {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("2 arguments (width, height) are requires");
        }
        Integer width = (Integer)arguments.get(0);
        Integer height = (Integer)arguments.get(1);
        if (width <= 0) {
            throw new IllegalArgumentException("width=" + width + " must be a positive integer");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height=" + height + " must be a positive integer");
        }
        options.addArguments("window-size=" + width + "," + height);
        return options;
    }),

    withProfileDirectoryName((options, arguments) -> {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("2 arguments (userDataDir, projectDirectoryName) are required");
        }
        Path userDataDir = (Path)arguments.get(0);
        ProfileDirectoryName profileDirectoryName = (ProfileDirectoryName)arguments.get(1);
        options.addArguments("user-data-dir=" + userDataDir);
        options.addArguments("profile-directory=" + profileDirectoryName.getName());
        return options;
    }),

    ; // the end of enumeration


    /**
     *
     */
    private final ChromeOptionsModifyFunction fn;
    ChromeOptionsModifier(ChromeOptionsModifyFunction fn) {
        this.fn = fn;
    }
    ChromeOptions apply(ChromeOptions options, List<Object> arguments) {
        return fn.modify(options, arguments);
    }
}
