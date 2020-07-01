package com.blueanvil.tekst

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * @author Cosmin Marginean
 */
class TextMatchTest {

    @Test
    fun inverse() {
        val text = "I know you’re all feeling the darkness here today. " +
                "But there’s no reason to give in. " +
                "No matter what you’ve heard, this process will not take years. " +
                "In my heart, I know we cannot be defeated, because there is an answer that will open the door." +
                "There is a way around this system. This is a test of our patience and commitment."

        val matches = mutableListOf<TextMatch>()
        matches.addAll(Tekst.find(text, "in"))
        matches.addAll(Tekst.find(text, "no"))
        matches.addAll(Tekst.find(text, "a"))
        matches.addAll(Tekst.find(text, "there"))
        matches.addAll(Tekst.find(text, "will"))
        matches.addAll(Tekst.find(text, "cannot"))
        matches.addAll(Tekst.find(text, "be"))

        matches.sort()

        val inverses = TextMatch.invertMatches(text, matches)
        matches.addAll(inverses)
        matches.sort()

        // The best test is to check that the matches + the inverses combined produce the complete text when assembled.
        assertEquals(matches.joinToString("") { it.text }, text)
    }
}