package com.gmail.maystruks08.nfcruntracker.utils

import android.content.Context
import android.os.Environment.DIRECTORY_DOCUMENTS
import android.util.Log
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.*
import java.util.logging.Formatter

class TimberFileTree(context: Context) : Timber.DebugTree() {

    private val logger = Logger.getLogger("FileTree")

    init {
        val logsDir = context.getExternalFilesDir(DIRECTORY_DOCUMENTS)
        val pattern = "$logsDir/%u_%g.log"
        val filesLimit = 1024 * 1024 * 2 // binary 2MB
        val filesCount = 5

        val fileHandler = FileHandler(pattern, filesLimit, filesCount, true)

        fileHandler.formatter = LogFormatter()
        logger.addHandler(fileHandler)
        logger.useParentHandlers = false
        logger.level = Level.FINE
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val level = when (priority) {
            Log.DEBUG -> Level.FINE
            Log.INFO -> Level.INFO
            Log.WARN -> Level.WARNING
            Log.ERROR -> Level.SEVERE
            else -> return
        }

        val logMessage = if (tag != null) "$tag $message" else message
        logger.log(level, logMessage, t)
    }

    private class LogFormatter : Formatter() {

        private val date = Date()
        private val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS Z", Locale.getDefault())

        override fun format(record: LogRecord): String {
            val levelString = when (record.level) {
                Level.SEVERE -> "E"
                Level.WARNING -> "W"
                Level.INFO -> "I"
                Level.FINE -> "D"
                Level.FINEST -> "V"
                else -> "Unexpected"
            }

            date.time = record.millis

            val message = record.message
            val throwable: String? = if (record.thrown != null) getStackTraceString(record.thrown) else null

            val formattedDate = dateFormat.format(date)

            return if (throwable != null) {
                "$formattedDate $levelString/ $message\n$throwable\n"
            } else {
                "$formattedDate $levelString/ $message\n"
            }
        }

        private fun getStackTraceString(t: Throwable): String {
            val sw: Writer = StringWriter(256)
            val pw = PrintWriter(sw, false)
            pw.println()
            t.printStackTrace(pw)
            pw.close()
            return sw.toString()
        }
    }

}