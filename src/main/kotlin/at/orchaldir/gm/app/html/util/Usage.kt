package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.linkWithStar
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.sortElementsWithStar
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showGenericUsage(
    call: ApplicationCall,
    state: State,
    lists: List<List<Element<out Id<*>>>>,
) {
    val filtered = lists
        .filter { it.isNotEmpty() }

    if (filtered.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    val sorted = filtered.sortedBy {
        it.first().id().type()
    }

    sorted.forEach {
        field(it.first().id().plural()) {
            showList(state.sortElementsWithStar(it)) {
                linkWithStar(call, state, it)
            }
        }
    }
}
