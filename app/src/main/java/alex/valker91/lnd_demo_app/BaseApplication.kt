package alex.valker91.lnd_demo_app

import android.app.Application
import android.os.SystemClock
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {

    companion object {
        var coldStartTrace: Trace? = null
        var appStartTimeMs: Long = 0L
        var isColdStartMeasured = false
    }

    override fun onCreate() {
        appStartTimeMs = SystemClock.elapsedRealtime()
        coldStartTrace = FirebasePerformance.getInstance().newTrace("cold_start_ttid")
        coldStartTrace?.start()
        super.onCreate()
    }
}