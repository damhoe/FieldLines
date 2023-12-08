package com.damhoe.fieldlines.field.presentation

import android.app.Application
import android.graphics.PointF
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.damhoe.fieldlines.field.domain.Field
import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.settings.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class FieldViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)

    val showAxes = settingsManager.showAxesFlow.asLiveData(Dispatchers.IO)

    fun setShowAxes(showAxes: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowAxes(showAxes = showAxes)
        }
    }

    val maxLinesCount = settingsManager.maxLinesCountFlow.asLiveData(Dispatchers.IO)

    fun setMaxLinesCount(value: Int) {
        viewModelScope.launch { settingsManager.setMaxLinesCount(value) }
    }

    private val mField = MutableLiveData<Field>().apply { value = Field.monopole() }

    var field: LiveData<Field> = mField

    fun initializeMonopole() = mField.postValue(Field.monopole())
    fun initializeDipole() = mField.postValue(Field.dipole())
    fun initializeQuadropole() = mField.postValue(Field.quadropole())

    fun createPointCharge(x: Float, y: Float, charge: Double) =
        mField.value?.run {
            PointCharge(position = PointF(x, y), charge = charge).let { charge ->
                addPointCharge(charge)
                    .onSuccess { mField.postValue(this) }
                    .mapCatching { charge }
            }
        } ?: failure(IllegalStateException("Field is not initialized"))

    fun addPointChargeAt(position: Int, charge: PointCharge) =
        mField.value?.run {
            val result = addPointChargeAt(position, charge)
            // Update LiveData
            mField.postValue(this)
            success(result)
        } ?: failure(IllegalStateException("Field is not initialized"))

    fun removePointChargeAt(position: Int): Result<PointCharge> =
        mField.value?.run {
            removePointChargeAt(position)
                .onSuccess { mField.postValue(this) }
        } ?: failure(IllegalStateException("Field is not initialized"))

    fun updateCharge(position: Int, newX: Float, newY: Float, newCharge: Double): Result<Unit> =
        mField.value?.run {
            updatePointChargeAt(position, newX, newY, newCharge)
                .onSuccess { mField.postValue(this) }
        } ?: failure(IllegalStateException("Field is not initialized"))
}