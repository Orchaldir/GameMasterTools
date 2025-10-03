package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.selector.culture.getCultures
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showFashion(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    showAppearanceFashion(fashion.appearance)
    showClothingFashion(call, state, fashion.clothing)

    h2 { +"Usage" }

    fieldElements(call, state, state.getCultures(fashion.id))
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

fun parseFashionId(parameters: Parameters, param: String) = parseOptionalFashionId(parameters, param) ?: FashionId(0)
fun parseOptionalFashionId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { FashionId(it) }

fun parseFashion(
    state: State,
    parameters: Parameters,
    id: FashionId,
) = Fashion(
    id,
    parseName(parameters),
    parseAppearanceFashion(parameters),
    parseClothingFashion(parameters),
)
