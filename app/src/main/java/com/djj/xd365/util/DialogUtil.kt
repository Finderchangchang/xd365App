package com.djj.xd365.util

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import com.djj.xd365.R
import java.util.*

/**
 * Created by liufeng on 2017-07-17.
 */
object DialogUtil {
    var dialog: Dialog? = null

    interface OnDialogClick {
        fun onPositiveClick()
        fun onNegativeClick()
    }


    fun showMsgDialog(context: Context, message: String, listener: OnDialogClick) {
        removeDialog()
        var builder = AlertDialog.Builder(context)
        with(builder) {
            setTitle("提示")
            setMessage(message)
            setPositiveButton("确定", { dialog, which -> listener.onPositiveClick() })
            setNegativeButton("取消", { dialog, which -> listener.onNegativeClick() })
            val dia = create()
            dia.show()
            dialog = dia
        }
    }

    fun removeDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    fun setTimeDialog(context: Context, view: EditText) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(context, R.style.AppTheme_Dialog, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDay, minute ->
            val time = String.format("%02d:%02d", hourOfDay, minute)
            view.setText(time)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消") { dialogInterface, i -> view.setText("") }
        timePickerDialog.show()
    }

    fun setDateDialog(context: Context, view: EditText) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = DatePickerDialog(context, R.style.AppTheme_Dialog, DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            val time = String.format("%04d年%02d月%02d日", year, month + 1, dayOfMonth)
            view.setText(time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        timePickerDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消") { dialogInterface, i -> view.setText("") }
        timePickerDialog.show()
    }

}