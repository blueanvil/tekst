package com.blueanvil.tekst

/**
 * @author Cosmin Marginean
 */
data class TextMatch(val text: String,
                     val startIndex: Int) {

    val length: Int
        get() = text.length

    val endIndex: Int
        get() = startIndex + text.length

    companion object {

        fun extract(sourceText: String, startIndex: Int, endIndex: Int): TextMatch {
            return TextMatch(sourceText.substring(startIndex, endIndex), startIndex)
        }
    }
}