package at.orchaldir.gm.utils

fun doNothing() {
    // do nothing
}

fun String.titlecaseFirstChar() = replaceFirstChar(Char::titlecase)