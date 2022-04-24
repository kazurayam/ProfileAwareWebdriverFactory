package com.kazurayam.webdriverfactory;

import com.kazurayam.webdriverfactory.TestUtils
import org.junit.BeforeClass;
import org.junit.Test

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths
import java.nio.file.StandardCopyOption;

public class TestUtilsTest {

    static Path outputFolder

    @BeforeClass
    static void beforeClass() {
        outputFolder = Paths.get(".").resolve("build/tmp/testOutput")
                .resolve(TestUtilsTest.class.getSimpleName())
        Files.createDirectories(outputFolder)
    }

    @Test
    void test_filesAreIdentical() {
        Path file = Paths.get("build.gradle")
        assert TestUtils.filesAreIdentical(file, file)
    }

    @Test
    void test_filesAreIdentical_large() {
        Path file = Paths.get("./src/web/SDG_DSD_MATRIX.1.7.xlsm")
        Path copied = outputFolder
                        .resolve("test_filesAreIdentical_large")
                        .resolve("copied.xlsm")
        Files.createDirectories(copied.getParent())
        Files.copy(file, copied, StandardCopyOption.REPLACE_EXISTING)
        assert TestUtils.filesAreIdentical(file, copied)
    }

}
