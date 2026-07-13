package alex.valker91.lnd_demo_app.ui.fitnesse

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL
import java.nio.file.Paths

class AppiumSetup {

    fun startDriver(): Boolean {
        val runId = "run_" + java.util.UUID.randomUUID().toString().take(8)
        println("Log: Генерируем новый RunId для этого запуска: $runId")

        val capabilities = DesiredCapabilities()
        capabilities.setCapability("appium:automationName", "UIAutomator2")
        capabilities.setCapability("appium:platformVersion", "16")
        capabilities.setCapability("appium:deviceName", "Medium Phone API 36.0")
        capabilities.setCapability("platformName", "Android")
//        val appPath = "${Paths.get("").toAbsolutePath()}/build/outputs/apk/debug/app-debug.apk"
        val appPath = "C:/Users/AliaksandrKreyer/Desktop/EPM-PAY/Lnd_demo_app/app/build/outputs/apk/debug/app-debug.apk"
        capabilities.setCapability("appium:app", appPath)
        capabilities.setCapability("appium:appWaitForLaunch", "false")

        capabilities.setCapability("appium:optionalIntentArguments", "-e test_run_id $runId")

        AppiumContext.driver = AndroidDriver(URL("http://127.0.0.1:4724"), capabilities)

        return AppiumContext.driver != null
    }
}