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
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemRegisterNewRunnerBinding
import java.util.*


data class InputDataView(
    var fullName: String? = null,
    var shortName: String? = null,
    var phone: String? = null,
    var runnerSex: RunnerSex? = null,
    var dateOfBirthday: Date? = Date(),
    var city: String? = null,
    var runnerNumber: String? = null,
    var runnerCardId: String? = null
) {
    fun isEmpty(): Boolean = fullName.isNullOrEmpty() ||
            phone.isNullOrEmpty() ||
            runnerSex == null ||
            dateOfBirthday == null ||
            city.isNullOrEmpty() ||
            runnerNumber == null
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

    private var selectedViewBinding: ItemRegisterNewRunnerBinding? = null
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

        val binding = ItemRegisterNewRunnerBinding.bind(itemView)

        private val fullName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnerRegisterData[adapterPosition].fullName = s?.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        private val runnerNumber = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                runnerRegisterData[adapterPosition].runnerNumber = s?.toString()
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
            with(binding) {
                etRunnerFullName.setText(item.fullName.orEmpty())
                etRunnerNumber.setText(item.runnerNumber?.toString().orEmpty())
                etRunnerPhoneNumber.setText(item.phone.orEmpty())
                tvDateOfBirthday.text = item.dateOfBirthday?.toDateFormat()
                tvScanCard.text = tvScanCard.context.resources.getString(
                    R.string.card,
                    item.runnerCardId.orEmpty()
                )
                etRunnerCity.setText(item.city.orEmpty())
                when (item.runnerSex) {
                    RunnerSex.MALE -> radioGroupSex.check(R.id.rbMale)
                    RunnerSex.FEMALE -> radioGroupSex.check(R.id.rbFemale)
                    else -> radioGroupSex.clearCheck()
                }
                val focusChange = View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) onItemViewClick(adapterPosition)
                }

                etRunnerFullName.onFocusChangeListener = focusChange
                etRunnerNumber.onFocusChangeListener = focusChange
                tvDateOfBirthday.onFocusChangeListener = focusChange
                etRunnerCity.onFocusChangeListener = focusChange

                tvDateOfBirthday.setOnClickListener {
                    onItemViewClick(adapterPosition)
                    DatePickerDialog(
                        itemView.context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            tvDateOfBirthday.text = calendar.time.toDateFormat()
                            runnerRegisterData[adapterPosition].dateOfBirthday =
                                calendar.time
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                radioGroupSex.setOnCheckedChangeListener { _, checkedId ->
                    onItemViewClick(adapterPosition)
                    val runnerSex = when (checkedId) {
                        R.id.rbMale -> RunnerSex.MALE
                        R.id.rbFemale -> RunnerSex.FEMALE
                        else -> null
                    }
                    runnerRegisterData[adapterPosition].runnerSex = runnerSex
                }
                rootItem.setOnClickListener { onItemViewClick(adapterPosition) }
            }
        }

        private fun onItemViewClick(position: Int) {
            //deselect previous view
            selectedViewBinding?.let { selectView(it, false) }
            //select new view
            selectView(binding, true)
            selectedPosition = position
            selectedViewBinding = binding
        }

        private fun selectView(currentBinding: ItemRegisterNewRunnerBinding, isSelected: Boolean) {
            with(currentBinding) {
                val drawableId = if (isSelected) {
                    selectedViewBinding = currentBinding
                    etRunnerFullName.addTextChangedListener(fullName)
                    etRunnerNumber.addTextChangedListener(runnerNumber)
                    etRunnerCity.addTextChangedListener(city)
                    etRunnerPhoneNumber.addTextChangedListener(phone)
                    R.drawable.bg_selected_bottom_corner_layout
                } else {
                    etRunnerFullName.removeTextChangedListener(fullName)
                    etRunnerNumber.removeTextChangedListener(runnerNumber)
                    etRunnerCity.removeTextChangedListener(city)
                    etRunnerPhoneNumber.removeTextChangedListener(phone)
                    R.drawable.bg_bottom_corner_layout
                }
                constraintLayout.background = ContextCompat.getDrawable(itemView.context, drawableId)
            }
        }
    }
}