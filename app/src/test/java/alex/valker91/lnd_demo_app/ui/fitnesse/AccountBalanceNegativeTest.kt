package alex.valker91.lnd_demo_app.ui.fitnesse

import alex.valker91.lnd_demo_app.ui.page.FirstPage

// http://localhost:8089/AccountBalanceNegativeTest?edit
class AccountBalanceNegativeTest {

    private var accountBalanceToTest: String = ""
    fun setAccountNumber(numberFromTable: String) {
        this.accountBalanceToTest = numberFromTable
    }

    fun isNetworkErrorShown(): Boolean {
        val driver = AppiumContext.driver
            ?: throw IllegalStateException("Something wrong")

        val firstPage = FirstPage(driver)
            .sendTextToEtAccountNumber(accountBalanceToTest)
            .clickBtnGetBalances()

        val toastText = firstPage.getTextFromToast()
        return toastText.contains("failed to connect to /10.68.84.61 (port 8080) from /10.0.2.16")
    }

}