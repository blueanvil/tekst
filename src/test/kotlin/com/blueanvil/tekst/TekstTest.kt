package com.blueanvil.tekst

import org.testng.Assert.*
import org.testng.annotations.Test
import java.io.FileOutputStream
import kotlin.random.Random


/**
 * @author Cosmin Marginean
 */
class TekstTest {

    @Test
    fun words() {
        assertMatches("You want respect?", "You", "want", "respect")
        assertMatches("Go out and get it for yourself.", "Go", "out", "and", "get", "it", "for", "yourself")
        assertMatches("You’re going to need a stronger stomach if you’re going to be back in the kitchen seeing how the sausage is made.",
                "You", "re", "going", "to", "need", "a", "stronger", "stomach", "if", "you", "re",
                "going", "to", "be", "back", "in", "the", "kitchen", "seeing", "how", "the", "sausage", "is", "made")
    }

    @Test
    fun noMatches() {
        assertTrue(Tekst.find("Dissatisfaction is a symptom of ambition. It’s the coal that fuels the fire", "stardom", StemmingLanguage.ENGLISH).isEmpty())
        assertTrue(Tekst.find("Dissatisfaction is a symptom of ambition. It’s the coal that fuels the fire", listOf("stardom"), StemmingLanguage.ENGLISH)["stardom"]!!.isEmpty())
    }

    @Test
    fun testSeparators() {
        textSamples.forEach { textSample ->
            val testText = String(textSample.toCharArray().map {
                if (it == ' ')
                    Tekst.ALL_DELIMITERS[Random.nextInt(0, Tekst.ALL_DELIMITERS.length)]
                else it
            }.toCharArray())

            println("\n---Testing:\n$testText\n---")
            assertMatches(testText, Tekst.words(textSample))
        }
    }

    @Test
    fun findWords() {
        val text = "I'm glad this is an environment where you feel free to fail."

        assertSameMatches(Tekst.find(text, "fail"), listOf(TextMatch("fail", text.indexOf("fail"))))
        assertSameMatches(Tekst.find(text, "Failed", StemmingLanguage.ENGLISH), listOf(TextMatch("fail", text.indexOf("fail"))))
        assertSameMatches(Tekst.find(text, "faiLING", StemmingLanguage.ENGLISH), listOf(TextMatch("fail", text.indexOf("fail"))))
        assertSameMatches(Tekst.find(text, "eNvironment", StemmingLanguage.ENGLISH), listOf(TextMatch("environment", text.indexOf("environment"))))
        assertSameMatches(Tekst.find(text, "enVIRonments", StemmingLanguage.ENGLISH), listOf(TextMatch("environment", text.indexOf("environment"))))

        assertSameMatches(Tekst.find(text, "frEEing", StemmingLanguage.ENGLISH), listOf(TextMatch("free", text.indexOf("free"))))
        assertSameMatches(Tekst.find(text, "feeLS", StemmingLanguage.ENGLISH), listOf(TextMatch("feel", text.indexOf("feel"))))
    }

    @Test
    fun sameWord() {
        assertTrue(Tekst.sameWord("trust", "TRUST"))
        assertTrue(Tekst.sameWord("trust", "trUST"))

        assertFalse(Tekst.sameWord("trust", "trUSTing"))

        assertTrue(Tekst.sameWord("trust", "trUSTing", StemmingLanguage.ENGLISH.newStemmer()))
        assertTrue(Tekst.sameWord("game", "gaming", StemmingLanguage.ENGLISH.newStemmer()))
        assertTrue(Tekst.sameWord("games", "gaming", StemmingLanguage.ENGLISH.newStemmer()))
        assertTrue(Tekst.sameWord("and", "and", StemmingLanguage.ENGLISH.newStemmer()))
    }

    @Test
    fun findSequenceBasics() {
        assertEquals(
                listOf(TextMatch("cut down on your drinking", 29)),
                Tekst.find("They say as soon you have to cut down on your drinking, you have a drinking problem.", "cut DOWN on your drinking")
        )

        assertEquals(
                listOf(TextMatch("They say", 0)),
                Tekst.find("They say as soon you have to cut down on your drinking, you have a drinking problem.", "they Say")
        )

        assertEquals(
                listOf(TextMatch("you have a drinking problem", 56)),
                Tekst.find("They say as soon you have to cut down on your drinking, you have a drinking problem.", "you HavE a drinking PROBLEM")
        )

        assertEquals(
                TextMatch("They say", 0),
                Tekst.find("They say as soon you have to cut down on your drinking, you have a drinking problem.", listOf("they   say", "you HavE a drinking PROBLEM"))["they   say"]!![0]
        )
        assertEquals(
                TextMatch("you have a drinking problem", 56),
                Tekst.find("They say as soon you have to cut down on your drinking, you have a drinking problem.", listOf("they   say", "you HavE a drinking PROBLEM"))["you HavE a drinking PROBLEM"]!![0]
        )
    }

    @Test
    fun findSequenceStemming() {
        assertEquals(
                listOf(TextMatch("fun and games", 9)),
                Tekst.find("It’s all fun and games till they shoot you in the face.", "fun and gaming", StemmingLanguage.ENGLISH)
        )
    }

    /**
     * This is just a test to show case main capabilities
     */
    @Test
    fun showCase() {
        val textWords = "If your tantrum has subsided, you’re welcome to join us."
        println(Tekst.words(textWords))


        val text = "I thought it would look petty and vindictive not to," +
                " and as a petty and vindictive individual I have to" +
                " take extra care not to appear petty or vindictive."
        Tekst.find(text, listOf("Petty", "Vindictive Individuals"), StemmingLanguage.ENGLISH)
                .forEach { searchTerm, matches ->
                    println("Matches for '$searchTerm':")
                    matches.forEach { println("\t" + it) }
                }


        val textToHighlight = "“I’m not a serpent!” said Alice indignantly. “Let me alone!” “Serpent, I say again!” repeated the Pigeon, but in a more subdued tone, and added with a kind of sob, “I’ve tried every way, and nothing seems to suit them!” “I haven’t the least idea what you’re talking about,” said Alice. “I’ve tried the roots of trees, and I’ve tried banks, and I’ve tried hedges,” the Pigeon went on, without attending to her; “but those serpents! There’s no pleasing them!” Alice was more and more puzzled, but she thought there was no use in saying anything more till the Pigeon had finished."
        Tekst.highlightHtml(textToHighlight, listOf("try", "talk", "serpent said"), FileOutputStream("output.html"), StemmingLanguage.ENGLISH)


        val txt = "I'm glad this is an environment where you feel free to fail."
        val envMatches = Tekst.find(txt, "environment", StemmingLanguage.ENGLISH)
        println(envMatches)
        println(TextMatch.invertMatches(txt, envMatches))
    }

    // Do not use this with stemming
    private fun assertMatches(text: String, vararg textWords: String) {
        val matches = mutableListOf<TextMatch>()
        textWords.forEachIndexed { index, it ->
            val startIndex = if (index == 0) 0 else matches[index - 1].endIndex
            matches.add(TextMatch(it, text.indexOf(it, startIndex)))
        }
        assertMatches(text, matches)
    }

    private fun assertMatches(text: String, textMatches: Collection<TextMatch>) {
        assertSameMatches(Tekst.words(text), textMatches)
    }

    private fun assertSameMatches(actual: Collection<TextMatch>, expected: Collection<TextMatch>) {
        assertTrue(actual == expected, "Different lists:\nExpected: $expected\nActual  : $actual")
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