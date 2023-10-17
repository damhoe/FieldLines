package com.damhoe.fieldlines.ui;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;
import com.example.fieldlines.R;
import com.example.fieldlines.databinding.FragmentEditChargesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;


public class EditChargesFragment extends Fragment implements NotifyItemClickListener {

    FragmentEditChargesBinding binding;
    SharedViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_charges, container, false);
        initializeMenu();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getCharges().observe(getViewLifecycleOwner(), this::updateAdapter);

        // Setup adapter
        EditableChargeAdapter adapter = new EditableChargeAdapter(this);
        adapter.updateCharges(viewModel.getCharges().getValue());
        binding.chargesRecyclerView.setAdapter(adapter);
        binding.chargesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chargesRecyclerView.addItemDecoration(new EditableChargeAdapter.ItemDecoration());
    }

    private void updateAdapter(ChargeList charges) {
        EditableChargeAdapter adapter = (EditableChargeAdapter)binding.chargesRecyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateCharges(charges);
        }
    }

    private void initializeMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu, menu);
                menu.removeItem(R.id.menu_library);
                menu.removeItem(R.id.menu_about);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_help) {
                    findNavController().navigate(R.id.action_editChargesFragment_to_helpFragment);
                }
                return true;
            }
        }, getViewLifecycleOwner());
    }

    private NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public void notifyChargeClicked(int position, Charge charge) {
        // Open edit charge dialog
        showEditChargeDialog(position, charge);
    }

    private void showEditChargeDialog(int position, Charge charge) {

        View layout = getLayoutInflater().inflate(R.layout.dialog_single_charge, null, false);
        TextInputEditText editTextX = layout.findViewById(R.id.edit_x_coordinate);
        TextInputEditText editTextY = layout.findViewById(R.id.edit_y_coordinate);
        TextInputEditText editTextAmount = layout.findViewById(R.id.edit_amount);

        if (charge != null) {
            editTextX.setText(String.valueOf(charge.Position.x));
            editTextY.setText(String.valueOf(charge.Position.y));
            editTextAmount.setText(String.format(Locale.US, "%.2f", charge.Amount));
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add charge")
                .setView(layout)
                .setPositiveButton("OK", (d, i) -> {
                    int x = Integer.parseInt(editTextX.getEditableText().toString());
                    int y = Integer.parseInt(editTextY.getEditableText().toString());
                    double amount = Double.parseDouble(editTextAmount.getEditableText().toString());
                    viewModel.updateCharge(position, x, y, amount);
                    d.dismiss();
                })
                .setNegativeButton("Cancel", (d, i) -> d.cancel())
                .show();

    }
}