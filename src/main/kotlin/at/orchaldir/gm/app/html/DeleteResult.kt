package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import io.ktor.server.application.*
import kotlinx.html.HTML

fun HTML.showDeleteResult(
    call: ApplicationCall,
    state: State,
    result: DeleteResult,
) {
    simpleHtml("Cannot delete ${result.id.print()}") {
        fieldLink("Element", call, state, result.id)

        result.elements.forEach { (_, ids) ->
            fieldIdList(call, state, ids)
        }
    }
}

