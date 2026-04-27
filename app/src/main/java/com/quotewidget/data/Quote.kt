package com.quotewidget.data

data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val source: String?,
    val isInspiredBy: Boolean
)
