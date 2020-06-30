package com.blueanvil.tekst

import org.tartarus.snowball.SnowballStemmer

/**
 * @author Cosmin Marginean
 */
enum class StemmingLanguage {

    ARABIC,
    BASQUE,
    CATALAN,
    DANISH,
    DUTCH,
    ENGLISH,
    FINNISH,
    FRENCH,
    GERMAN,
    GREEK,
    HINDI,
    HUNGARIAN,
    INDONESIAN,
    IRISH,
    ITALIAN,
    LITHUANIAN,
    NEPALI,
    NORWEGIAN,
    PORTUGUESE,
    ROMANIAN,
    RUSSIAN,
    SPANISH,
    SWEDISH,
    TAMIL,
    TURKISH;

    // Do lookup early so we save some time on that, but don't created the instance as it's not re-usable and thread-safe anyway
    private val stemmerClass = Class.forName("org.tartarus.snowball.ext.${name.toLowerCase()}Stemmer")

    fun newStemmer() = stemmerClass.newInstance() as SnowballStemmer
}

fun SnowballStemmer.stem(word: String): String {
    current = word.toLowerCase()
    stem()
    return current
}

fun SnowballStemmer.stem(words: List<String>): List<String> {
    return words.map { stem(it) }
}


