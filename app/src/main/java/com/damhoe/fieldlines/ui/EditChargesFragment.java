package com.damhoe.fieldlines.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damhoe.fieldlines.domain.Charge;
import com.damhoe.fieldlines.domain.ChargeList;
import com.damhoe.fieldlines.ui.EditableChargeAdapter.ChargeViewHolder;
import com.example.fieldlines.R;
import com.example.fieldlines.databinding.FragmentEditChargesBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;


public class EditChargesFragment extends Fragment implements NotifyItemClickListener {

    FragmentEditChargesBinding binding;
    SharedViewModel viewModel;
    ActionMode actionMode;

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
        binding.chargesRecyclerView.addItemDecoration(new EditableChargeAdapter.ItemDecoration(4));
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
                menu.findItem(R.id.menu_help).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
        ChargeViewHolder viewHolder = (ChargeViewHolder)
                binding.chargesRecyclerView.findViewHolderForAdapterPosition(position);

        if (viewHolder == null) {
            return;
        }

        PopupMenu popup = new PopupMenu(requireContext(), viewHolder.buttonMore);
        popup.getMenuInflater().inflate(R.menu.menu_charge, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.edit) {
                    showEditChargeDialog(position, charge);
                    return true;
                }
                if (menuItem.getItemId() == R.id.delete) {
                    Charge removedCharge = viewModel.removeCharge(position);
                    Snackbar.make(binding.getRoot(), "Removed charge", Snackbar.LENGTH_SHORT)
                            .setAction("Undo", view -> {
                                viewModel.addCharge(position, removedCharge);
                            })
                            .show();
                    return true;
                }
                return false;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.show();
    }

    private void showEditChargeDialog(int position, Charge charge) {
        new ChargeAlertDialogFactory(requireContext(), new ChargeRequestListener() {
            @Override
            public void notifyChargeRequest(int x, int y, double amount) {
                viewModel.updateCharge(position, x, y, amount);
            }
        }).createEditChargeDialog(charge).show();
    }
}