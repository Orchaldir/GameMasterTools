package at.orchaldir.gm.app.routes

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.editConfig
import at.orchaldir.gm.app.html.util.parseConfig
import at.orchaldir.gm.app.html.util.showConfig
import at.orchaldir.gm.core.action.UpdateData
import at.orchaldir.gm.core.model.Config
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

@Resource("/config")
class ConfigRoutes {

    @Resource("edit")
    class Edit(val parent: ConfigRoutes = ConfigRoutes())

    @Resource("preview")
    class Preview(val parent: ConfigRoutes = ConfigRoutes())

    @Resource("update")
    class Update(val parent: ConfigRoutes = ConfigRoutes())
}

fun Application.configureConfigRouting() {
    routing {
        get<ConfigRoutes> {
            logger.info { "Get config" }

            val state = STORE.getState()

            call.respondHtml(HttpStatusCode.OK) {
                showConfigDetails(call, state, state.config)
            }
        }
        get<ConfigRoutes.Edit> {
            logger.info { "Get editor for config" }

            val state = STORE.getState()

            call.respondHtml(HttpStatusCode.OK) {
                editConfigDetails(call, state, state.config)
            }
        }
        post<ConfigRoutes.Preview> {
            logger.info { "Preview config" }

            val state = STORE.getState()
            val config = parseConfig(state, call.receiveParameters())

            call.respondHtml(HttpStatusCode.OK) {
                editConfigDetails(call, state, config)
            }
        }
        post<ConfigRoutes.Update> {
            logger.info { "Update data" }

            val state = STORE.getState()
            val config = parseConfig(state, call.receiveParameters())

            STORE.dispatch(UpdateData(config))

            call.respondRedirect(call.application.href(ConfigRoutes()))

            STORE.getState().save()
        }
    }
}

private fun HTML.showConfigDetails(
    call: ApplicationCall,
    state: State,
    config: Config,
) {
    val editLink = call.application.href(ConfigRoutes.Edit())

    simpleHtml("Config") {
        showConfig(call, state, config)

        h2 { +"Actions" }

        action(editLink, "Edit")
        back("/")
    }
}

private fun HTML.editConfigDetails(
    call: ApplicationCall,
    state: State,
    config: Config,
) {
    val backLink = call.application.href(ConfigRoutes())
    val previewLink = call.application.href(ConfigRoutes.Preview())
    val updateLink = call.application.href(ConfigRoutes.Update())

    simpleHtml("Edit Config", true) {
        mainFrame {
            formWithPreview(previewLink, updateLink, backLink) {
                editConfig(state, config)
            }
        }
    }
}


