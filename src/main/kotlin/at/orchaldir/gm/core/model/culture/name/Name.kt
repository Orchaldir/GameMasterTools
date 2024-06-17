package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.NameListId
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.appearance.RarityMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Collections

enum class NamingConventionType {
    None,
    Mononym,
    Family,
    Patronym,
    Matronym,
    Genonym,
}

@Serializable
sealed class NamingConvention {

    abstract fun contains(id: NameListId): Boolean

    abstract fun getNameLists(): Set<NameListId>

}

@Serializable
@SerialName("None")
data object NoNamingConvention : NamingConvention() {

    override fun contains(id: NameListId) = false

    override fun getNameLists() = setOf<NameListId>()
}

@Serializable
@SerialName("Mononym")
data class MononymConvention(val names: GenderMap<NameListId>) : NamingConvention() {
    constructor(id: NameListId) : this(GenderMap(id))

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}

@Serializable
@SerialName("Family")
data class FamilyConvention(
    val nameOrder: NameOrder,
    val middleNameOptions: RarityMap<MiddleNameOption>,
    val givenNames: GenderMap<NameListId>,
    val familyNames: GenderMap<NameListId>,
) : NamingConvention() {

    override fun contains(id: NameListId) = givenNames.contains(id) || familyNames.contains(id)

    override fun getNameLists() = givenNames.getValues() + familyNames.getValues()
}


@Serializable
@SerialName("Patronym")
data class PatronymConvention(
    val lookupDistance: GenonymicLookupDistance,
    val style: GenonymicStyle,
    val names: GenderMap<NameListId>,
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}

@Serializable
@SerialName("Matronym")
data class MatronymConvention(
    val lookupDistance: GenonymicLookupDistance,
    val style: GenonymicStyle,
    val names: GenderMap<NameListId>,
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}

/**
 * Patronym or Matronym based on the own gender.
 */
@Serializable
@SerialName("Genonym")
data class GenonymConvention(
    val lookupDistance: GenonymicLookupDistance,
    val style: GenonymicStyle,
    val names: GenderMap<NameListId>,
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}
