package com.gmail.maystruks08.domain


fun String.isolateSpecialSymbolsForRegex(): String =
    this.replace("*", "\\*")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace("[", "\\[")
        .replace("]", "\\]")

