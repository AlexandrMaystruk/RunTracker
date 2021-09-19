package com.gmail.maystruks08.data

import android.graphics.*
import android.os.Environment
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class QrCodeGenerator @Inject constructor() {

    companion object {
        private val qrPackageName = Environment.getExternalStorageDirectory().path + "/RunTracker"
        private const val IMAGES = "/QR_codes"
    }

    fun generateQRCodes(source: List<String>) {
        var counter = 0
        var fos: FileOutputStream? = null
        source.forEach {
            val rqCodeBitmap = getQRCodeWithOverlay(it, 250)
            val directory = preparePathToDirectory(IMAGES)
            val logFile = File(directory, "${it.replace("/", "_")}.png")
            try {
                fos = FileOutputStream(logFile)
                rqCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                counter++
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        Timber.e("COUNTER -> $counter")
    }

    private fun preparePathToDirectory(key: String): File {
        val directory = File(qrPackageName)
        if (!directory.isDirectory || !directory.exists()) {
            directory.mkdirs()
        }
        val keyDirectory = File(directory, key)
        if (!keyDirectory.isDirectory || !keyDirectory.exists()) {
            keyDirectory.mkdirs()
        }
        return keyDirectory
    }


    fun getQRCodeWithOverlay(qrCodeContent: String, qrCodeSize: Int): Bitmap {
        val hints = hashMapOf<EncodeHintType, Int>().also {
            it[EncodeHintType.MARGIN] = 2
        }
        val bits = QRCodeWriter().encode(
            qrCodeContent,
            BarcodeFormat.QR_CODE,
            qrCodeSize,
            qrCodeSize,
            hints
        )
        val qrCode = Bitmap.createBitmap(qrCodeSize, qrCodeSize, Bitmap.Config.RGB_565).also {
            for (x in 0 until qrCodeSize) {
                for (y in 0 until qrCodeSize) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
        return scaleOverlay(qrCode, qrCodeContent)
    }

    private fun scaleOverlay(qrCode: Bitmap, number: String): Bitmap {
        val marginTop = 35
        val bmOverlay = Bitmap.createBitmap(qrCode.width, qrCode.height + marginTop, qrCode.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(qrCode, Matrix(), null)
        val paint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }

        canvas.drawRect(
            0f,
            (bmOverlay.height - marginTop).toFloat(),
            bmOverlay.width.toFloat(),
            bmOverlay.height.toFloat(),
            paint
        )
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 35f
        paint.color = Color.BLACK
        val length = paint.measureText(number)
        canvas.drawText(
            number,
            bmOverlay.width / 2f - length / 2,
            bmOverlay.height - 15f,
            paint
        )
        return bmOverlay
    }

}