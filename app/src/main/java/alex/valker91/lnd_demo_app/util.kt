package alex.valker91.lnd_demo_app

import android.util.Log
import io.sentry.Sentry
import io.sentry.SpanStatus
suspend fun <T> traceSuspend(
    operation: String,
    operationType: String = "usecase",
    block: suspend () -> T
): T {
    val transaction = Sentry.startTransaction(operation, operationType)

    val traceId = transaction.spanContext.traceId.toString()
    Log.d("MyAutomation", "SENTRY_TRACE_ID=$traceId for operation=$operation")

    Sentry.configureScope { scope ->
        scope.transaction = transaction
    }

    return try {
        val result = block()
        transaction.status = SpanStatus.OK
        result
    } catch (e: Throwable) {
        transaction.status = SpanStatus.INTERNAL_ERROR
        transaction.throwable = e
        throw e
    } finally {
        transaction.finish()

        Sentry.configureScope { scope ->
            if (scope.transaction == transaction) {
                scope.transaction = null
            }
        }
    }
}