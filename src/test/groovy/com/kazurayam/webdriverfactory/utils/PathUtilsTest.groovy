package com.kazurayam.webdriverfactory.utils

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static org.junit.Assert.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class PathUtilsTest {

    static Path outputDir
    Path sourceDir

    @BeforeClass
    static void beforeClass() {
        WebDriverManager.chromedriver().setup()
        outputDir = Paths.get(".").resolve("build/tmp/testOutput")
                .resolve(PathUtilsTest.class.getSimpleName())
        if (Files.exists(outputDir)) {
            outputDir.deleteDir()
        }
        Files.createDirectories(outputDir)
    }

    @Before
    void setup() {
        sourceDir = outputDir.resolve("source")
        Files.createDirectories(sourceDir)
        makeFixture(sourceDir)
    }

    void makeFixture(Path sourceDir) {
        Path hello = sourceDir.resolve("hello.txt")
        hello.toFile().text = "Hello"
        Path a = sourceDir.resolve("a")
        Files.createDirectories(a)
        Path world = a.resolve("world.txt")
        world.toFile().text = "World"
        Path z = sourceDir.resolve("z")
        Files.createDirectories(z)
        Path again = z.resolve("again.txt")
        again.text = "again"
    }

    @Test
    void test_listDirectoryRecursively() {
        List<Path> list = PathUtils.listDirectoryRecursively(sourceDir)
        assertEquals(6, list.size())
        /*
        list.forEach({p ->
            println p
        })
         */
    }

    @Test
    void test_copyDirectoryRecursively() {
        String dirName = "dest" + DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())
        Path destDir = outputDir.resolve(dirName)
        Files.createDirectories(destDir)
        int num = PathUtils.copyDirectoryRecursively(sourceDir, destDir)
        List<Path> list = PathUtils.listDirectoryRecursively(destDir)
        assertEquals(6, list.size())
        /*
        list.forEach({ p ->
            println p })
         */
    }

    @Test
    void test_deleteDirectoryRecursively() {
        String dirName = "dest" + DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())
        Path destDir = outputDir.resolve(dirName)
        Files.createDirectories(destDir)
        int num = PathUtils.copyDirectoryRecursively(sourceDir, destDir)
        assertTrue( Files.exists(destDir) )
        //
        PathUtils.deleteDirectoryRecursively(destDir)
        assertTrue( !Files.exists(destDir) )
    }
}
