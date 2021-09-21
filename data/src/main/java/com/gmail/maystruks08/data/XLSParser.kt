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
        fileName: String,
        isTeam: Boolean = false
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
                var teamName: String? = null

                while (cellIterator.hasNext()) {
                    val myCell = cellIterator.next() as HSSFCell
                    if(isTeam){
                        when (columnNumber) {
                            0 -> number = myCell.toString().replace(".0", "").trim()
                            1 -> teamName = if (myCell.toString().isEmpty()) null else myCell.toString().trim()
                            2 -> fullName = myCell.toString().trim()
                            3 -> shortName = myCell.toString().trim()
                        }
                    }else {
                        when (columnNumber) {
                            0 -> number = myCell.toString().replace(".0", "").trim()
                            1 -> fullName = myCell.toString().trim()
                            2 -> shortName = myCell.toString().trim()
                        }
                    }
                    columnNumber++
                }
                if (!number.isNullOrEmpty()) {
                    val newRunner = Runner(
                        cardId = "",
                        fullName = fullName.trim(),
                        shortName = shortName.trim(),
                        phone = phone.trim(),
                        number = number.trim(),
                        sex = runnerSex,
                        city = city.trim(),
                        dateOfBirthday = dateOfBirthday,
                        actualDistanceId = distanceId,
                        actualRaceId = raceId,
                        currentCheckpoints = mutableListOf(),
                        offTrackDistance = null,
                        currentTeamName = teamName,
                        currentResult = null
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
}
