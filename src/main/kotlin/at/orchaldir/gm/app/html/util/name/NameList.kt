package at.orchaldir.gm.app.html.util.name

import at.orchaldir.gm.app.NAMES
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.NameList
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.model.util.name.parseNames
import at.orchaldir.gm.core.selector.culture.getCultures
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showNameList(
    call: ApplicationCall,
    state: State,
    nameList: NameList,
) {
    fieldList("Names", nameList.names) { name ->
        +name.text
    }
    fieldElements(call, state, state.getCultures(nameList.id))
}

// edit

fun HtmlBlockTag.editNameList(nameList: NameList) {
    selectName(nameList.name)
    h2 { +"Names" }
    editTextArea(
        NAMES,
        30,
        nameList.names.size + 5,
        nameList.names.joinToString("\n") { it.text },
    )
}

// parse

fun parseNameListId(
    parameters: Parameters,
    param: String,
) = NameListId(parseInt(parameters, param))

fun parseNameList(state: State, parameters: Parameters, id: NameListId) = NameList(
    id,
    parseName(parameters),
    parseNames(parameters.getOrFail(NAMES)),
)

