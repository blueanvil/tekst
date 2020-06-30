package com.blueanvil.tekst

import org.testng.Assert.assertTrue
import org.testng.annotations.Test
import kotlin.random.Random

/**
 * @author Cosmin Marginean
 */
class TekstParserTest {

    @Test
    fun simpleParse() {
        assertIsWords("You want respect?", "You", "want", "respect")
        assertIsWords("Go out and get it for yourself.", "Go", "out", "and", "get", "it", "for", "yourself")
        assertIsWords("You’re going to need a stronger stomach if you’re going to be back in the kitchen seeing how the sausage is made.",
                "You", "re", "going", "to", "need", "a", "stronger", "stomach", "if", "you", "re",
                "going", "to", "be", "back", "in", "the", "kitchen", "seeing", "how", "the", "sausage", "is", "made")
    }

    @Test(dependsOnMethods = ["simpleParse"])
    fun testSeparators() {
        textSamples.forEach { textSample ->
            val testText = String(textSample.toCharArray().map {
                if (it == ' ')
                    TekstParser.ALL_DELIMITERS[Random.nextInt(0, TekstParser.ALL_DELIMITERS.length)]
                else it
            }.toCharArray())

            println("\n---Testing:\n$testText\n---")
            assertIsWords(testText, TekstParser.parse(textSample))
        }
    }

    fun assertIsWords(text: String, vararg textWords: String) {
        val words = mutableListOf<Word>()
        textWords.forEachIndexed { index, it ->
            val startIndex = if (index == 0) 0 else words[index - 1].endIndex
            words.add(Word(it, text.indexOf(it, startIndex)))
        }
        assertIsWords(text, words)
    }

    fun assertIsWords(text: String, words: Collection<Word>) {
        val parsedWords = TekstParser.parse(text)
        assertTrue(parsedWords == words, "Different lists:\nExpected: $words\nActual  : $parsedWords")
    }

    companion object {
        private val textSamples = listOf(
                "I'm glad this is an environment where you feel free to fail.",
                "Am I to entertain your ballad of dissatisfaction, or has something actually happened? Because I am at work, dear.",
                "If your tantrum has subsided, you’re welcome to join us.",
                "They say as soon you have to cut down on your drinking, you have a drinking problem.",
                "Dissatisfaction is a symptom of ambition. It’s the coal that fuels the fire.",
                "Men don’t take time to end things. They ignore you until you insist on a declaration of hate.",
                "But that’s life. One minute you’re on top of the world, the next minute some secretary’s running you over with a lawn mower.",
                "I keep going to a lot of places and ending up somewhere I’ve already been",
                "That poor girl. She doesn’t know that loving you is the worst way to get to you.",
                "Well, you know what they say about Detroit. It’s all fun and games till they shoot you in the face.",
                "Look, I want to tell you something because you’re very dear to me. And I hope you understand that it comes from the bottom of my damaged, damaged heart. You are the finest piece of ass I’ve ever had and I don’t care who knows it. I am so glad that I got to roam those hillsides",
                "You're born alone and you die alone and this world just drops a bunch of rules on top of you to make you forget those facts. But I never forget. I'm living like there's no tomorrow, because there isn't one.",
                "I know you’re all feeling the darkness here today. But there’s no reason to give in. No matter what you’ve heard, this process will not take years. In my heart, I know we cannot be defeated, because there is an answer that will open the door. There is a way around this system. This is a test of our patience and commitment."
        )
    }
}