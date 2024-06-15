package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.appearance.GenderMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GenonymicLookupDistance {
    OneGeneration,
    TwoGenerations,
}

@Serializable
sealed class GenonymicStyle

@Serializable
@SerialName("NamesOnly")
data object NamesOnlyStyle : GenonymicStyle()

@Serializable
@SerialName("Prefix")
data class PrefixStyle(
    val prefix: GenderMap<String>,
) : GenonymicStyle()

@Serializable
@SerialName("Suffix")
data class SuffixStyle(
    val suffix: GenderMap<String>,
) : GenonymicStyle()

@Serializable
@SerialName("ChildOf")
data class ChildOfStyle(
    val words: GenderMap<String>,
) : GenonymicStyle()