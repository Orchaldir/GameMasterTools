package at.orchaldir.gm.app.html.model.character.title

import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.SEPARATOR
import at.orchaldir.gm.app.WORD
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseNotEmptyString
import at.orchaldir.gm.app.html.selectOptionalText
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.html.parseOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.selectNotEmptyString
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
    field("Text", title.text)
    field("Position", title.position)
    field("Separator", title.separator?.let { "\"$it\"" } ?: "")
}

// edit

fun FORM.editTitle(
    title: Title,
) {
    selectName(title.name)
    selectNotEmptyString("Text", title.text, WORD)
    selectValue("Position", POSITION, TitlePosition.entries, title.position)
    selectOptionalText("Separator", title.separator?.toString(), SEPARATOR)
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
