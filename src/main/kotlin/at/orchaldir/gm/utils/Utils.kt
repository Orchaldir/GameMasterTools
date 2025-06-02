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

fun <T, U> List<T>.combine(other: List<U>): List<Pair<T, U>> {
    val newList: MutableList<Pair<T, U>> = mutableListOf()

    this.forEach { a ->
        other.forEach { b ->
            newList.add(Pair(a, b))
        }
    }

    return newList
}

fun Boolean.toInt() = when (this) {
    true -> 1
    false -> 0
}

fun Boolean.convert(trueValue: Int, falseValue: Int): Int = if (this) {
    trueValue
} else {
    falseValue
}

fun Int.isEven() = this % 2 == 0