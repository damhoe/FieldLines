package com.damhoe.fieldlines.charges.presentation

import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damhoe.fieldlines.charges.application.PointChargeDialogBuilder
import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.charges.presentation.ChargeItemAdapter.ChargeViewHolder
import com.damhoe.fieldlines.field.domain.Field
import com.damhoe.fieldlines.field.presentation.FieldViewModel
import com.example.fieldlines.R
import com.example.fieldlines.databinding.FragmentEditChargesBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar

class EditChargesFragment : Fragment() {
    private lateinit var binding: FragmentEditChargesBinding
    private val viewModel: FieldViewModel by viewModels ({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_edit_charges, container, false)

        addMenu()

        setupAddChargeButton()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.field.observe(viewLifecycleOwner) { field: Field -> updateAdapter(field) }

        // Setup adapter
        val adapter = ChargeItemAdapter {
            position, charge -> handleChargeSelection(position, charge)
        }

        viewModel.field.value?.let { adapter.updateCharges(it.pointCharges) }

        binding.chargesRecyclerView.run {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            val dividerItemDecoration = MaterialDividerItemDecoration(context,
                MaterialDividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun updateAdapter(field: Field) {
        (binding.chargesRecyclerView.adapter as ChargeItemAdapter)
            .updateCharges(field.pointCharges)
    }

    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.run {
                    clear()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
               return false
            }
        }, viewLifecycleOwner)
    }

    private fun findNavController(): NavController {
        return findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun handleChargeSelection(position: Int, charge: PointCharge) {
        val viewHolder = binding.chargesRecyclerView
            .findViewHolderForAdapterPosition(position) as ChargeViewHolder
        val popup = PopupMenu(requireContext(), viewHolder.binding.buttonMore)
        popup.menuInflater.inflate(R.menu.menu_charge, popup.menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.edit) {
                showEditChargeDialog(position, charge)
                return@OnMenuItemClickListener true
            }
            if (menuItem.itemId == R.id.delete) {
                val result = viewModel.removePointChargeAt(position)
                result.getOrNull()?.let { pointCharge ->
                    Snackbar.make(binding.root, "Removed charge", Snackbar.LENGTH_SHORT)
                        .setAction("Undo") {
                            viewModel.addPointChargeAt(position, pointCharge)
                        }
                        .show()
                }

                return@OnMenuItemClickListener true
            }
            false
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }
        popup.show()
    }

    private fun setupAddChargeButton() {
        binding.buttonAdd.setOnClickListener {

            val defaultCoordinates = PointF(0f, 0f)
            val defaultCharge = 1.0

            PointChargeDialogBuilder(requireContext())
                .setTitle("Create charge")
                .setCharge(defaultCharge)
                .setPoint(defaultCoordinates)
                .setPositiveButton("Create") { x, y, charge ->
                    viewModel.createPointCharge(x, y, charge).onFailure {
                        Snackbar.make(
                            binding.root,
                            "Failed: ${it.message}",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("OK") {  }
                            .show()
                    }
                }
                .setNegativeButton("Cancel") { d, _ -> d.cancel() }
                .create().show()
        }
    }

    private fun showEditChargeDialog(position: Int, pointCharge: PointCharge) {
        PointChargeDialogBuilder(requireContext())
            .setTitle("Edit charge")
            .setPoint(pointCharge.position)
            .setCharge(pointCharge.charge)
            .setPositiveButton("Save") { x, y, charge ->
                viewModel.updateCharge(position, x, y, charge).onFailure {
                    Snackbar.make(
                        binding.root,
                        "Failed: ${it.message}",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("OK") {  }
                        .show()
                }
            }
            .setNegativeButton("Cancel") { d, _ -> d.cancel() }
            .create().show()
    }
}
