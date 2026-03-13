package at.orchaldir.gm.core.selector.culture

import at.orchaldir.gm.core.model.character.FamilyName
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.character.title.AbstractTitle
import at.orchaldir.gm.core.model.character.title.NoTitle
import at.orchaldir.gm.core.model.culture.name.*
import at.orchaldir.gm.core.model.util.name.Name

fun getFamilyName(
    nameOrder: NameOrder,
    name: FamilyName,
    gender: Gender,
    title: AbstractTitle = NoTitle,
) = when (nameOrder) {
    NameOrder.GivenNameFirst -> getDefaultFamilyName(name, gender, title)
    NameOrder.FamilyNameFirst -> getFamilyName(
        title.resolveFamilyName(name.family.text, gender),
        name.middle,
        name.given.text
    )
}

fun getDefaultFamilyName(
    name: FamilyName,
    gender: Gender,
    title: AbstractTitle,
): String = getFamilyName(
    name.given.text,
    name.middle,
    title.resolveFamilyName(name.family.text, gender)
)

private fun getFamilyName(first: String, middle: Name?, last: String) = if (middle != null) {
    "$first ${middle.text} $last"
} else {
    "$first $last"
}

fun NamingConvention.isAnyGenonym() = when (this) {
    is GenonymConvention -> true
    is MatronymConvention -> true
    is PatronymConvention -> true
    else -> false
}
