package com.kazurayam.webdriverfactory;

import com.kazurayam.webdriverfactory.TestUtils;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtilsTest {

    @Test
    void test_filesAreIdentical() {
        Path file = Paths.get("build.gradle")
        assert TestUtils.filesAreIdentical(file, file)
    }

}
