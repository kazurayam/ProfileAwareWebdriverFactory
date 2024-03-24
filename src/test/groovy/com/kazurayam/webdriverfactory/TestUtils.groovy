package com.kazurayam.webdriverfactory

import com.kazurayam.subprocessj.Subprocess
import com.kazurayam.subprocessj.Subprocess.CompletedProcess
import org.junit.Ignore

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

import static org.junit.Assert.*
import org.junit.Test

class TestUtils {

    static String getGitHubPersonalAccessToken() {
        String account = "kazurayam"
        String server  = "github.com"
        Subprocess subprocess = new Subprocess()
        subprocess.cwd(new File(System.getProperty("user.home")))
        CompletedProcess cp = subprocess.run(Arrays.asList(
                "security", "find-internet-password",
                "-s", server, "-a", account, "-w"))
        //System.out.println(cp.returncode())
        //cp.stdout().forEach({line -> println line })
        //cp.stderr().forEach({line -> println line })
        return cp.stdout().get(0).trim()
    }

    static boolean filesAreIdentical(Path file1, Path file2) {
        Objects.requireNonNull(file1)
        Objects.requireNonNull(file2)
        if ( ! Files.exists(file1)) {
            throw new IllegalArgumentException("${file1} is not present")
        }
        if ( ! Files.exists(file2)) {
            throw new IllegalArgumentException("${file2} is not present")
        }
        MessageDigest md = MessageDigest.getInstance("MD5")
        md.update(Files.readAllBytes(file1))
        byte[] digest1 = md.digest()
        md.update(Files.readAllBytes(file2))
        byte[] digest2 = md.digest()
        if (digest1.length == digest2.length) {
            boolean result = true
            for (int i = 0; i < digest1.length; i++) {
                if (digest1[i] != digest2[i]) {
                    result = false
                    break
                }
            }
            return result
        } else {
            return false
        }
    }

    @Ignore
    @Test
    void test_getGitHubPersonalAccessToken() {
        String pat = TestUtils.getGitHubPersonalAccessToken()
        assertNotNull(pat)
        assertTrue(pat.startsWith("ghp_"))
    }
}
