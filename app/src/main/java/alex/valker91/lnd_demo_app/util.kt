package alex.valker91.lnd_demo_app

import io.sentry.Sentry
import io.sentry.SpanStatus
suspend fun <T> traceSuspend(
    operation: String,
    operationType: String = "usecase",
    block: suspend () -> T
): T {
    val transaction = Sentry.startTransaction(operation, operationType)
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
    }
}