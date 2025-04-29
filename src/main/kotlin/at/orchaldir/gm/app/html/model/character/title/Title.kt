package at.orchaldir.gm.app.html.model.character.title

import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.WORD
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.selectText
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.character.title.TitlePosition
import io.ktor.http.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTitle(
    title: Title,
) {
    field("Tex", title.text.text)
    field("Position", title.position)
}

// edit

fun FORM.editTitle(
    title: Title,
) {
    selectName(title.name)
    selectText("Text", title.text.text, WORD, 1)
    selectValue("Position", POSITION, TitlePosition.entries, title.position)
}

// parse

fun parseTitleId(parameters: Parameters, param: String) = parseOptionalTitleId(parameters, param) ?: TitleId(0)
fun parseOptionalTitleId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { TitleId(it) }

fun parseTitle(parameters: Parameters, state: State, id: TitleId) = Title(
    id,
    parseName(parameters),
    parseNotEmptyString(parameters, WORD),
    parse(parameters, POSITION, TitlePosition.BeforeFamilyName),
)
