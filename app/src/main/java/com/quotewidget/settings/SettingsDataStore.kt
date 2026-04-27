package com.quotewidget.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.quotewidget.data.QuoteCategory
import com.quotewidget.widget.WidgetUpdateWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

val Context.quoteWidgetDataStore: DataStore<Preferences> by preferencesDataStore("quote_widget_settings")

class SettingsDataStore(private val context: Context) {
    val settings: Flow<AppSettings> = context.quoteWidgetDataStore.data.map { preferences ->
        AppSettings(
            themeMode = preferences[Keys.themeMode].toEnum(ThemeMode.System),
            widgetCategories = preferences[Keys.widgetCategories]?.splitCsv()?.ifEmpty { QuoteCategory.titles }
                ?: QuoteCategory.titles,
            widgetFrequency = preferences[Keys.widgetFrequency].toEnum(WidgetFrequency.Daily),
            favoriteIds = preferences[Keys.favoriteIds]?.splitCsv().orEmpty(),
            widgetSeed = preferences[Keys.widgetSeed] ?: 0
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.quoteWidgetDataStore.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun setWidgetFrequency(frequency: WidgetFrequency) {
        context.quoteWidgetDataStore.edit { it[Keys.widgetFrequency] = frequency.name }
        scheduleWidgetUpdates(context, frequency)
    }

    suspend fun toggleWidgetCategory(category: String) {
        context.quoteWidgetDataStore.edit { preferences ->
            val current = preferences[Keys.widgetCategories]?.splitCsv()?.toMutableSet()
                ?: QuoteCategory.titles.toMutableSet()
            if (category in current) current.remove(category) else current.add(category)
            preferences[Keys.widgetCategories] = current.ifEmpty { QuoteCategory.titles }.joinToString(",")
        }
    }

    suspend fun toggleFavorite(id: String) {
        context.quoteWidgetDataStore.edit { preferences ->
            val current = preferences[Keys.favoriteIds]?.splitCsv()?.toMutableSet() ?: mutableSetOf()
            if (id in current) current.remove(id) else current.add(id)
            preferences[Keys.favoriteIds] = current.joinToString(",")
        }
    }

    suspend fun nextWidgetSeed() {
        context.quoteWidgetDataStore.edit { preferences ->
            preferences[Keys.widgetSeed] = (preferences[Keys.widgetSeed] ?: 0) + 1
        }
    }

    companion object {
        const val WORK_NAME = "quote_widget_periodic_update"

        fun scheduleWidgetUpdates(context: Context, frequency: WidgetFrequency) {
            val workManager = WorkManager.getInstance(context)
            if (frequency == WidgetFrequency.Manual) {
                workManager.cancelUniqueWork(WORK_NAME)
                return
            }
            val interval = if (frequency == WidgetFrequency.EverySixHours) 6L else 1L
            val unit = if (frequency == WidgetFrequency.EverySixHours) TimeUnit.HOURS else TimeUnit.DAYS
            val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(interval, unit).build()
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val widgetCategories = stringPreferencesKey("widget_categories")
        val widgetFrequency = stringPreferencesKey("widget_frequency")
        val favoriteIds = stringPreferencesKey("favorite_ids")
        val widgetSeed = intPreferencesKey("widget_seed")
    }
}

private inline fun <reified T : Enum<T>> String?.toEnum(default: T): T {
    return enumValues<T>().firstOrNull { it.name == this } ?: default
}

private fun String.splitCsv(): Set<String> {
    return split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
}
