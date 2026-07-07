package alex.valker91.lnd_demo_app.ui.fitnesse

class AppiumTeardown {
    fun stopDriver(): Boolean {
        AppiumContext.driver?.quit()
        AppiumContext.driver = null
        return true
    }
}