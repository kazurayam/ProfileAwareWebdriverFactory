package com.kazurayam.webdriverfactory

import com.kazurayam.subprocessj.Subprocess
import com.kazurayam.subprocessj.Subprocess.CompletedProcess

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

    @Test
    void test_getGitHubPersonalAccessToken() {
        String pat = TestUtils.getGitHubPersonalAccessToken()
        assertNotNull(pat)
        assertTrue(pat.startsWith("ghp_"))
    }
}
