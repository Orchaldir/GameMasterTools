package at.orchaldir.gm.app.html.model.culture

import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showFashion(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    showAppearanceFashion(fashion.appearance)
    showClothingFashion(call, state, fashion.clothing)
}

// edit

fun HtmlBlockTag.editFashion(
    fashion: Fashion,
    state: State,
) {
    selectName(fashion.name)
    editAppearanceFashion(fashion.appearance)
    editClothingFashion(state, fashion.clothing)
}

// parse

fun parseFashionId(
    parameters: Parameters,
    param: String,
) = FashionId(parseInt(parameters, param))

fun parseFashion(id: FashionId, parameters: Parameters) = Fashion(
    id,
    parseName(parameters),
    parseAppearanceFashion(parameters),
    parseClothingFashion(parameters),
)
