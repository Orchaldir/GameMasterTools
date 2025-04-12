package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.character.appearance.PaddedSize
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.div

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showGallery(
    call: ApplicationCall,
    elements: List<Triple<ELEMENT, String, PaddedSize>>,
    getSvg: (Triple<ELEMENT, String, PaddedSize>) -> Svg,
) = showGallery(
    call,
    elements,
    { entry -> entry.first.id() },
    { entry -> entry.second },
    getSvg,
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showGallery(
    call: ApplicationCall,
    elements: List<ELEMENT>,
    getName: (ELEMENT) -> String,
    getSvg: (ELEMENT) -> Svg,
) = showGallery(
    call,
    elements,
    { element -> element.id() },
    getName,
    getSvg,
)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showGallery(
    call: ApplicationCall,
    state: State,
    elements: List<ELEMENT>,
    getSvg: (ELEMENT) -> Svg,
) = showGallery(
    call,
    elements,
    { element -> element.id() },
    { element -> element.name(state) },
    getSvg,
)

fun <T, ID : Id<ID>> HtmlBlockTag.showGallery(
    call: ApplicationCall,
    elements: List<T>,
    getId: (T) -> ID,
    getName: (T) -> String,
    getSvg: (T) -> Svg,
) {
    div("grid-container") {
        elements.forEach { element ->
            val svg = getSvg(element)

            div("grid-item") {
                a(href(call, getId(element))) {
                    div {
                        +getName(element)
                    }
                    svg(svg, 100)
                }
            }
        }
    }
}
