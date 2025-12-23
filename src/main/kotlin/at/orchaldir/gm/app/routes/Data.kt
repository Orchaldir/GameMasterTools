package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.editData
import at.orchaldir.gm.app.html.util.parseData
import at.orchaldir.gm.app.html.util.showData
import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.State
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
import kotlinx.html.h2
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

            val state = STORE.getState()

            call.respondHtml(HttpStatusCode.OK) {
                showDataDetails(call, state, state.data)
            }
        }
        get<DataRoutes.Edit> {
            logger.info { "Get editor for data" }

            val state = STORE.getState()

            call.respondHtml(HttpStatusCode.OK) {
                editDataDetails(call, state, state.data)
            }
        }
        post<DataRoutes.Preview> {
            logger.info { "Preview data" }

            val state = STORE.getState()
            val data = parseData(state, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                editDataDetails(call, state, data)
            }
        }
        post<DataRoutes.Update> {
            logger.info { "Update data" }

            val state = STORE.getState()
            val data = parseData(state, call.receiveParameters())

            STORE.dispatch(UpdateData(data))

            call.respondRedirect(call.application.href(DataRoutes()))

            STORE.getState().save()
        }
    }
}

private fun HTML.showDataDetails(
    call: ApplicationCall,
    state: State,
    data: Data,
) {
    val editLink = call.application.href(DataRoutes.Edit())

    simpleHtml("Data") {
        showData(call, state, data)

        h2 { +"Actions" }

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

    simpleHtml("Edit Data", true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editData(state, data)
            }
        }
    }
}


