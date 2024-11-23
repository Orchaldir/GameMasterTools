package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.culture.name.GenonymicLookupDistance.OneGeneration
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.OneOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    fun getFamilyName(name: FamilyName): String {
        when (this) {
            is FamilyConvention -> return when (nameOrder) {
                NameOrder.GivenNameFirst -> getFamilyName(
                    name.given,
                    name.middle,
                    name.family
                )

                NameOrder.FamilyNameFirst -> getFamilyName(
                    name.family,
                    name.middle,
                    name.given
                )
            }

            else -> error("A family name requires a family convention!")
        }
    }
}

private fun getFamilyName(first: String, middle: String?, last: String) = if (middle != null) {
    "$first $middle $last"
} else {
    "$first $last"
}

@Serializable
@SerialName("None")
data object NoNamingConvention : NamingConvention() {

    override fun contains(id: NameListId) = false

    override fun getNameLists() = setOf<NameListId>()
}

@Serializable
@SerialName("Mononym")
data class MononymConvention(val names: GenderMap<NameListId> = GenderMap(NameListId(0))) : NamingConvention() {
    constructor(id: NameListId) : this(GenderMap(id))

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}

@Serializable
@SerialName("Family")
data class FamilyConvention(
    val nameOrder: NameOrder = NameOrder.GivenNameFirst,
    val middleNameOptions: OneOf<MiddleNameOption> = OneOf(MiddleNameOption.entries),
    val givenNames: GenderMap<NameListId> = GenderMap(NameListId(0)),
    val familyNames: NameListId = NameListId(0),
) : NamingConvention() {

    override fun contains(id: NameListId) = givenNames.contains(id) || familyNames == id

    override fun getNameLists() = givenNames.getValues() + setOf(familyNames)
}


@Serializable
@SerialName("Patronym")
data class PatronymConvention(
    val lookupDistance: GenonymicLookupDistance = OneGeneration,
    val style: GenonymicStyle = NamesOnlyStyle,
    val names: GenderMap<NameListId> = GenderMap(NameListId(0)),
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}

@Serializable
@SerialName("Matronym")
data class MatronymConvention(
    val lookupDistance: GenonymicLookupDistance = OneGeneration,
    val style: GenonymicStyle = NamesOnlyStyle,
    val names: GenderMap<NameListId> = GenderMap(NameListId(0)),
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
    val lookupDistance: GenonymicLookupDistance = OneGeneration,
    val style: GenonymicStyle = NamesOnlyStyle,
    val names: GenderMap<NameListId> = GenderMap(NameListId(0)),
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getValues()
}

fun NamingConvention.isAnyGenonym() = when (this) {
    is GenonymConvention -> true
    is MatronymConvention -> true
    is PatronymConvention -> true
    else -> false
}
