package com.blueanvil.tekst

/**
 * @author Cosmin Marginean
 */
data class TextMatch(val text: String,
                     val startIndex: Int) : Comparable<TextMatch> {

    val length: Int
        get() = text.length

    val endIndex: Int
        get() = startIndex + text.length

    override fun compareTo(other: TextMatch): Int {
        return startIndex.compareTo(other.startIndex)
    }

    companion object {

        fun extract(sourceText: String, startIndex: Int, endIndex: Int): TextMatch {
            return TextMatch(sourceText.substring(startIndex, endIndex), startIndex)
        }

        fun invertMatches(sourceText: String, matches: List<TextMatch>): List<TextMatch> {
            val inverses = mutableListOf<TextMatch>()

            matches.forEachIndexed { index, match ->

                if (index == 0) {
                    // This is the first match so if there's anything available before we should produce a match
                    if (match.startIndex > 0)
                        inverses.add(extract(sourceText, 0, match.startIndex))
                } else {
                    // For all other matches add the text between the current and the previous one
                    val startIndex = matches[index - 1].endIndex
                    if (startIndex < match.startIndex) {
                        val inverse = extract(sourceText, startIndex, match.startIndex)
                        inverses.add(inverse)
                    }
                }

                if (index == matches.size - 1) {
                    // This is the last match so if there's anything available after we should produce a match
                    if (match.endIndex < sourceText.length)
                        inverses.add(extract(sourceText, match.endIndex, sourceText.length))
                }
            }

            return inverses
        }
    }
}