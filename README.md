# Tekst
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.com/blueanvil/tekst.svg?branch=master)](https://travis-ci.com/blueanvil/tekst)
[![Coverage Status](https://coveralls.io/repos/github/blueanvil/tekst/badge.svg)](https://coveralls.io/github/blueanvil/tekst)

Tekst is a Kotlin library for basic text processing and parsing.

# Gradle
```
repositories {
    maven { url 'https://jitpack.io' }
}
```
```
dependencies {
    implementation 'com.github.blueanvil:tekst:0.9.0'
}
```

# Usage
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

## Invert matches
This can be useful if you want to extract the text "around" the matches, or text that didn't match the search string.
```kotlin
val txt = "I'm glad this is an environment where you feel free to fail."
val envMatches = Tekst.find(txt, "environment", StemmingLanguage.ENGLISH)
println(envMatches)
println(TextMatch.invertMatches(txt, envMatches))
```
##### Output
```
[TextMatch(text=environment, startIndex=20)]
[TextMatch(text=I'm glad this is an , startIndex=0), TextMatch(text= where you feel free to fail., startIndex=31)]
```

## Highlighting demo
This uses the output of a `find()` method call to produce a sample HTML with the matches for each of the searched strings.
```kotlin
val textToHighlight = "“I’m not a serpent!” said Alice indignantly. “Let me alone!” “Serpent, I say again!” repeated the Pigeon, but in a more subdued tone, and added with a kind of sob, “I’ve tried every way, and nothing seems to suit them!” “I haven’t the least idea what you’re talking about,” said Alice. “I’ve tried the roots of trees, and I’ve tried banks, and I’ve tried hedges,” the Pigeon went on, without attending to her; “but those serpents! There’s no pleasing them!” Alice was more and more puzzled, but she thought there was no use in saying anything more till the Pigeon had finished."
Tekst.highlightHtml(textToHighlight, listOf("try", "talk", "serpent said"), FileOutputStream("output.html"), StemmingLanguage.ENGLISH)
```
##### Output
![alt text](https://github.com/blueanvil/tekst/blob/master/etc/docs/highlight-demo.png)


# License Information
* The code is licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
* The project uses code from the [Snowball](https://snowballstem.org/) project, licensed as derscribed [here](https://snowballstem.org/license.html). The same copy right notice is included in the root package of the source code: https://raw.githubusercontent.com/blueanvil/tekst/master/src/main/resources/snowball-copyright-notice.txt
