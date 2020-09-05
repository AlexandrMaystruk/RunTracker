package com.gmail.maystruks08.data

import android.content.Context
import android.content.res.AssetManager
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import timber.log.Timber
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class XLSParcer @Inject constructor(private val context: Context) {

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

    private fun parseDateString(date: String, format: AppCustomDateFormat): Date = try {
        val dateFormat = AppCustomDateFormat.getFormatString(format)
        val df = SimpleDateFormat(dateFormat, Locale.getDefault())
        df.parse(date)
    } catch (e: ParseException) {
        Timber.d("Error parse date in - $date, format - ${format.name}")
        if(format == AppCustomDateFormat.SERVER_DATETIME_FORMAT) {
            parseDateString(date, AppCustomDateFormat.SERVER_DATETIME_FORMAT)
        } else {
            Date()
        }
    }

    fun String?.toDate(def: Date = Date()): Date = if(this != null) parseDateString(this, AppCustomDateFormat.SERVER_DATE_FORMAT) else def

    fun readExcelFileFromAssets(checkpoints: List<Checkpoint>, fileName: String, runnerType: RunnerType): List<Runner> {
        val result = mutableListOf<Runner>()
        try {
            val myInput: InputStream
            // initialize asset manager
            val assetManager: AssetManager = context.assets
            //  open excel sheet
            myInput = assetManager.open(fileName)
            // Create a POI File System object
            val myFileSystem = POIFSFileSystem(myInput)

            // Create a workbook using the File System
            val myWorkBook = HSSFWorkbook(myFileSystem)

            // Get the first sheet from workbook
            val mySheet: HSSFSheet = myWorkBook.getSheetAt(0)

            // We now need something to iterate through the cells.
            val rowIterator: Iterator<Row> = mySheet.rowIterator()
            var rowNumber = 0
            while (rowIterator.hasNext()) {
                Timber.e("Row number $rowNumber")
                val myRow = rowIterator.next() as HSSFRow
                if (rowNumber != 0) {
                    val cellIterator: Iterator<Cell> = myRow.cellIterator()
                    var columnNumber = 0

                    var fullName = ""
                    var shortName = ""
                    var phone = ""
                    var runnerSex = RunnerSex.MALE
                    var city = ""
                    var number = Random().nextInt(10000)
                    var dateOfBirthday = Date()

                    while (cellIterator.hasNext()) {
                        val myCell = cellIterator.next() as HSSFCell
                        when (columnNumber) {
                            1 -> fullName = myCell.toString()
                            2 -> shortName = myCell.toString()
                            3 -> runnerSex = if(myCell.toString() == "М") RunnerSex.MALE else RunnerSex.FEMALE
                            4 -> city = myCell.toString()
                            5 -> phone = myCell.toString()
                            6 -> dateOfBirthday = myCell.toString().toDate()
                            8 -> number = myCell.toString().toIntOrNull()?:number
                        }
                        columnNumber++
                    }
                    val newRunner = Runner(
                        id = UUID.randomUUID().toString(),
                        fullName = fullName,
                        shortName = shortName,
                        phone =phone ,
                        number = number,
                        sex = runnerSex,
                        city = city,
                        dateOfBirthday = dateOfBirthday,
                        type = runnerType,
                        totalResult = null,
                        checkpoints = checkpoints.toMutableList(),
                        isOffTrack = false
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