package alex.valker91.lnd_demo_app

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        measureColdStart()
    }

    private fun measureColdStart() {
        if (BaseApplication.isColdStartMeasured) return

        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    content.post {
                        if (!BaseApplication.isColdStartMeasured) {
                            val duration = SystemClock.elapsedRealtime() - BaseApplication.appStartTimeMs
                            BaseApplication.coldStartTrace?.putMetric("duration_ms", duration)
                            BaseApplication.coldStartTrace?.stop()
                            BaseApplication.coldStartTrace = null
                            BaseApplication.isColdStartMeasured = true
                            Log.d("MyPerfTest", "Cold start TTID: $duration ms")
                        }
                    }
                    return true
                }
            }
        )
    }
}