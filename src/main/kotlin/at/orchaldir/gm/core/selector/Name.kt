package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.appearance.GenderMap
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.GenonymicLookupDistance.TwoGenerations


fun State.getName(character: CharacterId) = getName(characters.getOrThrow(character))

fun State.getName(character: Character): String {
    return when (val name = character.name) {
        is FamilyName -> {
            val culture = cultures.getOrThrow(character.culture)

            getFamilyName(culture.namingConvention, name)
        }

        is Genonym -> {
            val culture = cultures.getOrThrow(character.culture)
            when (culture.namingConvention) {
                is GenonymConvention -> TODO()
                is MatronymConvention -> TODO()
                is PatronymConvention -> {
                    val fatherId = getFather(character)

                    return if (fatherId != null) {
                        val father = characters.getOrThrow(fatherId)
                        val result =
                            getGenonymName(name.given, character.gender, culture.namingConvention.style, father)

                        if (culture.namingConvention.lookupDistance == TwoGenerations) {
                            val grandfatherId = getFather(father)

                            if (grandfatherId != null) {
                                val grandfather = characters.getOrThrow(grandfatherId)
                                return getGenonymName(
                                    result,
                                    father.gender,
                                    culture.namingConvention.style,
                                    grandfather
                                )
                            }
                        }

                        return result

                    } else {
                        name.given
                    }
                }

                else -> error("A genonym requires a genonym convention!")
            }
        }
        is Mononym -> name.name
    }
}

private fun getFamilyName(
    namingConvention: NamingConvention,
    name: FamilyName,
): String {
    when (namingConvention) {
        is FamilyConvention -> return when (namingConvention.nameOrder) {
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

private fun getFamilyName(first: String, middle: String?, last: String) = if (middle != null) {
    "$first $middle $last}"
} else {
    "$first $last}"
}

private fun getGenonymName(first: String, gender: Gender, style: GenonymicStyle, parent: Character): String {
    val parentGiven = getGivenName(parent)

    return when (style) {
        is ChildOfStyle -> getGenonymName("$first %s $parentGiven", style.words, gender)
        NamesOnlyStyle -> "$first $parentGiven"
        is PrefixStyle -> getGenonymName("$first %s$parentGiven", style.prefix, gender)
        is SuffixStyle -> getGenonymName("$first $parentGiven%s", style.suffix, gender)
    }
}

private fun getGenonymName(format: String, map: GenderMap<String>, gender: Gender): String {
    val insert = map.get(gender)

    return String.format(format, insert)
}

private fun getGivenName(character: Character) = when (character.name) {
    is FamilyName -> character.name.given
    is Genonym -> character.name.given
    is Mononym -> character.name.name
}
