package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.culture.name.GenonymicLookupDistance.TwoGenerations
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.selector.character.getFather
import at.orchaldir.gm.core.selector.character.getMother

fun State.canHaveFamilyName(character: Character): Boolean {
    val culture = getCultureStorage().getOrThrow(character.culture)

    return culture.namingConvention is FamilyConvention
}

fun State.canHaveGenonym(character: Character): Boolean {
    val convention = getCultureStorage().getOrThrow(character.culture).namingConvention

    return convention is GenonymConvention || convention is PatronymConvention || convention is MatronymConvention
}

fun State.getGenonymName(
    character: Character,
    name: Genonym,
): String {
    val culture = getCultureStorage().getOrThrow(character.culture)

    return when (val convention = culture.namingConvention) {
        is GenonymConvention -> getGenonymName(
            character,
            name,
            convention.lookupDistance,
            convention.style,
            Character::getParentForGenonym
        )

        is MatronymConvention -> getGenonymName(
            character,
            name,
            convention.lookupDistance,
            convention.style,
            Character::getMother
        )

        is PatronymConvention -> getGenonymName(
            character,
            name,
            convention.lookupDistance,
            convention.style,
            Character::getFather
        )

        else -> error("A genonym requires a genonym convention!")
    }
}

fun Character.getParentForGenonym() = when (origin) {
    is Born -> when (gender) {
        Gender.Female -> origin.mother
        else -> origin.father
    }

    UndefinedCharacterOrigin -> null
}

private fun State.getGenonymName(
    character: Character,
    name: Genonym,
    lookupDistance: GenonymicLookupDistance,
    style: GenonymicStyle,
    getParent: (Character) -> CharacterId?,
): String {
    val parentId = getParent(character)

    return if (parentId != null) {
        val parent = getCharacterStorage().getOrThrow(parentId)
        val result =
            getGenonymName(name.given.text, character.gender, style, parent)

        if (lookupDistance == TwoGenerations) {
            val grandparentId = getParent(parent)

            if (grandparentId != null) {
                val grandparent = getCharacterStorage().getOrThrow(grandparentId)
                return getGenonymName(
                    result,
                    parent.gender,
                    style,
                    grandparent
                )
            }
        }

        return result

    } else {
        name.given.text
    }
}

private fun getGenonymName(first: String, gender: Gender, style: GenonymicStyle, parent: Character): String {
    val parentGiven = parent.getGivenName().text

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

fun Character.getGivenName() = when (name) {
    is FamilyName -> name.given
    is Genonym -> name.given
    is Mononym -> name.name
}
