package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.showDate
import at.orchaldir.gm.app.html.showList
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.History
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

fun <T> HtmlBlockTag.showHistory(
    call: ApplicationCall,
    state: State,
    history: History<T>,
    label: String,
    showEntry: HtmlBlockTag.(ApplicationCall, State, T) -> Unit,
) {
    showList("Previous $label", history.previousEntries) { previous ->
        +"Until "
        showDate(call, state, previous.until)
        +": "
        showEntry(call, state, previous.entry)
    }
    field(label) {
        showEntry(call, state, history.current)
    }
}
