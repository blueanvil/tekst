package com.blueanvil.tekst

import org.tartarus.snowball.SnowballStemmer

/**
 * @author Cosmin Marginean
 */
object TekstParser {

    private val DELIMITERS =
            """
                |/\-—(){}[]<>"“”‘’«»*%^&~`:;,.!?
            """.trim()

    private val ALL_WHITESPACE = "\u0009\u000A\u000B\u000C\u000D\u0020\u0085\u00A0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u2028\u2029\u202F\u205F\u3000"

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

    fun findWords(text: String, searchWords: List<String>, stemmingLanguage: StemmingLanguage? = null): List<TextMatch> {
        return if (stemmingLanguage != null) {
            val stemmer = stemmingLanguage.newStemmer()
            val wordsToSearch = stemmer.stem(searchWords)
            words(text).filter {
                stemmer.stem(it.text) in wordsToSearch
            }
        } else {
            val lowerCaseWords = searchWords.map { it.toLowerCase() }
            words(text).filter {
                lowerCaseWords.contains(it.text.toLowerCase())
            }
        }
    }

    fun findSequence(text: String, sequence: String, stemmingLanguage: StemmingLanguage? = null): List<TextMatch> {
        val textWords = words(text)
        val sequenceWords = words(sequence)

        val allMatches = mutableListOf<TextMatch>()
        val stemmer = stemmingLanguage?.newStemmer()
        for (i in 0..textWords.size - sequenceWords.size) {
            if (sameWord(textWords[i].text, sequenceWords[0].text, stemmer)) {
                var matchesSequence = true
                for (j in 1 until sequenceWords.size) {
                    if (!sameWord(textWords[i + j].text, sequenceWords[j].text, stemmer)) {
                        matchesSequence = false
                        break
                    }
                }
                if (matchesSequence) {
                    val startIndex = textWords[i].startIndex
                    val endIndex = textWords[i + sequenceWords.size - 1].endIndex
                    allMatches.add(TextMatch.extract(text, startIndex, endIndex))
                }
            }
        }
        return allMatches
    }

    internal fun sameWord(word1: String, word2: String, stemmer: SnowballStemmer? = null): Boolean {
        return if (stemmer != null) {
            stemmer.stem(word1) == stemmer.stem(word2)
        } else {
            word1.toLowerCase() == word2.toLowerCase()
        }
    }
}

data class WordCursor(val startIndex: Int) {

    private val text = StringBuilder()

    fun addChar(char: Char) {
        text.append(char)
    }

    fun word(): TextMatch {
        return TextMatch(text.toString(), startIndex)
    }
}