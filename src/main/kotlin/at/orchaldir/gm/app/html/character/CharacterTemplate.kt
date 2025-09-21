package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.statistic.editStatblock
import at.orchaldir.gm.app.html.character.statistic.parseStatblock
import at.orchaldir.gm.app.html.character.statistic.showStatblock
import at.orchaldir.gm.app.html.culture.editKnownLanguages
import at.orchaldir.gm.app.html.culture.parseKnownLanguages
import at.orchaldir.gm.app.html.culture.parseOptionalCultureId
import at.orchaldir.gm.app.html.culture.showKnownLanguages
import at.orchaldir.gm.app.html.item.parseOptionalUniformId
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.fieldBeliefStatus
import at.orchaldir.gm.app.html.util.parseBeliefStatus
import at.orchaldir.gm.app.html.util.selectBeliefStatus
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.selector.character.getCharactersUsing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showCharacterTemplate(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    fieldLink(call, state, template.race)
    optionalField("Gender", template.gender)
    optionalFieldLink(call, state, template.culture)
    showKnownLanguages(call, state, template)
    fieldBeliefStatus(call, state, template.belief)
    optionalFieldLink(call, state, template.uniform)
    showStatblock(call, state, template.statblock)
    showDataSources(call, state, template.sources)
    showUsage(call, state, template)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val characters = state.getCharactersUsing(template.id)

    if (characters.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
}

// edit

fun FORM.editCharacterTemplate(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val race = state.getRaceStorage().getOrThrow(template.race)

    selectName(template.name)
    selectElement(state, RACE, state.getRaceStorage().getAll(), template.race)
    selectOptionalFromOneOf("Gender", GENDER, race.genders, template.gender)
    editOptionalElement(state, CULTURE, state.getCultureStorage().getAll(), template.culture)
    editKnownLanguages(state, template.languages)
    selectBeliefStatus(state, BELIEVE, template.belief)
    editOptionalElement(state, UNIFORM, state.getUniformStorage().getAll(), template.uniform)
    editStatblock(call, state, template.statblock)
    editDataSources(state, template.sources)
}

// parse

fun parseCharacterTemplateId(parameters: Parameters, param: String) = CharacterTemplateId(parseInt(parameters, param))

fun parseCharacterTemplate(
    state: State,
    parameters: Parameters,
    id: CharacterTemplateId,
) = CharacterTemplate(
    id,
    parseName(parameters),
    parseRaceId(parameters, RACE),
    parse<Gender>(parameters, GENDER),
    parseOptionalCultureId(parameters, CULTURE),
    parseKnownLanguages(parameters, state),
    parseBeliefStatus(parameters, state, BELIEVE),
    parseOptionalUniformId(parameters, UNIFORM),
    parseStatblock(state, parameters),
    parseDataSources(parameters),
)
