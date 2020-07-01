package com.blueanvil.tekst

import org.tartarus.snowball.SnowballStemmer
import java.io.OutputStream
import java.nio.charset.StandardCharsets


/**
 * @author Cosmin Marginean
 */
object Tekst {

    private val HIGHLIGHT_TEMPLATE = Tekst::class.java.getResourceAsStream("/highlight-template.html").asString()
    private val ALL_WHITESPACE = "\u0009\u000A\u000B\u000C\u000D\u0020\u0085\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u2028\u2029\u202F\u205F\u3000"
    private val DELIMITERS =
            """
                |/\-—(){}[]<>"“”‘’«»*%^&~`:;,.!?
            """.trim()

    internal val ALL_DELIMITERS = DELIMITERS + ALL_WHITESPACE

    fun words(text: String): List<TextMatch> {
        val words = mutableListOf<TextMatch>()
        val chars = text.toCharArray()

        var cursor: WordCursor? = null
        for (i in text.indices) {
            var char = chars[i]

            if (char.isWhitespace() || ALL_DELIMITERS.contains(char)) {
                // We're in delimiter territory. Check if there is a current word and close that if necessary
                if (cursor != null) {
                    words.add(cursor.word())
                    cursor = null
                }
            } else {
                if (cursor == null) {
                    // A new word is starting
                    cursor = WordCursor(i)
                }
                cursor.addChar(char)
            }
        }

        if (cursor != null) {
            words.add(cursor.word())
        }
        return words
    }

    fun find(text: String, searchString: String, stemmingLanguage: StemmingLanguage? = null): List<TextMatch> {
        return find(text, listOf(searchString), stemmingLanguage)[searchString]!!
    }

    fun find(text: String, searchStrings: List<String>, stemmingLanguage: StemmingLanguage? = null): Map<String, List<TextMatch>> {
        val textWords = words(text)
        val state = FindState(searchStrings)
        val stemmer = stemmingLanguage?.newStemmer()

        for (textWordIndex in textWords.indices) {
            searchStrings.forEach { searchString ->
                val searchStringWords = state.words[searchString]!!
                if (sameWord(textWords[textWordIndex].text, searchStringWords[0].text, stemmer)
                        && textWordIndex + searchStringWords.size <= textWords.size) {
                    var matchesSequence = true
                    for (j in 1 until searchStringWords.size) {
                        if (!sameWord(textWords[textWordIndex + j].text, searchStringWords[j].text, stemmer)) {
                            matchesSequence = false
                            break
                        }
                    }
                    if (matchesSequence) {
                        val startIndex = textWords[textWordIndex].startIndex
                        val endIndex = textWords[textWordIndex + searchStringWords.size - 1].endIndex
                        state.matches[searchString]!!.add(TextMatch.extract(text, startIndex, endIndex))
                    }
                }
            }
        }
        return state.matches
    }

    /**
     * This is only a tool to demonstrate Tekst capabilities and
     * to help with diagnosis and debugging
     */
    fun highlightHtml(text: String, searchStrings: List<String>, output: OutputStream, stemmingLanguage: StemmingLanguage? = null) {
        val matches = mutableListOf<TextMatch>()
        find(text, searchStrings, stemmingLanguage)
                .values
                .forEach { textMatches -> matches.addAll(textMatches) }

        val highlightedText =
                if (matches.isEmpty()) {
                    text
                } else {
                    val inverted = TextMatch.invertMatches(text, matches.sorted())

                    val allMatches = mutableListOf<TextMatch>()
                    allMatches.addAll(matches)
                    allMatches.addAll(inverted)
                    allMatches.sort()

                    val sb = StringBuilder()
                    allMatches.forEach { match ->
                        if (match in matches) {
                            sb.append("<span class=\"highlight\">${match.text}</span>")
                        } else {
                            sb.append(match.text)
                        }
                    }
                    sb.toString()
                }

        val htmlOutput = HIGHLIGHT_TEMPLATE
                .replace("SEARCH_STRING", searchStrings.joinToString(", ") { "<span class=\"highlight\">${it}</span>" })
                .replace("ORIGINAL_TEXT", text)
                .replace("HIGHLIGHTED_TEXT", highlightedText)

        output.write(htmlOutput.toByteArray(StandardCharsets.UTF_8))
    }

    internal fun sameWord(word1: String, word2: String, stemmer: SnowballStemmer? = null): Boolean {
        return if (stemmer != null) {
            stemmer.stem(word1) == stemmer.stem(word2)
        } else {
            word1.toLowerCase() == word2.toLowerCase()
        }
    }
}

data class FindState(val searchStrings: List<String>) {

    val words = searchStrings.associate { it to Tekst.words(it) }
    val matches = searchStrings.associate { it to mutableListOf<TextMatch>() }
}