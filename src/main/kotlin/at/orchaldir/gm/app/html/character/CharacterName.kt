package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.title.parseOptionalTitleId
import at.orchaldir.gm.app.html.culture.editKnownLanguages
import at.orchaldir.gm.app.html.culture.parseKnownLanguages
import at.orchaldir.gm.app.html.culture.parseOptionalCultureId
import at.orchaldir.gm.app.html.culture.showKnownLanguages
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.rpg.statblock.editStatblockLookup
import at.orchaldir.gm.app.html.rpg.statblock.parseStatblockLookup
import at.orchaldir.gm.app.html.rpg.statblock.showStatblockLookupDetails
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.fieldDistance
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.routes.character.CharacterRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.character.appearance.HeadOnly
import at.orchaldir.gm.core.model.character.appearance.HumanoidBody
import at.orchaldir.gm.core.model.character.appearance.UndefinedAppearance
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.aging.SimpleAging
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.date.Year
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.character.*
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMapForLookup
import at.orchaldir.gm.core.selector.organization.getOrganizations
import at.orchaldir.gm.core.selector.race.getExistingRaces
import at.orchaldir.gm.core.selector.realm.getBattlesLedBy
import at.orchaldir.gm.core.selector.time.getCurrentYear
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.canHaveFamilyName
import at.orchaldir.gm.core.selector.util.canHaveGenonym
import at.orchaldir.gm.core.selector.util.getGivenName
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.util.*
import kotlinx.html.*
import kotlin.random.Random

// edit

fun HtmlBlockTag.selectCharacterName(
    state: State,
    character: Character,
) {
    showDetails("Name", true) {
        field("Type") {
            select {
                id = NAME_TYPE
                name = NAME_TYPE
                onChange = ON_CHANGE_SCRIPT
                option {
                    label = "Mononym"
                    value = "Mononym"
                    selected = character.name is Mononym
                }
                option {
                    label = "FamilyName"
                    value = "FamilyName"
                    selected = character.name is FamilyName
                    disabled = !state.canHaveFamilyName(character)
                }
                option {
                    label = "Genonym"
                    value = "Genonym"
                    selected = character.name is Genonym
                    disabled = !state.canHaveGenonym(character)
                }
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

    return when (parameters.getOrFail(NAME_TYPE)) {
        "FamilyName" -> FamilyName(
            given,
            parseOptionalName(parameters, MIDDLE_NAME),
            parseName(parameters, FAMILY_NAME, "Unknown"),
        )

        "Genonym" -> Genonym(given)
        else -> Mononym(given)
    }
}
