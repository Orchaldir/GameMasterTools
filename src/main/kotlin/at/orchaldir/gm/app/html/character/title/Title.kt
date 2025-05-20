package at.orchaldir.gm.app.html.character.title

import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.SEPARATOR
import at.orchaldir.gm.app.WORD
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.character.title.TitlePosition
import at.orchaldir.gm.core.selector.character.getCharacters
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTitle(
    call: ApplicationCall,
    state: State,
    title: Title,
) {
    field("Text", title.text)
    field("Position", title.position)
    field("Separator", title.separator)
    fieldList(call, state, state.getCharacters(title.id))
}

// edit

fun FORM.editTitle(
    title: Title,
) {
    selectName(title.name)
    selectNotEmptyString("Text", title.text, WORD)
    selectValue("Position", POSITION, TitlePosition.entries, title.position)
    selectChar("Separator", title.separator, SEPARATOR)
}

// parse

fun parseTitleId(parameters: Parameters, param: String) = parseOptionalTitleId(parameters, param) ?: TitleId(0)
fun parseOptionalTitleId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TitleId(it) }

fun parseTitle(parameters: Parameters, state: State, id: TitleId) = Title(
    id,
    parseName(parameters),
    parseNotEmptyString(parameters, WORD),
    parse(parameters, POSITION, TitlePosition.BeforeFamilyName),
    parseChar(parameters, SEPARATOR, ' '),
)
