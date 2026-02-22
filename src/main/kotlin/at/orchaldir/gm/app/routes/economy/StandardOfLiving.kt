package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.back
import at.orchaldir.gm.app.html.economy.showStandardOfLiving
import at.orchaldir.gm.app.html.simpleHtmlDetails
import at.orchaldir.gm.app.routes.ConfigRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.standard.STANDARD_TYPE
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$STANDARD_TYPE")
class StandardOfLivingRoutes {
    @Resource("details")
    class Details(val id: StandardOfLivingId, val parent: StandardOfLivingRoutes = StandardOfLivingRoutes())

}

fun Application.configureStandardOfLivingRouting() {
    routing {
        get<StandardOfLivingRoutes.Details> { details ->
            logger.info { "Get details of standard ${details.id.value}" }

            val state = STORE.getState()
            val standard = state.config.economy.getStandardOfLiving(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStandardOfLivingDetails(call, state, standard)
            }
        }
    }
}

private fun HTML.showStandardOfLivingDetails(
    call: ApplicationCall,
    state: State,
    standard: StandardOfLiving,
) {
    val backLink = call.application.href(ConfigRoutes())

    simpleHtmlDetails(standard) {
        showStandardOfLiving(call, state, standard)

        back(backLink)
    }
}

