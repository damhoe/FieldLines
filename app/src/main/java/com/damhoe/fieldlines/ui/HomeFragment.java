package com.damhoe.fieldlines.ui;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;
import com.example.fieldlines.R;
import com.example.fieldlines.databinding.FragmentHomeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class HomeFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, NotifyAddChargeRequestListener {

    FragmentHomeBinding binding;
    SharedViewModel viewModel;

    private boolean onTouch = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        // Setup Menu
        initializeMenu();

        binding.mainView.setAddChargeRequestListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getCharges().observe(getViewLifecycleOwner(), new Observer<ChargeList>() {
            @Override
            public void onChanged(ChargeList charges) {
                binding.mainView.setCharges(charges);
            }
        });

        setupAddChargeButton();
        setupEditChargeButton();

        // Refresh layout if button is clicked
        binding.buttonRefreshCoordinates.setOnClickListener(view1 -> {
            binding.mainView.recenter();
        });
    }

    private void setupEditChargeButton() {
        binding.buttonEditCharge.setOnClickListener(view -> {
            findNavController().navigate(R.id.action_homeFragment_to_editChargesFragment);
        });
    }

    private void setupAddChargeButton() {
        binding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(requireContext(), view);
                popup.getMenuInflater().inflate(R.menu.menu_add, popup.getMenu());
                popup.setOnMenuItemClickListener(HomeFragment.this);
                popup.show();
            }
        });
    }

    private void initializeMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu, menu);
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_about) {
                    findNavController().navigate(R.id.action_homeFragment_to_aboutFragment);
                }
                if (item.getItemId() == R.id.menu_help) {
                    findNavController().navigate(R.id.action_homeFragment_to_helpFragment);
                }
                if (item.getItemId() == R.id.menu_library) {
                    findNavController().navigate(R.id.action_homeFragment_to_libraryFragment);
                }
                return true;
            }
        }, getViewLifecycleOwner());
    }

    private NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    /*
    * Add charge button popup menu
    * */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_single_charge) {
            showAddSingleChargeDialog(null);
        }
        else if (menuItem.getItemId() == R.id.add_monopole) {
            viewModel.initializeMonopole();
        }
        else if (menuItem.getItemId() == R.id.add_dipole) {
            viewModel.initializeDipole();
        }
        else if (menuItem.getItemId() == R.id.add_quadropole) {
            viewModel.initializeQuadropole();
        }
        return true;
    }

    private void showAddSingleChargeDialog(@Nullable Point position) {

        View layout = getLayoutInflater().inflate(R.layout.dialog_single_charge, null, false);
        TextInputEditText editTextX = layout.findViewById(R.id.edit_x_coordinate);
        TextInputEditText editTextY = layout.findViewById(R.id.edit_y_coordinate);
        TextInputEditText editTextAmount = layout.findViewById(R.id.edit_amount);

        if (position != null) {
            editTextX.setText(String.valueOf(position.x));
            editTextY.setText(String.valueOf(position.y));
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add charge")
                .setView(layout)
                .setPositiveButton("OK", (d, i) -> {
                    int x = Integer.parseInt(editTextX.getEditableText().toString());
                    int y = Integer.parseInt(editTextY.getEditableText().toString());
                    double amount = Double.parseDouble(editTextAmount.getEditableText().toString());
                    createAndAddCharge(x, y, amount);
                    d.dismiss();
                })
                .setNegativeButton("Cancel", (d, i) -> d.cancel())
                .show();

    }

    private void createAndAddCharge(int x, int y, double amount) {
        Charge charge = new Charge(new Point(x, y), amount);
        viewModel.addCharge(charge);
    }

    @Override
    public void notifyNewAddChargeRequest(Point position) {
        showAddSingleChargeDialog(position);
    }
}