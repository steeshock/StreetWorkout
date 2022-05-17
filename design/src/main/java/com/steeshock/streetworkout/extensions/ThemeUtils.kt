package com.steeshock.streetworkout.extensions

import androidx.appcompat.app.AppCompatDelegate.*

/**
 * Map for indexes when displaying Theme Change dialog
 */
val themesMap: Map<Int, Int> = mapOf(
    0 to MODE_NIGHT_NO,
    1 to MODE_NIGHT_YES,
    2 to MODE_NIGHT_FOLLOW_SYSTEM,
)

/**
 * Returns simple dialog item index from theme constant
 */
fun getIndexByThemeName(): Int {
    val currentTheme = getDefaultNightMode()
    val index = themesMap.values.indexOf(currentTheme)
    return if (index >= 0) themesMap.values.indexOf(currentTheme) else 2
}

/**
 * Returns theme constant from simple dialog item index
 */
fun getThemeByIndex(itemIndex: Int): Int {
    return try {
        themesMap.getValue(itemIndex)
    } catch (e: Exception) {
        MODE_NIGHT_FOLLOW_SYSTEM
    }
}