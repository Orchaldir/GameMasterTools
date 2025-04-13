package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFashion(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    field("Name", fashion.name)
    showAppearanceStyle(fashion.appearance)
    showClothingStyle(call, state, fashion.clothing)
}

// edit

fun HtmlBlockTag.editFashion(
    fashion: Fashion,
    state: State,
) {
    selectName(fashion.name)
    editAppearanceOptions(fashion.appearance)
    editClothingStyle(state, fashion.clothing)
}

// parse

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parseInt(parameters, param))

fun parseFashion(id: FashionId, parameters: Parameters) = Fashion(
    id,
    parameters.getOrFail(NAME),
    parseAppearanceStyle(parameters),
    parseClothingStyle(parameters),
)
