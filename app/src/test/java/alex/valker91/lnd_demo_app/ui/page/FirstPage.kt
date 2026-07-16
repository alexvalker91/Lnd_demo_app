package alex.valker91.lnd_demo_app.ui.page

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By.xpath
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TimeoutException
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

    @FindBy(id = "amount")
    private val amount: WebElement? = null

    @FindBy(id = "clientIdFrom")
    private val clientIdFrom: WebElement? = null

    @FindBy(id = "accountNumberFrom")
    private val accountNumberFrom: WebElement? = null

    @FindBy(id = "accountNumberTo")
    private val accountNumberTo: WebElement? = null

    @FindBy(id = "comment")
    private val comment: WebElement? = null

    @FindBy(id = "btnCreate")
    private val btnCreate: WebElement? = null

    @FindBy(id = "tvAccountBalance1")
    private val tvAccountBalance: WebElement? = null

    @FindBy(id = "OriginatorId")
    private val originatorId: WebElement? = null

    @FindBy(xpath = "//android.widget.Toast")
    private val toastMessage: WebElement? = null

    fun sendTextToEtAccountNumber(text: String): FirstPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.visibilityOf(etAccountNumber))
        etAccountNumber?.clear()
        etAccountNumber?.sendKeys(text)
        return this
    }

    fun clickBtnGetBalances(): FirstPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.elementToBeClickable(btnGetBalances))
        btnGetBalances?.click()
        System.err.println("Log: 123 getEmulatorNetworkStatus = ${getEmulatorNetworkStatus()}")
        println("Log: 456 getEmulatorNetworkStatus = ${getEmulatorNetworkStatus()}")
        return this
    }

    fun clickGetBalancesForAccount(accountNumberValue: String): FirstPage {
        return sendTextToEtAccountNumber(accountNumberValue).clickBtnGetBalances()
    }

    fun getAccountBalanceValue(): String {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.visibilityOf(tvAccountBalance))
        return tvAccountBalance?.text ?: ""
    }

    fun fillMoneyTransferForm(
        amountValue: String,
        clientIdFromValue: String,
        accountNumberFromValue: String,
        accountNumberToValue: String,
        commentValue: String
    ): FirstPage {
        typeIntoField(amount, amountValue)
        typeIntoField(clientIdFrom, clientIdFromValue)
        typeIntoField(accountNumberFrom, accountNumberFromValue)
        typeIntoField(accountNumberTo, accountNumberToValue)
        typeIntoField(comment, commentValue)
        return this
    }

    fun clickBtnCreate(): FirstPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.elementToBeClickable(btnCreate))
        btnCreate?.click()
        return this
    }

    fun createMoneyTransfer(
        amountValue: String,
        clientIdFromValue: String,
        accountNumberFromValue: String,
        accountNumberToValue: String,
        commentValue: String
    ): FirstPage {
        return fillMoneyTransferForm(
            amountValue,
            clientIdFromValue,
            accountNumberFromValue,
            accountNumberToValue,
            commentValue
        ).clickBtnCreate()
    }

    fun getOriginatorIdValue(): String {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.visibilityOf(originatorId))
        return originatorId?.text ?: ""
    }

    fun getTextFromToast(): String {
        WebDriverWait(driver, Duration.ofMillis(25_000))
            .until(ExpectedConditions.presenceOfElementLocated(xpath("//android.widget.Toast")))
        return toastMessage?.text ?: ""
    }

    fun hasToastContaining(text: String, timeoutMs: Long = 5_000): Boolean {
        return try {
            WebDriverWait(driver, Duration.ofMillis(timeoutMs))
                .until(ExpectedConditions.presenceOfElementLocated(xpath("//android.widget.Toast[contains(@text, '$text')]")))
            true
        } catch (e: TimeoutException) {
            false
        } catch (e: NoSuchElementException) {
            false
        }
    }

    fun openSecondPage(): SecondPage {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.elementToBeClickable(fabAction))
        fabAction?.click()
        return SecondPage(driver)
    }

    private fun typeIntoField(field: WebElement?, value: String) {
        WebDriverWait(driver, Duration.ofMillis(5_000))
            .until(ExpectedConditions.visibilityOf(field)))
        field?.clear()
        field?.sendKeys(value)
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
                else -> "Unknown speed:\n$output"
            }
        } catch (e: Exception) {
            "Error while getting network status"
        }
    }
}