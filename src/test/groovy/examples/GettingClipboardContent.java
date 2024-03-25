package examples;

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory;
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers;
import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifiers;
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of a Tips suggested at
 * [Getting Clipboard content from remote Selenium Chrome Nodes](https://sahajamit.medium.com/getting-clipboard-content-from-remote-selenium-chrome-nodes-67a4c4d862bd)
 * by AMIT RAWAT
 *
 * Some web page has a button labeled "copy link".
 * Here is an example:
 *     https://codepen.io/RevCred/pen/vxXrww
 * If you click the button, a URL is copied into the clipboard of the machine
 * on which the browser is working.
 *
 * Now I want to write a Selenium test case which verifies
 * if the URL written into the clipboard matches is as expected or not.
 *
 * In order to do that, I need to read the clipboard content and
 * transfer the text back to the test case script.
 * You can use JavaScript's Clipboard.readText()
 * https://developer.mozilla.org/en-US/docs/Web/API/Clipboard/readText
 *
 * When you try to call Clipboard.readText() from your Selenium test,
 * you would be blocked by a Dialog from browser. Browser ask you if
 * you grant JavaScript to get access to the clipboard or not.
 *
 * The above-mentioned article explains that you can grant it by
 * setting the Chrome preference `profile.content_settings.exceptions.clipboard`
 * What this test case does?
 * 1. It opens a Chrome browser specifying a configures preference
 * 2. It visits the target page "https://codepen.io/RevCred/pen/vxXrww"
 * 3. It clicks the button. By this, a URL string will be written into the OS clipboard
 * 4. It sends a JavaScript script to the Chrome browser;
 *    Chrome will execute the script;
 *    The JavaScript will return a URL string back to the test script.
 * 5. It verifies if the returned URL string is equal to the expected.
 */
@Ignore
public class GettingClipboardContent {
    private final String targetURL = "https://codepen.io/RevCred/pen/vxXrww";
    private final String iframeLocator = "//iframe[@id='result']";
    private final String buttonLocator = "//div[@id='copy']";
    private WebDriver driver;

    @BeforeClass
    public static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setup() throws IOException {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory();

        // If I turn Chrome to be Headless, this test will fail.
        // I don't know why. It could be that the target web page does not work healthy
        // if the browser is headless.
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless());

        // modify Chrome Preferences to grant access to Clipboard
        cdf.addChromePreferencesModifier(
                ChromePreferencesModifiers.grantAccessToClipboard());

        LaunchedChromeDriver launched = cdf.newChromeDriver();
        driver = launched.getDriver();
    }

    @Test
    public void testReadingClipboard() throws InterruptedException {
        driver.navigate().to(targetURL);
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        WebElement iframe = wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("result")));
        driver.switchTo().frame(iframe);
        WebElement button = wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("copy")));
        button.click();
        Thread.sleep(1 * 1000);
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor)driver;
            Object val = js.executeScript("return navigator.clipboard.readText();");
            if (val instanceof String) {
                String text = (String)val;
                System.out.println("*** Text out of clipboard: " + text);
                assert text.startsWith("https://staging.revolutioncredit.com/signupc/");
            }
        }

    }

    @After
    public void teardown() {
        driver.quit();
    }

}
