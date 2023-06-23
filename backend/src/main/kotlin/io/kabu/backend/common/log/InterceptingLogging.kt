package io.kabu.backend.common.log

import mu.KLogger
import mu.KotlinLogging

object InterceptingLogging {

    var logSink: LogSink? = null

    fun logger(func: () -> Unit): KLogger = InterceptingLogger(KotlinLogging.logger(func))

    // Returns true, if message should be logged
    fun info(msg: String?): Boolean {
        return log(msg, LogSink::info)
    }

    // Returns true, if message should be logged
    fun warn(msg: String?): Boolean {
        return log(msg, LogSink::warn)
    }

    // Returns true, if message should be logged
    fun error(msg: String?): Boolean {
        return log(msg, LogSink::error)
    }

    private fun log(
        msg: String?,
        sinkMethod: LogSink.(String) -> Unit,
    ): Boolean {
        val sink = logSink
        return if (sink != null) {
            msg?.let { sink.sinkMethod(msg) }
            false
        } else {
            true
        }
    }
}
