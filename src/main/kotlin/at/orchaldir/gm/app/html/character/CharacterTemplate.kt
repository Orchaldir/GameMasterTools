package at.orchaldir.gm.app.html.character

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseOptionalCultureId
import at.orchaldir.gm.app.html.culture.showKnownLanguages
import at.orchaldir.gm.app.html.item.parseOptionalUniformId
import at.orchaldir.gm.app.html.race.parseRaceId
import at.orchaldir.gm.app.html.util.fieldBeliefStatus
import at.orchaldir.gm.app.html.util.parseBeliefStatus
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.character.Gender
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

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
    showDataSources(call, state, template.sources)
}

// edit

fun FORM.editCharacterTemplate(
    state: State,
    template: CharacterTemplate,
) {
    selectName(template.name)
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
    emptyMap(),
    parseBeliefStatus(parameters, state, BELIEVE),
    parseOptionalUniformId(parameters, UNIFORM),
    parseDataSources(parameters),
)
