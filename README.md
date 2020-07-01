# Tekst

Tekst is a Kotlin library for basic text processing and parsing.

TODO: How to get it.

### Find occurrences of a string in a text
```kotlin
val text = "I thought it would look petty and vindictive not to," +
        " and as a petty and vindictive individual I have to" +
        " take extra care not to appear petty or vindictive."

Tekst.find(text, listOf("Petty", "Vindictive Individuals"), StemmingLanguage.ENGLISH)
        .forEach { searchTerm, matches ->
            println("Matches for '$searchTerm':")
            matches.forEach { println("\t" + it) }
        }
```
Output:
```
Matches for 'Petty':
	TextMatch(text=petty, startIndex=24)
	TextMatch(text=petty, startIndex=62)
	TextMatch(text=petty, startIndex=134)
Matches for 'Vindictive Individuals':
	TextMatch(text=vindictive individual, startIndex=72)
```
