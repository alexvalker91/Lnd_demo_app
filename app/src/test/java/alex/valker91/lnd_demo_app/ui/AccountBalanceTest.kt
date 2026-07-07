package alex.valker91.lnd_demo_app.ui

import alex.valker91.lnd_demo_app.ui.page.FirstPage
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import java.nio.file.Paths

class AccountBalanceTest {

    private lateinit var driver: AppiumDriver

    @Before
    fun setUp() {
        val capabilities: DesiredCapabilities = DesiredCapabilities()
        capabilities.setCapability("appium:automationName", "UIAutomator2")
        capabilities.setCapability("appium:platformVersion", "16")
        capabilities.setCapability("appium:deviceName", "Medium Phone API 36.0")
        capabilities.setCapability("platformName", "Android")
        capabilities.setCapability(
            "appium:app",
            "${Paths.get("").toAbsolutePath()}/build/outputs/apk/debug/app-debug.apk"
        )
        capabilities.setCapability("appium:appWaitForLaunch", "false")
        driver = AndroidDriver(URL("http://127.0.0.1:4724"), capabilities)
    }

    @Test
    fun negativeAccountBalanceTest() {
        val incorrectAccountBalance: String = "1234567890"
        val firstPage: FirstPage = FirstPage(driver as AndroidDriver).sendTextToEtAccountNumber(incorrectAccountBalance).clickBtnGetBalances()
        val toastText = firstPage.getTextFromToast()
        Assert.assertTrue(toastText.contains("failed to connect to /10.68.84.61 (port 8080) from /10.0.2.16"))
    }

    @After
    fun tearDown() {
        driver.quit()
    }
}