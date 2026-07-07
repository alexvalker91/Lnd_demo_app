package alex.valker91.lnd_demo_app

import android.app.Application
import android.os.SystemClock
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import io.sentry.android.fragment.FragmentLifecycleIntegration

@HiltAndroidApp
class BaseApplication : Application() {

//    companion object {
//        var coldStartTrace: Trace? = null
//        var appStartTimeMs: Long = 0L
//        var isColdStartMeasured = false
//    }

//    override fun onCreate() {
//        appStartTimeMs = SystemClock.elapsedRealtime()
//        coldStartTrace = FirebasePerformance.getInstance().newTrace("cold_start_ttid")
//        coldStartTrace?.start()
//        super.onCreate()
//    }

    override fun onCreate() {
        super.onCreate()

        SentryAndroid.init(this) { options ->
            // DSN можно оставить в манифесте ИЛИ продублировать тут.
            // Если оставляешь в манифесте — эту строку можно не писать.
            options.dsn = "https://62602a000b780eb5110d8678e725afb5@o4511579998781440.ingest.de.sentry.io/4511580007432272"

            // === PERFORMANCE ===
            options.tracesSampleRate = 1.0  // 100% для теста (в проде ставь 0.1)

            // Cold/Warm start — собирается автоматически при включённом трейсинге
            options.isEnableAutoActivityLifecycleTracing = true   // переходы между Activity + TTID
            options.isEnableActivityLifecycleBreadcrumbs = true
            options.isEnableUserInteractionTracing = true          // клики/свайпы как транзакции
            options.isEnableFramesTracking = true                  // slow/frozen frames

            // === FRAGMENTS (переходы между фрагментами) ===
            options.addIntegration(
                FragmentLifecycleIntegration(
                    this,
                    enableFragmentLifecycleBreadcrumbs = true,
                    enableAutoFragmentLifecycleTracing = true  // <- транзакции на каждый фрагмент
                )
            )

            // Логи Sentry в Logcat (удобно для отладки настройки)
            options.isDebug = true
        }
    }
}