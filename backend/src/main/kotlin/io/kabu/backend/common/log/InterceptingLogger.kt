package io.kabu.backend.common.log

import mu.KLogger
import mu.Marker

class InterceptingLogger(private val kLogger: KLogger) : KLogger by kLogger {

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    override fun warn(msg: String?) {
        if (InterceptingLogging.warn(msg)) kLogger.warn(msg)
    }

    /**
     * Lazy add a log message if isWarnEnabled is true
     */
    override fun warn(msg: () -> Any?) {
        if (InterceptingLogging.warn(msg()?.toString())) kLogger.warn(msg)
    }

    /**
     * Lazy add a log message with throwable payload if isWarnEnabled is true
     */
    override fun warn(t: Throwable?, msg: () -> Any?) {
        if (InterceptingLogging.warn(msg()?.toString())) kLogger.warn(t, msg)
    }

    /**
     * Lazy add a log message if isWarnEnabled is true
     */
    override fun warn(marker: Marker?, msg: () -> Any?) {
        if (InterceptingLogging.warn(msg()?.toString())) kLogger.warn(marker, msg)
    }

    /**
     * Lazy add a log message with throwable payload if isWarnEnabled is true
     */
    override fun warn(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        if (InterceptingLogging.warn(msg()?.toString())) kLogger.warn(marker, t, msg)
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun warn(msg: String?, t: Throwable?) {
        if (InterceptingLogging.warn(msg)) kLogger.warn(msg, t)
    }

    /**
     * This method is similar to [.warn]
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun warn(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) {
        if (InterceptingLogging.warn(format?.format(arg1, arg2))) kLogger.warn(marker, format, arg1, arg2)
    }

    /**
     * This method is similar to [.warn] method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    override fun warn(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) {
        if (InterceptingLogging.warn(msg)) kLogger.warn(marker, msg, t)
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the WARN level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for WARN. The variants taking
     * [one][.warn] and [two][.warn]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun warn(format: String?, vararg arguments: Any?) {
        if (InterceptingLogging.warn(format?.format(arguments))) kLogger.warn(format, arguments)
    }

    /**
     * This method is similar to [.warn]
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun warn(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) {
        if (InterceptingLogging.warn(format?.format(arguments))) kLogger.warn(marker, format, arguments)
    }

    /**
     * This method is similar to [.warn] method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    override fun warn(marker: org.slf4j.Marker?, format: String?, arg: Any?) {
        if (InterceptingLogging.warn(format?.format(arg))) kLogger.warn(marker, format, arg)
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the WARN level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun warn(format: String?, arg: Any?) {
        if (InterceptingLogging.warn(format?.format(arg))) kLogger.warn(format, arg)
    }

    /**
     * Log a message with the specific Marker at the WARN level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    override fun warn(marker: org.slf4j.Marker?, msg: String?) {
        if (InterceptingLogging.warn(msg)) kLogger.warn(marker, msg)
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the WARN level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        if (InterceptingLogging.warn(format?.format(arg1, arg2))) kLogger.warn(format, arg1, arg2)
    }
    
    // INFO

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    override fun info(msg: String?) {
        if (InterceptingLogging.info(msg)) kLogger.info(msg)
    }

    /**
     * Lazy add a log message if isWarnEnabled is true
     */
    override fun info(msg: () -> Any?) {
        if (InterceptingLogging.info(msg()?.toString())) kLogger.info(msg)
    }

    /**
     * Lazy add a log message with throwable payload if isWarnEnabled is true
     */
    override fun info(t: Throwable?, msg: () -> Any?) {
        if (InterceptingLogging.info(msg()?.toString())) kLogger.info(t, msg)
    }

    /**
     * Lazy add a log message if isWarnEnabled is true
     */
    override fun info(marker: Marker?, msg: () -> Any?) {
        if (InterceptingLogging.info(msg()?.toString())) kLogger.info(marker, msg)
    }

    /**
     * Lazy add a log message with throwable payload if isWarnEnabled is true
     */
    override fun info(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        if (InterceptingLogging.info(msg()?.toString())) kLogger.info(marker, t, msg)
    }

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun info(msg: String?, t: Throwable?) {
        if (InterceptingLogging.info(msg)) kLogger.info(msg, t)
    }

    /**
     * This method is similar to [.info]
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun info(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) {
        if (InterceptingLogging.info(format?.format(arg1, arg2))) kLogger.info(marker, format, arg1, arg2)
    }

    /**
     * This method is similar to [.info] method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    override fun info(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) {
        if (InterceptingLogging.info(msg)) kLogger.info(marker, msg, t)
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the INFO level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for INFO. The variants taking
     * [one][.info] and [two][.info]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun info(format: String?, vararg arguments: Any?) {
        if (InterceptingLogging.info(format?.format(arguments))) kLogger.info(format, arguments)
    }

    /**
     * This method is similar to [.info]
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun info(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) {
        if (InterceptingLogging.info(format?.format(arguments))) kLogger.info(marker, format, arguments)
    }

    /**
     * This method is similar to [.info] method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    override fun info(marker: org.slf4j.Marker?, format: String?, arg: Any?) {
        if (InterceptingLogging.info(format?.format(arg))) kLogger.info(marker, format, arg)
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the INFO level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun info(format: String?, arg: Any?) {
        if (InterceptingLogging.info(format?.format(arg))) kLogger.info(format, arg)
    }

    /**
     * Log a message with the specific Marker at the INFO level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    override fun info(marker: org.slf4j.Marker?, msg: String?) {
        if (InterceptingLogging.info(msg)) kLogger.info(marker, msg)
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the INFO level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        if (InterceptingLogging.info(format?.format(arg1, arg2))) kLogger.info(format, arg1, arg2)
    }
    
    // ERROR

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    override fun error(msg: String?) {
        if (InterceptingLogging.error(msg)) kLogger.error(msg)
    }

    /**
     * Lazy add a log message if isWarnEnabled is true
     */
    override fun error(msg: () -> Any?) {
        if (InterceptingLogging.error(msg()?.toString())) kLogger.error(msg)
    }

    /**
     * Lazy add a log message with throwable payload if isWarnEnabled is true
     */
    override fun error(t: Throwable?, msg: () -> Any?) {
        if (InterceptingLogging.error(msg()?.toString())) kLogger.error(t, msg)
    }

    /**
     * Lazy add a log message if isWarnEnabled is true
     */
    override fun error(marker: Marker?, msg: () -> Any?) {
        if (InterceptingLogging.error(msg()?.toString())) kLogger.error(marker, msg)
    }

    /**
     * Lazy add a log message with throwable payload if isWarnEnabled is true
     */
    override fun error(marker: Marker?, t: Throwable?, msg: () -> Any?) {
        if (InterceptingLogging.error(msg()?.toString())) kLogger.error(marker, t, msg)
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    override fun error(msg: String?, t: Throwable?) {
        if (InterceptingLogging.error(msg)) kLogger.error(msg, t)
    }

    /**
     * This method is similar to [.error]
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun error(marker: org.slf4j.Marker?, format: String?, arg1: Any?, arg2: Any?) {
        if (InterceptingLogging.error(format?.format(arg1, arg2))) kLogger.error(marker, format, arg1, arg2)
    }

    /**
     * This method is similar to [.error] method
     * except that the marker data is also taken into consideration.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    override fun error(marker: org.slf4j.Marker?, msg: String?, t: Throwable?) {
        if (InterceptingLogging.error(msg)) kLogger.error(marker, msg, t)
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the ERROR level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an `Object[]` before invoking the method,
     * even if this logger is disabled for ERROR. The variants taking
     * [one][.error] and [two][.error]
     * arguments exist solely in order to avoid this hidden cost.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun error(format: String?, vararg arguments: Any?) {
        if (InterceptingLogging.error(format?.format(arguments))) kLogger.error(format, arguments)
    }

    /**
     * This method is similar to [.error]
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    override fun error(marker: org.slf4j.Marker?, format: String?, vararg arguments: Any?) {
        if (InterceptingLogging.error(format?.format(arguments))) kLogger.error(marker, format, arguments)
    }

    /**
     * This method is similar to [.error] method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    override fun error(marker: org.slf4j.Marker?, format: String?, arg: Any?) {
        if (InterceptingLogging.error(format?.format(arg))) kLogger.error(marker, format, arg)
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    override fun error(format: String?, arg: Any?) {
        if (InterceptingLogging.error(format?.format(arg))) kLogger.error(format, arg)
    }

    /**
     * Log a message with the specific Marker at the ERROR level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    override fun error(marker: org.slf4j.Marker?, msg: String?) {
        if (InterceptingLogging.error(msg)) kLogger.error(marker, msg)
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     *
     *
     *
     * This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        if (InterceptingLogging.error(format?.format(arg1, arg2))) kLogger.error(format, arg1, arg2)
    }
}
