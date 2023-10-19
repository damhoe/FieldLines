package com.damhoe.fieldlines.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.damhoe.fieldlines.domain.Charge;
import com.example.fieldlines.R;
import com.example.fieldlines.databinding.DialogSingleChargeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class ChargeAlertDialogFactory {
    Context context;
    DialogSingleChargeBinding binding;
    MaterialAlertDialogBuilder dialogBuilder;
    ChargeRequestListener listener;

    public ChargeAlertDialogFactory(Context context, ChargeRequestListener listener) {
        this.context = context;
        this.listener = listener;
        initialize();
    }

    private void initialize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_single_charge, null, false);
        dialogBuilder = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot());
    }

    public AlertDialog createAddChargeDialog() {
        AlertDialog dialog = createChargeDialog();
        dialog.setTitle(R.string.title_add_charge);
        return dialog;
    }

    public AlertDialog createAddChargeDialog(Point point) {
        AlertDialog dialog = createChargeDialog();
        dialog.setTitle(R.string.title_add_charge);
        binding.editXCoordinate.setText(String.valueOf(point.x));
        binding.editYCoordinate.setText(String.valueOf(point.y));
        return dialog;
    }

    public AlertDialog createEditChargeDialog(Charge charge) {
        AlertDialog dialog = createChargeDialog();
        dialog.setTitle(R.string.title_edit_charge);
        binding.editXCoordinate.setText(String.valueOf(charge.position.x));
        binding.editYCoordinate.setText(String.valueOf(charge.position.y));
        binding.editAmount.setText(String.valueOf(charge.amount));
        return dialog;
    }

    private AlertDialog createChargeDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot())
                .setPositiveButton("OK", (d, i) -> {
                    int x = getX();
                    int y = getY();
                    double amount = getAmount();
                    listener.notifyChargeRequest(x, y, amount);
                    d.dismiss();
                })
                .setNegativeButton("Cancel", (d, i) -> d.cancel())
                .create();

        addTextWatcher(binding.editXCoordinate, dialog);
        addTextWatcher(binding.editYCoordinate, dialog);
        addTextWatcher(binding.editAmount, dialog);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                invalidatePositiveButton(dialog.getButton(DialogInterface.BUTTON_POSITIVE));
            }
        });
        return dialog;
    }

    private double getAmount() {
        return Double.parseDouble(binding.editAmount.getEditableText().toString());
    }

    private int getY() {
        return Integer.parseInt(binding.editYCoordinate.getEditableText().toString());
    }

    private int getX() {
        return Integer.parseInt(binding.editXCoordinate.getEditableText().toString());
    }

    private void addTextWatcher(TextInputEditText binding, AlertDialog dialog) {
        binding.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Ignore.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Ignore.
            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidatePositiveButton(dialog.getButton(DialogInterface.BUTTON_POSITIVE));
            }
        });
    }

    private void invalidatePositiveButton(Button button) {
        boolean isValidX = !binding.editXCoordinate.getEditableText().toString().isEmpty();
        boolean isValidY = !binding.editYCoordinate.getEditableText().toString().isEmpty();
        boolean isValidAmount = !binding.editAmount.getEditableText().toString().isEmpty();
        if (button != null) {
            button.setEnabled(isValidX && isValidAmount && isValidY);
        }
    }
}
