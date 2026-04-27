package com.quotewidget.data

import com.quotewidget.quotes.LocalQuotes
import java.time.LocalDate
import kotlin.math.abs

class QuoteRepository {
    val allQuotes: List<Quote> = LocalQuotes.items

    fun quoteOfDay(categories: Set<String> = QuoteCategory.titles): Quote {
        val pool = filtered(categories)
        val day = LocalDate.now().toEpochDay()
        return pool[abs(day.toInt()) % pool.size]
    }

    fun quoteForWidget(categories: Set<String>, seed: Int): Quote {
        val pool = filtered(categories.ifEmpty { QuoteCategory.titles })
        val day = LocalDate.now().toEpochDay().toInt()
        return pool[abs(day + seed) % pool.size]
    }

    fun nextQuote(currentId: String?, category: String?): Quote {
        val pool = filtered(if (category == null) QuoteCategory.titles else setOf(category))
        val currentIndex = pool.indexOfFirst { it.id == currentId }
        return pool[(currentIndex + 1).floorMod(pool.size)]
    }

    fun byIds(ids: Set<String>): List<Quote> {
        return allQuotes.filter { it.id in ids }
    }

    private fun filtered(categories: Set<String>): List<Quote> {
        return allQuotes.filter { it.category in categories }.ifEmpty { allQuotes }
    }

    private fun Int.floorMod(other: Int): Int = ((this % other) + other) % other
}
