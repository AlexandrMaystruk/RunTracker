package com.gmail.maystruks08.nfcruntracker

import com.gmail.maystruks08.nfcruntracker.ui.views.ChartItem
import com.gmail.maystruks08.nfcruntracker.ui.views.ChartView
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val chart = ChartView.Chart()
            .apply {
                chartItems.add(ChartItem("Text", R.color.colorText, R.color.colorPrimary, 10))
                chartItems.add(ChartItem("Text", R.color.colorText, R.color.colorGreen, 10))
                chartItems.add(ChartItem("Text", R.color.colorText, R.color.colorRed, 10))
            }

        println(chart.chartItems.toTypedArray().contentToString())
        val result = chart.getRecalculatedChartItems()
        println(result.toTypedArray().contentToString())
    }
}
