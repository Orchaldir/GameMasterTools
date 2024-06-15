package at.orchaldir.gm.core.model.culture.name

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NamingConvention

@Serializable
@SerialName("Mononym")
data class MononymConvention(val name: String) : NamingConvention()

@Serializable
enum class NameOrder {
    GivenNameFirst,
    FamilyNameFirst,
}

@Serializable
@SerialName("Family")
data class FamilyConvention(
    val nameOrder: NameOrder,
) : NamingConvention()

@Serializable
enum class LookupDistance {
    OneGeneration,
    TwoGenerations,
}


@Serializable
@SerialName("Patronym")
data class PatronymConvention(
    val lookupDistance: LookupDistance,
) : NamingConvention()

@Serializable
@SerialName("Matronym")
data class MatronymConvention(
    val lookupDistance: LookupDistance,
) : NamingConvention()
