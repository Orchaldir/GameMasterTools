package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.appearance.RarityMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NamingConvention

@Serializable
@SerialName("Mononym")
data class MononymConvention(val name: String) : NamingConvention()

@Serializable
@SerialName("Family")
data class FamilyConvention(
    val nameOrder: NameOrder,
    val middleNameOptions: RarityMap<MiddleNameOption>,
) : NamingConvention()


@Serializable
@SerialName("Patronym")
data class PatronymConvention(
    val lookupDistance: GenonymicLookupDistance,
    val style: GenonymicStyle,
) : NamingConvention()

@Serializable
@SerialName("Matronym")
data class MatronymConvention(
    val lookupDistance: GenonymicLookupDistance,
    val style: GenonymicStyle,
) : NamingConvention()

/**
 * Patronym or Matronym based on the own gender.
 */
@Serializable
@SerialName("Genonym")
data class GenonymConvention(
    val lookupDistance: GenonymicLookupDistance,
    val style: GenonymicStyle,
) : NamingConvention()
