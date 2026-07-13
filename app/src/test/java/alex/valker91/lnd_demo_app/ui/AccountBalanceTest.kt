package alex.valker91.lnd_demo_app.ui

import alex.valker91.lnd_demo_app.ui.page.FirstPage
import com.tngtech.java.junit.dataprovider.DataProvider
import com.tngtech.java.junit.dataprovider.DataProviderRunner
import com.tngtech.java.junit.dataprovider.UseDataProvider
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import java.nio.file.Paths

@RunWith(DataProviderRunner::class)
class AccountBalanceTest {

    private lateinit var driver: AppiumDriver

    @Before
    fun setUp() {
        val runId = "run_" + java.util.UUID.randomUUID().toString().take(8)
        println("Log: Генерируем новый RunId для этого запуска: $runId")

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
        capabilities.setCapability("appium:optionalIntentArguments", "-e test_run_id $runId")
        driver = AndroidDriver(URL("http://127.0.0.1:4724"), capabilities)
    }

    @Test
    fun negativeAccountBalanceTest() {
        val incorrectAccountBalance: String = "1234567890"
        val firstPage: FirstPage = FirstPage(driver as AndroidDriver).sendTextToEtAccountNumber(incorrectAccountBalance).clickBtnGetBalances()
        val toastText = firstPage.getTextFromToast()
        Assert.assertTrue(toastText.contains("failed to connect to /10.68.84.61 (port 8080) from /10.0.2.16"))
    }


    companion object {
        @DataProvider
        @JvmStatic
        fun networkSpeedsMatrix(): Array<Any> {
//            return arrayOf(
//                "gsm",
//                "edge",
//                "hsdpa",
//                "lte",
//                "full"
//            )
            return arrayOf(
                "gsm",
                "full",
                "hsdpa",
                "lte",
                "edge"
            )
        }
    }

    @Test
    @UseDataProvider("networkSpeedsMatrix")
    fun positiveSimpleTest(speed: String) {
        val androidDriver = driver as AndroidDriver
        changeNetworkSpeed(speed)

        val startTime = System.currentTimeMillis()
        val toastText: String = FirstPage(androidDriver)
            .openSecondPage()
            .clickButton()
            .getTextFromToast()

        val durationMs = System.currentTimeMillis() - startTime
        println("Log: Тост получен за $durationMs мс на скорости $speed")

        Assert.assertTrue(
            "Тост Success не появился на скорости: $speed",
            toastText.contains("Success")
        )
    }

    private fun changeNetworkSpeed(speed: String) {
        println("Log: Меняем скорость сети на: $speed ")
        try {
            val process = ProcessBuilder("cmd.exe", "/c", "adb emu network speed $speed").start()
            process.waitFor()
            Thread.sleep(1000)
        } catch (e: Exception) {
            println("Ошибка при смене сети: ${e.message}")
        }
    }

    @After
    fun tearDown() {
        driver.quit()
    }
}

// C:\Users\AliaksandrKreyer\Desktop\EPM-PAY\Lnd_demo_app\app\build\reports\tests\testDebugUnitTest  index