package com.gmail.maystruks08.nfcruntracker.ui.register

import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.nfcruntracker.R
import kotlinx.android.synthetic.main.item_register_new_runner.view.*
import java.util.*


data class InputDataView(
    var fullName: String? = null,
    var shortName: String? = null,
    var phone: String? = null,
    var runnerSex: RunnerSex? = null,
    var dateOfBirthday: Date? = Date(),
    var city: String? = null,
    var runnerNumber: Int? = null,
    var runnerType: RunnerType? = null,
    var runnerCardId: String? = null
) {
    fun isEmpty(): Boolean = fullName.isNullOrEmpty() ||
            phone.isNullOrEmpty() ||
            runnerSex == null ||
            dateOfBirthday == null ||
            city.isNullOrEmpty() ||
            runnerType == null ||
            runnerNumber == null ||
            runnerCardId.isNullOrEmpty()
}

class RegisterRunnerAdapter : RecyclerView.Adapter<RegisterRunnerAdapter.ViewHolder>() {

    var runnerRegisterData = mutableListOf(InputDataView())

    fun addInputData(inputDataView: InputDataView) {
        runnerRegisterData.add(inputDataView)
        notifyItemInserted(runnerRegisterData.lastIndex)
    }

    fun setRunnerCardId(cardId: String) {
        runnerRegisterData[selectedPosition].runnerCardId = cardId
        notifyItemChanged(selectedPosition)
    }

    fun removeInputField(position: Int) {
        runnerRegisterData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun canSwipe(): Boolean = itemCount > 1

    private var selectedView: View? = null
    private var selectedPosition: Int = 0
    private val calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_register_new_runner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(runnerRegisterData[position])
    }

    override fun getItemCount(): Int = runnerRegisterData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val fullName = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnerRegisterData[adapterPosition].fullName = s?.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        private val runnerNumber = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnerRegisterData[adapterPosition].runnerNumber = s?.toString()?.toIntOrNull()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        private val city = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnerRegisterData[adapterPosition].city = s?.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

      private val phone = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnerRegisterData[adapterPosition].phone = s?.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        fun bindHolder(item: InputDataView) {
            itemView.apply {
                etRunnerFullName.setText(item.fullName.orEmpty())
                etRunnerNumber.setText(item.runnerNumber?.toString().orEmpty())
                etRunnerPhoneNumber.setText(item.phone.orEmpty())
                tvDateOfBirthday.text = item.dateOfBirthday?.toDateFormat()
                tvScanCard.text = context.resources.getString(R.string.card, item.runnerCardId.orEmpty())
                etRunnerCity.setText(item.city.orEmpty())
                when (item.runnerSex) {
                    RunnerSex.MALE -> radioGroupSex.check(R.id.rbMale)
                    RunnerSex.FEMALE -> radioGroupSex.check(R.id.rbFemale)
                    else -> radioGroupSex.clearCheck()
                }
                when (item.runnerType) {
                    RunnerType.NORMAL -> radioGroupRunnerType.check(R.id.rbRunner)
                    RunnerType.IRON -> radioGroupRunnerType.check(R.id.rbIronRunner)
                    else -> radioGroupRunnerType.clearCheck()
                }

                val focusChange = View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) onItemViewClick(itemView, adapterPosition)
                }

                etRunnerFullName.onFocusChangeListener = focusChange
                etRunnerNumber.onFocusChangeListener = focusChange
                tvDateOfBirthday.onFocusChangeListener = focusChange
                etRunnerCity.onFocusChangeListener = focusChange

                tvDateOfBirthday.setOnClickListener {
                    onItemViewClick(itemView, adapterPosition)
                    DatePickerDialog(
                        itemView.context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            itemView.tvDateOfBirthday.text = calendar.time.toDateFormat()
                            runnerRegisterData[adapterPosition].dateOfBirthday = calendar.time
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                radioGroupSex.setOnCheckedChangeListener { _, checkedId ->
                    onItemViewClick(itemView, adapterPosition)
                    val runnerSex = when (checkedId) {
                        R.id.rbMale -> RunnerSex.MALE
                        R.id.rbFemale -> RunnerSex.FEMALE
                        else -> null
                    }
                    runnerRegisterData[adapterPosition].runnerSex = runnerSex
                }
                radioGroupRunnerType.setOnCheckedChangeListener { _, checkedId ->
                    onItemViewClick(itemView, adapterPosition)
                    val runnerType = when (checkedId) {
                        R.id.rbRunner -> RunnerType.NORMAL
                        R.id.rbIronRunner -> RunnerType.IRON
                        else -> null
                    }
                    runnerRegisterData[adapterPosition].runnerType = runnerType
                }
                setOnClickListener { onItemViewClick(itemView, adapterPosition) }
            }
        }

        private fun onItemViewClick(view: View, position: Int) {
            //deselect previous view
            selectedView?.let { selectView(it, false) }
            //select new view
            selectView(view, true)
            selectedPosition = position
            selectedView = view
        }

        private fun selectView(itemView: View, isSelected: Boolean) {
            val drawableId = if(isSelected){
                selectedView = itemView
                itemView.etRunnerFullName.addTextChangedListener(fullName)
                itemView.etRunnerNumber.addTextChangedListener(runnerNumber)
                itemView.etRunnerCity.addTextChangedListener(city)
                itemView.etRunnerPhoneNumber.addTextChangedListener(phone)
                R.drawable.bg_selected_bottom_corner_layout
            } else {
                itemView.etRunnerFullName.removeTextChangedListener(fullName)
                itemView.etRunnerNumber.removeTextChangedListener(runnerNumber)
                itemView.etRunnerCity.removeTextChangedListener(city)
                itemView.etRunnerPhoneNumber.removeTextChangedListener(phone)
                R.drawable.bg_bottom_corner_layout
            }
            itemView.constraintLayout.background = ContextCompat.getDrawable(itemView.context, drawableId)
        }
    }
}