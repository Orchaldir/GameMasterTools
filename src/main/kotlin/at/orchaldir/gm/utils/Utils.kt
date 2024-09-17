package at.orchaldir.gm.utils

fun doNothing() {
    // do nothing
}

fun String.titlecaseFirstChar() = replaceFirstChar(Char::titlecase)

fun <T> List<T>.update(index: Int, item: T): List<T> = toMutableList().apply { this[index] = item }