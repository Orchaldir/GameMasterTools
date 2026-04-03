package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.selector.character.name.canHaveFamilyName
import at.orchaldir.gm.core.selector.character.name.canHaveGenonym
import at.orchaldir.gm.core.selector.character.name.getGivenName
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// edit

fun HtmlBlockTag.selectCharacterName(
    call: ApplicationCall,
    state: State,
    character: Character,
) {
    val lastJob = character.employmentStatus.getLastJob()

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
                CharacterNameType.Occupational -> lastJob == null
            }
        }
        selectName("Given Name", character.getGivenName(), GIVEN_NAME)

        when (character.name) {
            is FamilyName -> {
                selectOptionalName("Middle Name", character.name.middle, combine(MIDDLE, NAME))
                selectName("Family Name", character.name.family, FAMILY_NAME)
            }

            is Genonym -> doNothing()
            is Mononym -> doNothing()
            is OccupationalName -> optionalFieldLink("Last Job", call, state, lastJob)
        }
    }
}

// parse

fun parseCharacterName(parameters: Parameters): CharacterName {
    val given = parseName(parameters, GIVEN_NAME)

    return when (parse(parameters, combine(NAME, TYPE), CharacterNameType.Mononym)) {
        CharacterNameType.Family -> FamilyName(
            given,
            parseOptionalName(parameters, combine(MIDDLE, NAME)),
            parseName(parameters, FAMILY_NAME, "Unknown"),
        )

        CharacterNameType.Genonym -> Genonym(given)
        CharacterNameType.Mononym -> Mononym(given)
        CharacterNameType.Occupational -> OccupationalName(given)
    }
}
