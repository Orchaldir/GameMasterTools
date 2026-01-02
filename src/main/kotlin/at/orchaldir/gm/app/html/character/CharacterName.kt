package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.selector.util.canHaveFamilyName
import at.orchaldir.gm.core.selector.util.canHaveGenonym
import at.orchaldir.gm.core.selector.util.getGivenName
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag

// edit

fun HtmlBlockTag.selectCharacterName(
    state: State,
    character: Character,
) {
    showDetails("Name", true) {
        selectValue(
            "Type",
            combine(NAME, TYPE),
            CharacterNameType.entries,
            character.name.getType(),
        ) { type ->
            when (type) {
                CharacterNameType.Family -> !state.canHaveFamilyName(character)
                CharacterNameType.Genonym -> !state.canHaveGenonym(character)
                CharacterNameType.Mononym -> false
            }
        }
        selectName("Given Name", character.getGivenName(), GIVEN_NAME)

        if (character.name is FamilyName) {
            selectOptionalName("Middle Name", character.name.middle, MIDDLE_NAME)
            selectName("Family Name", character.name.family, FAMILY_NAME)
        }
    }
}

// parse

fun parseCharacterName(parameters: Parameters): CharacterName {
    val given = parseName(parameters, GIVEN_NAME)

    return when (parse(parameters, combine(NAME, TYPE), CharacterNameType.Mononym)) {
        CharacterNameType.Family -> FamilyName(
            given,
            parseOptionalName(parameters, MIDDLE_NAME),
            parseName(parameters, FAMILY_NAME, "Unknown"),
        )

        CharacterNameType.Genonym -> Genonym(given)
        CharacterNameType.Mononym -> Mononym(given)
    }
}
