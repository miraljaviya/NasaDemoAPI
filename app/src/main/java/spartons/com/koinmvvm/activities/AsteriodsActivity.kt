package spartons.com.koinmvvm.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_asteriod.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import spartons.com.koinmvvm.R
import spartons.com.koinmvvm.activities.main.viewModel.MovieViewModel
import java.util.*

class AsteriodsActivity : AppCompatActivity() {

    private val movieViewModel: MovieViewModel by viewModel()
    private val picasso: Picasso by inject()
    private val colors = intArrayOf(
        Color.rgb(255, 255, 255),
        Color.rgb(0, 0, 255),
        Color.rgb(250, 119, 91),
        Color.rgb(211, 211, 211),
        Color.rgb(255, 140, 157)
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asteriod)

        btn_submit.setOnClickListener{
            movieViewModel.retrieveNearEarthObjects(et_start.text.toString(), et_end.text.toString())
        }

        movieViewModel.astState.observe(this, Observer {
            val dataState = it ?: return@Observer
            progress_bar.visibility = if (dataState.showProgress) View.VISIBLE else View.GONE
            if (dataState.asteriods != null && !dataState.asteriods.consumed)
                dataState.asteriods.consume()?.let { asteriods ->
                    val minDistance= asteriods.entries.map { it.value }.flatten().minBy { p-> p.closeApproachData!![0].missDistance?.kilometers!! }

                    val speed= asteriods.entries.map { it.value }.flatten().maxBy { p-> p.closeApproachData!![0].relativeVelocity?.kilometersPerHour!! }

                    txt_fast.text = "Fastest Asteroid: \nName: ${speed?.name} \nVelocity(km/h): ${speed?.closeApproachData!![0].relativeVelocity?.kilometersPerHour}"
                    txt_near.text = "Closest Asteroid: \nName: ${minDistance?.name} \nDistance: ${minDistance?.closeApproachData!![0].missDistance?.kilometers} km"

                    val avr1=(speed.estimatedDiameter!!.kilometers?.minimumDiameter!!+speed.estimatedDiameter.kilometers?.maximumDiameter!!)/2
                    val avr2=(minDistance.estimatedDiameter!!.kilometers?.minimumDiameter!!+minDistance.estimatedDiameter.kilometers?.maximumDiameter!!)/2
                    txt_average.text = "Average sizes: \n${speed.name}: $avr1 km \n${minDistance.name}:$avr2 km"
                    Log.e("zxxx", "min $minDistance $speed")

                    setFullDay(asteriods)
                }
            if (dataState.error != null && !dataState.error.consumed)
                dataState.error.consume()?.let { errorResource ->
                    Toast.makeText(this, resources.getString(errorResource), Toast.LENGTH_SHORT)
                        .show()
                    // handle error state
                }


        })
    }


    private fun setFullDay(map: Map<String, List<NearEarthObject>>) {

        val alist: Array<String>  = map.keys.toTypedArray()

        Log.e("xxx","max values ${map.values.maxBy { it.size }?.size!!.toFloat()} ${alist.toList().toString()}")
        weekly_chart.clear()

        weekly_chart.setDrawBarShadow(false)
        weekly_chart.setDrawValueAboveBar(true)
        weekly_chart.description.isEnabled = false
        weekly_chart.setPinchZoom(false)
        weekly_chart.setDrawGridBackground(false)
        val xAxis = weekly_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45F

        val xAxisFormatter= IndexAxisValueFormatter(alist)
        xAxis.valueFormatter = xAxisFormatter

        xAxis.spaceMin = 1f
        xAxis.axisMaximum = alist.size.toFloat()+1
        xAxis.spaceMax = 1f


        val leftAxis = weekly_chart!!.axisLeft
        leftAxis.setDrawGridLines(false)

        leftAxis.setDrawLimitLinesBehindData(false)

        leftAxis.axisMinimum = 0f
        weekly_chart.setPinchZoom(false)
        weekly_chart!!.axisRight.isEnabled = false
        weekly_chart!!.axisRight.axisMinimum = 0f
        weekly_chart!!.axisRight.axisMaximum = map.values.maxBy { it.size }?.size!!.toFloat()+5
        weekly_chart!!.axisRight.setLabelCount(4, false)
        weekly_chart!!.axisRight.granularity = 1f
        weekly_chart!!.axisRight.setDrawGridLines(false)
        weekly_chart!!.axisRight.setDrawLimitLinesBehindData(true)
        weekly_chart!!.axisLeft.isEnabled = true
        weekly_chart!!.axisLeft.axisMinimum = 0f
        weekly_chart!!.axisLeft.axisMaximum = map.values.maxBy { it.size }?.size!!.toFloat()+5
        weekly_chart!!.axisLeft.setLabelCount(4, false)
        weekly_chart!!.axisLeft.granularity = 1f
        weekly_chart!!.axisLeft.setDrawGridLines(false)
        weekly_chart!!.axisLeft.setDrawLimitLinesBehindData(true)
        weekly_chart!!.axisLeft.removeAllLimitLines()

        weekly_chart!!.axisRight.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value == 0f) {
                    return "0"
                }
                return value.toInt().toString()
            }
        }

        weekly_chart!!.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                if (value == 0f) {
                    return "0"
                }
                return value.toInt().toString()
            }
        }

        weekly_chart.legend.isEnabled = false
        weekly_chart.setTouchEnabled(false)

        try {


            val values = ArrayList<BarEntry>()
            map.keys.forEachIndexed { index, entry ->
                Log.e("xxx", "added ${index.toFloat()} ${map[entry]?.size!!.toFloat()}")
                values.add(
                    BarEntry(
                        index.toFloat(),
                        map[entry]?.size!!.toFloat()
                    )
                )
            }

            val set1: BarDataSet
            if (weekly_chart!!.data != null &&
                weekly_chart!!.data.dataSetCount > 0
            ) {
                set1 = weekly_chart!!.data.getDataSetByIndex(0) as BarDataSet
                set1.values = values
                set1.color = ContextCompat.getColor(this, R.color.colorAccent)
                weekly_chart!!.data.notifyDataChanged()
                weekly_chart!!.notifyDataSetChanged()
            } else {
                set1 = BarDataSet(values, "Hours Used")
                set1.colors = colors.toList()
                set1.setDrawIcons(false)
                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(set1)
                set1.color = ContextCompat.getColor(this, R.color.colorAccent)
                val data = BarData(dataSets)
              //  data.getGroupWidth(0f,10f)
                data.setValueTextSize(5f)
                data.barWidth = 0.8f
                data.setDrawValues(false)
                weekly_chart!!.data = data
                weekly_chart!!.invalidate()

            }
        } catch (e: Exception) {
        }

    }
}
