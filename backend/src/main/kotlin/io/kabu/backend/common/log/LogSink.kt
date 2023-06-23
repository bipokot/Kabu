package io.kabu.backend.common.log

interface LogSink {

    fun error(message: String)

    fun warn(message: String)

    fun info(message: String)
}
