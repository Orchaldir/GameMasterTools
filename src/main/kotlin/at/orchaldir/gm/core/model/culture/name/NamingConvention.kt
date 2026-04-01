package at.orchaldir.gm.core.model.culture.name

import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.title.AbstractTitle
import at.orchaldir.gm.core.model.character.title.NoTitle
import at.orchaldir.gm.core.model.culture.name.GenonymicLookupDistance.OneGeneration
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.selector.character.name.getDefaultFamilyName
import at.orchaldir.gm.core.selector.character.name.getFamilyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class NamingConventionType {
    None,
    Mononym,
    Random,
    Family,
    Patronym,
    Matronym,
    Genonym,
}

@Serializable
sealed class NamingConvention {

    abstract fun contains(id: NameListId): Boolean

    abstract fun getNameLists(): Set<NameListId>

    fun getFamilyName(name: FamilyName, gender: Gender, title: AbstractTitle = NoTitle): String =
        when (this) {
            is FamilyConvention -> getFamilyName(nameOrder, name, gender, title)
            is RandomGivenAndLastName -> getDefaultFamilyName(name, gender, title)

            else -> error("A family name requires a family convention!")
        }

    fun getType() = when (this) {
        is FamilyConvention -> NamingConventionType.Family
        is GenonymConvention -> NamingConventionType.Genonym
        is MatronymConvention -> NamingConventionType.Matronym
        is MononymConvention -> NamingConventionType.Mononym
        NoNamingConvention -> NamingConventionType.None
        is PatronymConvention -> NamingConventionType.Patronym
        is RandomGivenAndLastName -> NamingConventionType.Random
    }
}

@Serializable
@SerialName("None")
data object NoNamingConvention : NamingConvention() {

    override fun contains(id: NameListId) = false

    override fun getNameLists() = setOf<NameListId>()
}

@Serializable
@SerialName("Mononym")
data class MononymConvention(
    val names: GivenNames,
) : NamingConvention() {
    constructor(id: NameListId) : this(NonGenderedGivenNames(id))

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getNameLists()
}

@Serializable
@SerialName("Random")
data class RandomGivenAndLastName(
    val givenNames: GivenNames,
    val lastNames: NameListId,
    val middleNameOptions: OneOf<MiddleNameOption> = OneOf(MiddleNameOption.entries),
) : NamingConvention() {

    override fun contains(id: NameListId) = givenNames.contains(id) || lastNames == id

    override fun getNameLists() = givenNames.getNameLists() + setOf(lastNames)
}

@Serializable
@SerialName("Family")
data class FamilyConvention(
    val givenNames: GivenNames,
    val familyNames: NameListId,
    val nameOrder: NameOrder = NameOrder.GivenNameFirst,
    val middleNameOptions: OneOf<MiddleNameOption> = OneOf(MiddleNameOption.entries),
) : NamingConvention() {

    override fun contains(id: NameListId) = givenNames.contains(id) || familyNames == id

    override fun getNameLists() = givenNames.getNameLists() + setOf(familyNames)
}


@Serializable
@SerialName("Patronym")
data class PatronymConvention(
    val names: GivenNames,
    val lookupDistance: GenonymicLookupDistance = OneGeneration,
    val style: GenonymicStyle = NamesOnlyStyle,
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getNameLists()
}

@Serializable
@SerialName("Matronym")
data class MatronymConvention(
    val names: GivenNames,
    val lookupDistance: GenonymicLookupDistance = OneGeneration,
    val style: GenonymicStyle = NamesOnlyStyle,
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getNameLists()
}

/**
 * Patronym or Matronym based on the own gender.
 */
@Serializable
@SerialName("Genonym")
data class GenonymConvention(
    val names: GivenNames,
    val lookupDistance: GenonymicLookupDistance = OneGeneration,
    val style: GenonymicStyle = NamesOnlyStyle,
) : NamingConvention() {

    override fun contains(id: NameListId) = names.contains(id)

    override fun getNameLists() = names.getNameLists()
}

fun NamingConvention.isAnyGenonym() = when (this) {
    is GenonymConvention -> true
    is MatronymConvention -> true
    is PatronymConvention -> true
    else -> false
}
