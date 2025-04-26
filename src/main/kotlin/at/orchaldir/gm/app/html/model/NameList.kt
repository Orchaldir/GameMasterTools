package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.NAMES
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.name.NameList
import at.orchaldir.gm.core.model.name.NameListId
import at.orchaldir.gm.core.selector.culture.getCultures
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.id
import kotlinx.html.textArea

// show

fun HtmlBlockTag.showNameList(
    call: ApplicationCall,
    state: State,
    nameList: NameList,
) {
    showList("Names", nameList.names) { name ->
        +name.text
    }
    showList("Cultures", state.getCultures(nameList.id)) { culture ->
        link(call, culture)
    }
}

// edit

fun FORM.editNameList(nameList: NameList) {
    selectName(nameList.name)
    h2 { +"Names" }
    textArea {
        id = "names"
        name = "names"
        cols = "30"
        rows = (nameList.name.text.length + 5).toString()
        +nameList.names.joinToString("\n") { it.text }
    }
}

// parse

fun parseNameListId(
    parameters: Parameters,
    param: String,
) = NameListId(parseInt(parameters, param))

fun parseNameList(id: NameListId, parameters: Parameters) = NameList(
    id,
    parseName(parameters),
    parseNames(parameters),
)

private fun parseNames(parameters: Parameters): List<Name> = parameters.getOrFail(NAMES)
    .split("\n", ",", ".", ";")
    .map { it.trim() }
    .filter { it.isNotEmpty() }
    .map { Name.init(it) }
    .toList()
