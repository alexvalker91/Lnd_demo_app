package alex.valker91.lnd_demo_app.ui.page

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By.xpath
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class FirstPage(private val driver: AndroidDriver) : AbstractPage(driver) {

    @FindBy(id = "etAccountNumber")
    private val etAccountNumber: WebElement? = null

    @FindBy(id = "btnGetBalances")
    private val btnGetBalances: WebElement? = null

    @FindBy(id = "fabAction")
    private val fabAction: WebElement? = null

    @FindBy(xpath = "//android.widget.Toast[contains(@text, 'failed to connect to')]")
    private val toastMessage: WebElement? = null

    public fun sendTextToEtAccountNumber(text: String): FirstPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.visibilityOf(etAccountNumber))
        etAccountNumber?.sendKeys(text)
        return this
    }

    public fun clickBtnGetBalances(): FirstPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.elementToBeClickable(btnGetBalances))
        btnGetBalances?.click()
        System.err.println("Log: 123 getEmulatorNetworkStatus = ${getEmulatorNetworkStatus()}")
        println("Log: 456 getEmulatorNetworkStatus = ${getEmulatorNetworkStatus()}")
        return this
    }

    private fun getEmulatorNetworkStatus(): String {
        return try {
            val process = ProcessBuilder("cmd.exe", "/c", "adb emu network status").start()
            process.waitFor()
            val output = process.inputStream.bufferedReader().readText().trim()
            when {
                output.contains("14400 bits/s") -> "gsm"
                output.contains("473600 bits/s") -> "edge"
                output.contains("14400000 bits/s") -> "hsdpa"
                output.contains("173000000 bits/s") -> "lte"
                output.contains("0 bits/s") || output.contains("no limit") -> "full"
                else -> "Неизвестная скорость: \n$output"
            }
        } catch (e: Exception) {
            "Ошибка при получении статуса сети"
        }
    }

    public fun getTextFromToast(): String {
        WebDriverWait(driver, Duration.ofMillis(25_000))
            .until(ExpectedConditions.presenceOfElementLocated(xpath("//android.widget.Toast[contains(@text, 'failed to connect to')]")))
        return toastMessage?.text ?: ""
    }

    public fun openSecondPage(): SecondPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.elementToBeClickable(fabAction))
        fabAction?.click()
        return SecondPage(driver)
    }
}