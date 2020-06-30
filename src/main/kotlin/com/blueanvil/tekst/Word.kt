package com.blueanvil.tekst

/**
 * @author Cosmin Marginean
 */
data class Word(val text: String,
                val startIndex: Int) {

    val length: Int
        get() = text.length

    val endIndex: Int
        get() = startIndex + text.length - 1
}