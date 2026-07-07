package alex.valker91.lnd_demo_app.ui.page

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.support.PageFactory

abstract class AbstractPage (driver: AndroidDriver) {

    init {
        PageFactory.initElements(driver, this)
    }
}