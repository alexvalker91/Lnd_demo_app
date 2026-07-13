package alex.valker91.lnd_demo_app.ui.fitnesse

import alex.valker91.lnd_demo_app.ui.page.FirstPage

// http://localhost:8089/NetworkSpeedPositiveTest?edit
class NetworkSpeedPositiveTest {

    private var speed: String = ""

    fun setSpeed(speed: String) {
        this.speed = speed
    }

    fun isSuccessToastShown(): Boolean {
        val setup = AppiumSetup()
        setup.startDriver()

        val driver = AppiumContext.driver ?: throw IllegalStateException("Драйвер не запустился!")

        try {
            changeNetworkSpeed(speed)

            val startTime = System.currentTimeMillis()

            val toastText = FirstPage(driver)
                .openSecondPage()
                .clickButton()
                .getTextFromToast()

            val durationMs = System.currentTimeMillis() - startTime
            System.err.println("Log: Тост получен за $durationMs мс на скорости $speed")

            return toastText.contains("Success")

        } finally {
            val teardown = AppiumTeardown()
            teardown.stopDriver()
        }
    }

    private fun changeNetworkSpeed(networkSpeed: String) {
        System.err.println("Log: Меняем скорость сети на: $networkSpeed ")
        try {
            val process = ProcessBuilder("cmd.exe", "/c", "adb emu network speed $networkSpeed").start()
            process.waitFor()
            Thread.sleep(1000)
        } catch (e: Exception) {
            System.err.println("Ошибка при смене сети: ${e.message}")
        }
    }
}