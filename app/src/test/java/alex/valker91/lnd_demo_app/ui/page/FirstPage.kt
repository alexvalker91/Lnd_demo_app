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
        return this
    }

    public fun getTextFromToast(): String {
        WebDriverWait(driver, Duration.ofMillis(25_000))
            .until(ExpectedConditions.presenceOfElementLocated(xpath("//android.widget.Toast[contains(@text, 'failed to connect to')]")))
        return toastMessage?.text ?: ""
    }
}