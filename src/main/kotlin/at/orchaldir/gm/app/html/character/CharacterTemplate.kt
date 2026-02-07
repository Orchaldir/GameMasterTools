package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editKnownLanguages
import at.orchaldir.gm.app.html.culture.parseKnownLanguages
import at.orchaldir.gm.app.html.culture.parseOptionalCultureId
import at.orchaldir.gm.app.html.culture.showKnownLanguages
import at.orchaldir.gm.app.html.race.editRaceLookup
import at.orchaldir.gm.app.html.race.parseRaceLookup
import at.orchaldir.gm.app.html.race.showRaceLookup
import at.orchaldir.gm.app.html.rpg.statblock.editStatblockLookup
import at.orchaldir.gm.app.html.rpg.statblock.parseStatblockLookup
import at.orchaldir.gm.app.html.rpg.statblock.showStatblockLookupDetails
import at.orchaldir.gm.app.html.util.fieldBeliefStatus
import at.orchaldir.gm.app.html.util.parseBeliefStatus
import at.orchaldir.gm.app.html.util.selectBeliefStatus
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersUsing
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentIdMapForLookup
import at.orchaldir.gm.core.selector.rpg.statblock.getStatblock
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showCharacterTemplate(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val race = template.race.defaultRace()

    showRaceLookup(call, state, template.race)
    optionalField("Gender", template.gender)
    optionalFieldLink(call, state, template.culture)
    showKnownLanguages(call, state, template)
    fieldBeliefStatus(call, state, template.belief)

    if (race != null) {
        val statblock = state.getStatblock(race)

        showStatblockLookupDetails(call, state, statblock, template.statblock)
        showEquippedDetails(call, state, template.equipped, statblock, template.statblock)
    }

    showDataSources(call, state, template.sources)
    showUsage(call, state, template)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val characters = state.getCharactersUsing(template.id)
    val templates = state.getCharacterTemplates(template.id)

    if (characters.isEmpty() && templates.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
    fieldElements(call, state, templates)
}

// edit

fun HtmlBlockTag.editCharacterTemplate(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val raceId = template.race.defaultRace()
    val race = state.getRaceStorage().getOptional(raceId)

    selectName(template.name)
    editRaceLookup(state, template.race)

    if (race != null) {
        selectOptionalFromOneOf("Gender", GENDER, race.genders, template.gender)
    }

    editOptionalElement(state, CULTURE, state.getCultureStorage().getAll(), template.culture)
    editKnownLanguages(state, template.languages)
    selectBeliefStatus(state, BELIEVE, template.belief)

    if (race != null) {
        val statblock = race.lifeStages.statblock()

        editStatblockLookup(call, state, statblock, template.statblock, setOf(template.id))
        editEquipped(call, state, EQUIPPED, template.equipped, template.statblock)
    }

    editDataSources(state, template.sources)
}

// parse

fun parseCharacterTemplateId(parameters: Parameters, param: String) = CharacterTemplateId(parseInt(parameters, param))

fun parseCharacterTemplate(
    state: State,
    parameters: Parameters,
    id: CharacterTemplateId,
): CharacterTemplate {
    val lookup = parseStatblockLookup(state, parameters)
    val baseEquipment = state.getEquipmentIdMapForLookup(lookup)

    return CharacterTemplate(
        id,
        parseName(parameters),
        parseRaceLookup(parameters),
        parse<Gender>(parameters, GENDER),
        parseOptionalCultureId(parameters, CULTURE),
        parseKnownLanguages(parameters, state),
        parseBeliefStatus(parameters, state, BELIEVE),
        lookup,
        parseEquipped(parameters, state, EQUIPPED, baseEquipment),
        parseDataSources(parameters),
    )
}
