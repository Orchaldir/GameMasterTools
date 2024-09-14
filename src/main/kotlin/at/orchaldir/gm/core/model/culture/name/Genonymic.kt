package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.util.GenderMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * How many generations of ancestors are used in the name?
 */
@Serializable
enum class GenonymicLookupDistance {
    OneGeneration,
    TwoGenerations,
}

enum class GenonymicStyleType {
    NamesOnly,
    Prefix,
    Suffix,
    ChildOf,
}

@Serializable
sealed class GenonymicStyle

/**
 * "A B" means that A is son of B.
 */
@Serializable
@SerialName("NamesOnly")
data object NamesOnlyStyle : GenonymicStyle()

/**
 * "A {prefix}B" means that A is child of B.
 */
@Serializable
@SerialName("Prefix")
data class PrefixStyle(
    val prefix: GenderMap<String>,
) : GenonymicStyle()

/**
 * "A B{suffix}" means that A is child of B.
 *
 * E.g. Thor Odinson
 */
@Serializable
@SerialName("Suffix")
data class SuffixStyle(
    val suffix: GenderMap<String>,
) : GenonymicStyle()

/**
 * "A {words} B" means that A is child of B.
 */
@Serializable
@SerialName("ChildOf")
data class ChildOfStyle(
    val words: GenderMap<String>,
) : GenonymicStyle()