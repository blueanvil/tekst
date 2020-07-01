# Tekst

Tekst is a Kotlin library for basic text processing and parsing.

TODO: How to get it.


## Split text into words
```kotlin
val textWords = "If your tantrum has subsided, you’re welcome to join us."
println(Tekst.words(textWords))
```
##### Output
```
[TextMatch(text=If, startIndex=0), TextMatch(text=your, startIndex=3), TextMatch(text=tantrum, startIndex=8), TextMatch(text=has, startIndex=16), TextMatch(text=subsided, startIndex=20), TextMatch(text=you, startIndex=30), TextMatch(text=re, startIndex=34), TextMatch(text=welcome, startIndex=37), TextMatch(text=to, startIndex=45), TextMatch(text=join, startIndex=48), TextMatch(text=us, startIndex=53)]
```

## Find occurrences of a string in a text
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
##### Output
```
Matches for 'Petty':
	TextMatch(text=petty, startIndex=24)
	TextMatch(text=petty, startIndex=62)
	TextMatch(text=petty, startIndex=134)
Matches for 'Vindictive Individuals':
	TextMatch(text=vindictive individual, startIndex=72)
```
