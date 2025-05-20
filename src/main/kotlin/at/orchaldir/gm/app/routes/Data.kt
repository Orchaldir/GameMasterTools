package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.action
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.model.util.editData
import at.orchaldir.gm.app.html.model.util.parseData
import at.orchaldir.gm.app.html.model.util.showData
import at.orchaldir.gm.app.html.simpleHtml
import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/data")
class DataRoutes {

    @Resource("edit")
    class Edit(val parent: DataRoutes = DataRoutes())

    @Resource("preview")
    class Preview(val parent: DataRoutes = DataRoutes())

    @Resource("update")
    class Update(val parent: DataRoutes = DataRoutes())
}

fun Application.configureDataRouting() {
    routing {
        get<DataRoutes> {
            logger.info { "Get data" }

            call.respondHtml(HttpStatusCode.OK) {
                showDataDetails(call)
            }
        }
        get<DataRoutes.Edit> {
            logger.info { "Get editor for data" }

            val state = STORE.getState()

            call.respondHtml(HttpStatusCode.OK) {
                editDataDetails(call, state, state.data)
            }
        }
        post<DataRoutes.Preview> { preview ->
            logger.info { "Preview data" }

            val state = STORE.getState()
            val data = parseData(call.receiveParameters(), state.getDefaultCalendar())

            call.respondHtml(HttpStatusCode.OK) {
                editDataDetails(call, state, data)
            }
        }
        post<DataRoutes.Update> {
            logger.info { "Update data" }

            val data = parseData(call.receiveParameters(), STORE.getState().getDefaultCalendar())

            STORE.dispatch(UpdateData(data))

            call.respondRedirect(call.application.href(DataRoutes()))

            STORE.getState().save()
        }
    }
}

private fun HTML.showDataDetails(call: ApplicationCall) {
    val state = STORE.getState()
    val editLink = call.application.href(DataRoutes.Edit())

    simpleHtml("Data") {
        showData(call, state)
        action(editLink, "Edit")
        back("/")
    }
}

private fun HTML.editDataDetails(
    call: ApplicationCall,
    state: State,
    data: Data,
) {
    val backLink = call.application.href(DataRoutes())
    val previewLink = call.application.href(DataRoutes.Preview())
    val updateLink = call.application.href(DataRoutes.Update())

    simpleHtml("Edit Data") {
        formWithPreview(previewLink, updateLink, backLink) {
            editData(state, data)
        }
    }
}


