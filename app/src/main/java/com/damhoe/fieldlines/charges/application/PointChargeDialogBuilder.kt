package com.damhoe.fieldlines.charges.application

import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.damhoe.fieldlines.app.Vector
import com.example.fieldlines.R
import com.example.fieldlines.databinding.DialogSingleChargeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class PointChargeDialogBuilder(context: Context) : MaterialAlertDialogBuilder(context) {

    val binding: DialogSingleChargeBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.dialog_single_charge, null, false)

    val charge: Double
        get() = binding.editAmount.editableText.toString().toDouble()
    val y: Float
        get() = binding.editYCoordinate.editableText.toString().toFloat()
    val x: Float
        get() = binding.editXCoordinate.editableText.toString().toFloat()

    fun setPoint(point: Vector) = binding.run {
        editXCoordinate.text = SpannableStringBuilder.valueOf("%.1f".format(point.x))
        editYCoordinate.text = SpannableStringBuilder.valueOf("%.1f".format(point.y))
        this@PointChargeDialogBuilder
    }

    fun setCharge(charge: Double) = binding.run {
        binding.editAmount.text = SpannableStringBuilder.valueOf("%.2f".format(charge))
        this@PointChargeDialogBuilder
    }

    fun setPositiveButton(
        text: String,
        onClick: (x: Double, y: Double, charge: Double) -> Unit
    ) : PointChargeDialogBuilder {
        setPositiveButton(text) { _, _ ->
            val x = binding.editXCoordinate.text.toString().toDouble()
            val y = binding.editYCoordinate.text.toString().toDouble()
            val charge = binding.editAmount.text.toString().toDouble()
            onClick(x, y, charge)
        }
        return this@PointChargeDialogBuilder
    }

    override fun setNegativeButton(
        text: CharSequence?,
        listener: DialogInterface.OnClickListener?
    ): PointChargeDialogBuilder {
        super.setNegativeButton(text, listener)
        return this@PointChargeDialogBuilder
    }

    override fun setTitle(title: CharSequence?): PointChargeDialogBuilder {
        super.setTitle(title)
        return this@PointChargeDialogBuilder
    }

    override fun create(): AlertDialog {
        setView(binding.root)

        val dialog = super.create()

        addTextWatcher(binding.editAmount, dialog)
        addTextWatcher(binding.editXCoordinate, dialog)
        addTextWatcher(binding.editYCoordinate, dialog)

        return dialog
    }

    private fun addTextWatcher(binding: TextInputEditText, dialog: AlertDialog) {
        binding.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Ignore.
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Ignore.
            }

            override fun afterTextChanged(s: Editable) {
                updateButtonState(dialog.getButton(DialogInterface.BUTTON_POSITIVE))
            }
        })
    }

    private fun updateButtonState(button: Button) =
        binding.run {
            button.isEnabled = editXCoordinate.editableText.isNotEmpty() and
                    editYCoordinate.editableText.isNotEmpty() and
                    editAmount.editableText.isNotEmpty()
        }
}
