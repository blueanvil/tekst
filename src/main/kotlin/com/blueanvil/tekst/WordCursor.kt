package com.blueanvil.tekst

data class WordCursor(val startIndex: Int) {

    private val text = StringBuilder()

    fun addChar(char: Char) {
        text.append(char)
    }

    fun word(): TextMatch {
        return TextMatch(text.toString(), startIndex)
    }
}