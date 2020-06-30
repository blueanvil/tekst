package com.blueanvil.tekst

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * @author Cosmin Marginean
 */
class StemmingTest {

    @Test
    fun testLangs() {
        assertEquals("fail", StemmingLanguage.ENGLISH.newStemmer().stem("faILED"))
        assertEquals("recapitul", StemmingLanguage.ROMANIAN.newStemmer().stem("rEcaPitula"))
    }
}