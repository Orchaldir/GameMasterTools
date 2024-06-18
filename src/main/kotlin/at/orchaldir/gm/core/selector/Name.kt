package at.orchaldir.gm.core.selector

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.culture.name.*


fun State.getName(character: CharacterId) = getName(characters.getOrThrow(character))

fun State.getName(character: Character): String {
    val name = character.name
    val culture = cultures.getOrThrow(character.culture)

    return when (name) {
        is FamilyName -> getFamilyName(culture.namingConvention, name)
        is Genonym -> when (culture.namingConvention) {
            is GenonymConvention -> TODO()
            is MatronymConvention -> TODO()
            is PatronymConvention -> {
                val fatherId = getFather(character)

                return if (fatherId != null) {
                    val father = characters.getOrThrow(fatherId)
                    getGenonymName(name.given, culture.namingConvention.style, father)
                } else {
                    name.given
                }
            }

            else -> error("A genonym requires a genonym convention!")
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

private fun getGenonymName(first: String, style: GenonymicStyle, parent: Character): String {
    val parentGiven = getGivenName(parent)

    return when (style) {
        is ChildOfStyle -> "$first ${style.words} $parentGiven"
        NamesOnlyStyle -> "$first $parentGiven"
        is PrefixStyle -> "$first ${style.prefix}$parentGiven"
        is SuffixStyle -> "$first $parentGiven${style.suffix}"
    }
}

private fun getGivenName(character: Character) = when (character.name) {
    is FamilyName -> character.name.given
    is Genonym -> character.name.given
    is Mononym -> character.name.name
}
