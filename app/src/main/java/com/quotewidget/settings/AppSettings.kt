package com.quotewidget.settings

import com.quotewidget.data.QuoteCategory

enum class ThemeMode(val title: String) {
    System("Как в системе"),
    Light("Светлая"),
    Dark("Темная")
}

enum class WidgetFrequency(val title: String) {
    Daily("Раз в день"),
    EverySixHours("Каждые 6 часов"),
    Manual("Вручную")
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val widgetCategories: Set<String> = QuoteCategory.titles,
    val widgetFrequency: WidgetFrequency = WidgetFrequency.Daily,
    val favoriteIds: Set<String> = emptySet(),
    val widgetSeed: Int = 0
)
