package com.damhoe.fieldlines.field.presentation

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.compose.material3.Snackbar
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.damhoe.fieldlines.charges.application.PointChargeDialogBuilder
import com.example.fieldlines.R
import com.example.fieldlines.databinding.DialogSettingsBinding
import com.example.fieldlines.databinding.FragmentFieldBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class FieldFragment : Fragment() {

    private lateinit var binding: FragmentFieldBinding
    private val viewModel: FieldViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_field, container, false)

        // Setup Menu
        addMenu()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showAxes.observe(viewLifecycleOwner) {
            binding.fieldView.options.apply { showAxes = it }
            binding.fieldView.invalidate()
        }

        viewModel.maxLinesCount.observe(viewLifecycleOwner) {
            with(binding.fieldView) {
                options.apply { maxLinesCount = it }
                invalidate()
            }
        }

        // Observe field configuration changes
        viewModel.field.observe(viewLifecycleOwner) { binding.fieldView.updateField(it) }

        setupAddChargeButton()
        setupEditButton()

        // Refresh layout if button is clicked
        binding.buttonRefreshTransform
            .setOnClickListener{ binding.fieldView.centerAndResetScale() }
    }

    private val editFieldListener = PopupMenu.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.edit_charges -> navigateToEditCharges()
            R.id.add_monopole -> viewModel.initializeMonopole()
            R.id.add_dipole -> viewModel.initializeDipole()
            R.id.add_quadropole -> viewModel.initializeQuadropole()
        }
        true
    }

    private fun setupEditButton() {
        val editFieldPopup = PopupMenu(requireContext(), binding.buttonEdit).apply {
            menuInflater.inflate(R.menu.menu_edit, this.menu)
            setOnMenuItemClickListener(editFieldListener)
        }

        binding.buttonEdit.setOnClickListener { editFieldPopup.show() }
    }

    private fun navigateToEditCharges() = with(findNavController()) {
        val directions =
            FieldFragmentDirections.actionFieldFragmentToEditChargesFragment()
        navigate(directions)
    }

    private fun setupAddChargeButton() {
        binding.buttonAdd.setOnClickListener { startCreateChargeDialog() }
    }

    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when(item.itemId) {
                    R.id.menu_settings -> showSettings()
                }
                return true
            }
        }, viewLifecycleOwner)
    }

    private fun showSettings() {

        val dialogBinding: DialogSettingsBinding =
            DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.dialog_settings, null, false)

        dialogBinding.switchShowAxes.setOnCheckedChangeListener {
            _, newState -> viewModel.setShowAxes(newState)
        }

        dialogBinding.sliderFieldLinesCount.addOnChangeListener {
            _, newValue, _ -> viewModel.setMaxLinesCount(newValue.toInt())
        }

        // Init states
        dialogBinding.switchShowAxes.isChecked = viewModel.showAxes.value ?: false

        viewModel.maxLinesCount.value?.also {
            dialogBinding.sliderFieldLinesCount.value = it.toFloat()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_settings))
            .setView(dialogBinding.root)
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .create()

        dialogBinding.buttonAbout.setOnClickListener {
            dialog.dismiss()
            navigateToAbout()
        }

        dialogBinding.buttonHelp.setOnClickListener {
            dialog.dismiss()
            navigateToHelp()
        }

        dialog.show()
    }

    private fun navigateToAbout() {
        findNavController().navigate(R.id.action_fieldFragment_to_aboutFragment)
    }

    private fun navigateToHelp() {
        findNavController().navigate(R.id.action_fieldFragment_to_helpFragment)
    }

    private fun navigateToLibrary() {
        findNavController().navigate(R.id.action_fieldFragment_to_libraryFragment)
    }

    private fun findNavController(): NavController =
        findNavController(requireActivity(), R.id.nav_host_fragment)

    fun startCreateChargeDialog(position: PointF? = null) {
        val point = position ?: PointF(0f, 0f)
        val defaultCharge = 1.0

        PointChargeDialogBuilder(requireContext())
            .setTitle("Create charge")
            .setPoint(point)
            .setCharge(defaultCharge)
            .setPositiveButton("Create") { x, y, charge ->
                viewModel.createPointCharge(x, y, charge)
                    .onFailure {
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