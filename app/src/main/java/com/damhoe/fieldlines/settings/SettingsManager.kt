package com.damhoe.fieldlines.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(val context: Context) {

    private val showAxesDefault: Boolean = true
    private val maxLinesCountDefault: Int = 20

    companion object {
        val SHOW_AXES = booleanPreferencesKey("show_axes_preference")
        val MAX_LINES_COUNT = intPreferencesKey("max_lines_count_preference")
    }

    val showAxesFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences: Preferences ->
            preferences[SHOW_AXES] ?: showAxesDefault
        }

    suspend fun setShowAxes(showAxes: Boolean) = context.dataStore
        .edit { preferences -> preferences[SHOW_AXES] = showAxes }

    val maxLinesCountFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[MAX_LINES_COUNT] ?: maxLinesCountDefault
        }

    suspend fun setMaxLinesCount(maxLinesCount: Int) = context.dataStore
        .edit { it[MAX_LINES_COUNT] = maxLinesCount }
}
