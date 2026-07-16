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

    private lateinit val driver: AppiumDriver

    @Before
    fun setUp() {
        val runId = "run_" + java.util.UUID.randomUUID().toString().take(8)
        println("Log: Generating a new RunId for this execution: $runId")

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
    @UseDataProvider("accountBalanceNegativeMatrix")
    fun negativeAccountBalanceTest(invalidAccountNumber: String, scenarioDescription: String) {
        val firstPage = FirstPage(driver as AndroidDriver)
        firstPage.clickGetBalancesForAccount(invalidAccountNumber)

        val toastText = firstPage.getTextFromToast()
        Assert.assertTrue(
            "Expected connection error toast for scenario: $scenarioDescription. Actual: $toastText",
            toastText.contains("failed to connect to")
        )
    }

    @Test
    fun positiveAccountBalanceAlowOnlyLettersAndDigitsTest() {
        val validAccountNumber = "abc123"
        val firstPage = FirstPage(driver as AndroidDriver)

        firstPage.clickGetBalancesForAccount(validAccountNumber)

        Assert.assertFalse(
            "Account balance request should not show an error toast for alphanumeric input.",
            firstPage.hasToastContaining("failed to connect to")
        )
    }

    @DataProvider
    fun accountBalanceNegativeMatrix(): Array<Array<Any>> {
        return arrayOf(
            arrayOf("", "empty value"),
            arrayOf("    ", "whitespace only"),
            arrayOf("test@123", "special character at sign"),
            arrayOf("123-456", "hyphenated value"),
            arrayOf("abc_123", "underscore in value"),
            arrayOf("123 456", "embedded space"),
            arrayOf("!@#^%", "punctuation only")
        )
    }

    Companion object {
        @DataProvider
        @JvmStatic
        fun networkSpeedsMatrix(): Array<Any> {
            return arrayOf(
                "gsm",
                "full",
                "hsdpa",
                "lte",
                "edge"
            )
        }

        @DataProvider
        @JvmStatic
        fun createMoneyTransferNegativeMatrix(): Array<Array<Any>> {
            return arrayOf(
                arrayOf("", "1", "ABC123", "ZXY987", "payment", "empty amount"),
                arrayOf("-10", "1", "ABC123", "ZXY987", "payment", "negative amount"),
                arrayOf("100", "", "ABC123", "ZXY987", "payment", "empty client id from"),
                arrayOf("100", "1", "", "ZXY987", "payment", "empty account number from"),
                arrayOf("100", "1", "ABC123", "", "payment", "empty account number to"),
                arrayOf("100", "1", "ABC-123", "ZXY987", "payment", "account number from with hyphen"),
                arrayOf("100", "1", "ABC123", "ZXY_987", "payment", "account number to with underscore"),
                arrayOf("100", "1", "ABC123", "ZXY987", "", "empty comment")
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
        println("Log: Toast received in $durationMs ms at speed $speed")

        Assert.assertTrue(
            "Success toast did not appear at speed: $speed",
            toastText.contains("Success")
        )
    }

    @Test
    @UseDataProvider("createMoneyTransferNegativeMatrix")
    fun negativeCreateMoneyTransferTest(
        amount: String,
        clientIdFrom: String,
        accountNumberFrom: String,
        accountNumberTo: String,
        comment: String,
        scenarioDescription: String
    ) {
        val firstPage = FirstPage(driver as AndroidDriver)
        firstPage.createMoneyTransfer(
            amount,
            clientIdFrom,
            accountNumberFrom,
            accountNumberTo,
            comment
        )

        val toastText = firstPage.getTextFromToast()
        Assert.assertTrue(
            "Expected an error toast for create money transfer scenario: $scenarioDescription. Actual: $toastText",
            toastText.contains("failed to connect to")
        )
    }

    @Test
    fun positiveCreateMoneyTransferAllowOnlyLettersAndDigitsTest() {
        val firstPage = FirstPage(driver as AndroidDriver)
        firstPage.createMoneyTransfer(
            "100",
            "1",
            "ABC123",
            "ZXY987",
            "Payment001"
        )

        Assert.assertFalse(
            "Create money transfer should not show an error toast for alphanumeric input.",
            firstPage.hasToastContaining("failed to connect to")
        )
    }

    private fun changeNetworkSpeed(speed: String) {
        println("Log: Changing network speed to: $speed ")
        try {
            val process = ProcessBuilder("cmd.exe", "/c", "adb emu network speed $speed").start()
            process.waitFor()
            Thread.sleep(1000)
        } catch (e: Exception) {
            println("Error while changing network: ${e.message}")
        }
    }

    @After
    fun tearDown() {
        driver.quit()
    }
}
