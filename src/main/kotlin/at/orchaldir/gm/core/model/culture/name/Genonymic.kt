package at.orchaldir.gm.core.model.culture.name

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
    val male: String,
    val genderless: String,
    val female: String,
) : GenonymicStyle()

@Serializable
@SerialName("Suffix")
data class SuffixStyle(
    val male: String,
    val genderless: String,
    val female: String,
) : GenonymicStyle()

@Serializable
@SerialName("Suffix")
data class ChildOfStyle(
    val male: String,
    val genderless: String,
    val female: String,
) : GenonymicStyle()