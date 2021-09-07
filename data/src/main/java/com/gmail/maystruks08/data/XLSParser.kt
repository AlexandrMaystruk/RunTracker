package com.gmail.maystruks08.data

import android.content.Context
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import timber.log.Timber
import java.io.InputStream
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class XLSParser @Inject constructor(private val context: Context) {

    fun readExcelFileFromAssets(
        raceId: String,
        distanceId: String,
        fileName: String
    ): List<Runner> {
        val result = mutableListOf<Runner>()
        try {
            val myInput: InputStream
            val assetManager = context.assets
            myInput = assetManager.open(fileName)
            val myFileSystem = POIFSFileSystem(myInput)
            val myWorkBook = HSSFWorkbook(myFileSystem)
            val mySheet = myWorkBook.getSheetAt(0)
            val rowIterator: Iterator<Row> = mySheet.rowIterator()
            var rowNumber = 0
            while (rowIterator.hasNext()) {
                Timber.e("Row number $rowNumber")
                val myRow = rowIterator.next() as HSSFRow
                val cellIterator: Iterator<Cell> = myRow.cellIterator()
                var columnNumber = 0
                var fullName = ""
                var shortName = ""
                val phone = ""
                val runnerSex = RunnerSex.MALE
                val city = ""
                var number: String? = null
                val dateOfBirthday: Date? = null
                val raceIds: MutableList<String> = mutableListOf()
                val distanceIds: MutableList<String> = mutableListOf()
                val checkpointMap: MutableMap<String, MutableList<Checkpoint>> = mutableMapOf()
                val offTrackDistances: MutableList<String> = mutableListOf()
                val teamNames: MutableMap<String, String?> = mutableMapOf()
                val totalResults: MutableMap<String, Date?> = mutableMapOf()

                while (cellIterator.hasNext()) {
                    val myCell = cellIterator.next() as HSSFCell
                    when (columnNumber) {
                        0 -> {
                            number = myCell.toString().replace(".0", "")
                        }
                        1 -> fullName = myCell.toString()
                        2 -> shortName = myCell.toString()
                        3 -> teamNames[distanceId] =
                            if (myCell.toString().isEmpty()) null else myCell.toString()
                    }
                    columnNumber++
                }
                if (!number.isNullOrEmpty()) {
                    val newRunner = Runner(
                        cardId = "",
                        fullName = fullName,
                        shortName = shortName,
                        phone = phone,
                        number = number,
                        sex = runnerSex,
                        city = city,
                        dateOfBirthday = dateOfBirthday,
                        actualDistanceId = distanceId,
                        actualRaceId = raceId,
                        distanceIds = distanceIds,
                        offTrackDistances = offTrackDistances,
                        raceIds = raceIds,
                        teamNames = teamNames,
                        totalResults = totalResults,
                        checkpoints = checkpointMap
                    )
                    result.add(newRunner)
                    Timber.e(newRunner.toString())
                }
                rowNumber++
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return result
    }


    enum class AppCustomDateFormat {
        SERVER_DATETIME_FORMAT, SERVER_DATE_FORMAT;

        companion object {
            fun getFormatString(name: AppCustomDateFormat): String {
                return when (name) {
                    SERVER_DATETIME_FORMAT -> "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
                    SERVER_DATE_FORMAT -> "dd.MM.yyyy"
                }
            }
        }
    }

    fun BigDecimal.hasFraction(): Boolean {
        val fractionalPart = this.remainder(BigDecimal.ONE)
        return fractionalPart > BigDecimal.ZERO
    }

    fun BigDecimal.roundToInteger(): BigDecimal {
        val fractionalPart = this.remainder(BigDecimal.ONE)
        return if (fractionalPart > BigDecimal.ZERO) {
            this - fractionalPart + BigDecimal.ONE
        } else this
    }

    private fun parseDateString(date: String, format: AppCustomDateFormat): Date = try {
        val dateFormat = AppCustomDateFormat.getFormatString(format)
        val df = SimpleDateFormat(dateFormat, Locale.getDefault())
        df.parse(date)
    } catch (e: ParseException) {
        Timber.d("Error parse date in - $date, format - ${format.name}")
        if (format == AppCustomDateFormat.SERVER_DATETIME_FORMAT) {
            parseDateString(date, AppCustomDateFormat.SERVER_DATETIME_FORMAT)
        } else {
            Date()
        }
    }

}
