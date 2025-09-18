package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Id
import io.ktor.server.application.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

fun HTML.showDeleteResult(
    call: ApplicationCall,
    state: State,
    result: DeleteResult,
) {
    simpleHtml("Cannot delete ${result.id.print()}") {
        fieldLink("Name", call, state, result.id)

        h2 { +"Blocking Elements" }

        result.elements.forEach { (_, ids) ->
            fieldAnyIds(call, state, ids)
        }
    }
}

private fun HtmlBlockTag.fieldAnyIds(
    call: ApplicationCall,
    state: State,
    ids: Collection<Id<*>>,
) {
    if (ids.isNotEmpty()) {
        val first = ids.first()
        field(first.plural()) {
            showList(ids) {
                link(call, state, it)
            }
        }
    }
}

