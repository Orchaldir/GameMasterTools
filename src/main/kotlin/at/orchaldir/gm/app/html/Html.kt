package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.APP_TITLE
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.SkinColor
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHairColorEnum
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.reverseAndSort
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import kotlinx.html.*

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HTML.simpleHtmlDetails(
    element: ELEMENT,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml(element, "", keepPositionAfterReload, content)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HTML.simpleHtmlDetails(
    state: State,
    element: ELEMENT,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml(state, element, "", keepPositionAfterReload, content)

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HTML.simpleHtmlEditor(
    element: ELEMENT,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml(element, "Edit ", keepPositionAfterReload, content)

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HTML.simpleHtml(
    element: ELEMENT,
    prefix: String,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml("$prefix${element.id().type()}: ${element.name()}", keepPositionAfterReload, content)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HTML.simpleHtml(
    state: State,
    element: ELEMENT,
    prefix: String,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml("$prefix${element.id().type()}: ${element.name(state)}", keepPositionAfterReload, content)


fun HTML.simpleHtml(
    title: String,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) {
    head {
        title {
            if (title != APP_TITLE) {
                +"$title - $APP_TITLE"
            } else {
                +APP_TITLE
            }
        }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
        script(src = "/static/scripts.js") {
            charset = "utf-8"
            defer = true
        }
    }
    body {
        if (keepPositionAfterReload) {
            onLoad = "loadScroll()"
            onBeforeunLoad = "saveScroll()"
        } else {
            onLoad = "clearScroll()"
        }
        h1 { +title }

        content()
    }
}

fun HtmlBlockTag.mainFrame(content: DIV.() -> Unit) {
    div {
        id = "main"
        classes += "main"
        content()
    }
}

fun HtmlBlockTag.split(left: DIV.() -> Unit, right: DIV.() -> Unit) {
    div {
        id = "main"
        classes += "split"
        left()
    }
    div {
        id = "right"
        classes += "split"
        right()
    }
}

// misc

fun HtmlBlockTag.svg(svg: Svg, width: Int) {
    div {
        style = "display: inline-block; width:$width%"
        +"\n"
        unsafe { +svg.export() }
        +"\n"
    }
}

fun HtmlBlockTag.showDetails(
    label: String?,
    isOpen: Boolean = false,
    content: DETAILS.() -> Unit,
) {
    details {
        open = isOpen
        summary { +(label ?: "") }

        content()
    }
}

fun HtmlBlockTag.showTooltip(
    tooltip: String,
    content: ABBR.() -> Unit,
) {
    abbr {
        title = tooltip

        content()
    }
}
