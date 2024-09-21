package at.orchaldir.gm.utils

fun doNothing() {
    // do nothing
}

fun String.titlecaseFirstChar() = replaceFirstChar(Char::titlecase)

fun <T> List<T>.update(index: Int, item: T): List<T> = toMutableList().apply { this[index] = item }


fun <T> List<T>.update(items: Map<Int, T>): List<T> {
    val newList = toMutableList()

    items.forEach { (index, item) -> newList[index] = item }

    return newList
}