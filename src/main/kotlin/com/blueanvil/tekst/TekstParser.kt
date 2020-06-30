package com.blueanvil.tekst

import org.tartarus.snowball.SnowballStemmer
import org.tartarus.snowball.ext.englishStemmer

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

    fun parse(text: String): List<Word> {
        val words = mutableListOf<Word>()
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

    fun findWords(text: String, searchWords: Collection<String>): List<Word> {
        val stemmer: SnowballStemmer = englishStemmer()
        val stemmedSearchWords = searchWords.map {
            stemmer.current = it
            stemmer.stem()
            stemmer.current
        }

        return parse(text).filter {
            stemmer.current = it.text
            stemmer.stem()
            stemmedSearchWords.contains(stemmer.current)
        }
    }
}

data class WordCursor(val startIndex: Int) {

    private val text = StringBuilder()

    fun addChar(char: Char) {
        text.append(char)
    }

    fun word(): Word {
        return Word(text.toString(), startIndex)
    }
}