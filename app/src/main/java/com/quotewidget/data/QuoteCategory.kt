package com.quotewidget.data

enum class QuoteCategory(val title: String) {
    Philosophy("философия"),
    Discipline("дисциплина"),
    Business("бизнес"),
    Success("успех"),
    Life("жизнь"),
    Science("наука");

    companion object {
        val titles: Set<String> = entries.map { it.title }.toSet()
    }
}
