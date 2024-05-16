package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

const val TITLE = "Orchaldir's Game Master Tools"

fun Application.configureRouting() {
    routing {
        staticFiles("/static", File("static"))
        get("/") {
            logger.info { "Root" }
            val characterCount = STORE.getState().characters.getSize()
            val cultureCount = STORE.getState().cultures.getSize()
            val languageCount = STORE.getState().languages.getSize()
            val racesCount = STORE.getState().races.getSize()
            val charactersLink = call.application.href(Characters())
            val culturesLink = call.application.href(Cultures())
            val languagesLink = call.application.href(Languages())
            val racesLink = call.application.href(Races())

            call.respondHtml(HttpStatusCode.OK) {
                simpleHtml(TITLE) {
                    fieldLink("Characters", charactersLink, "$characterCount")
                    fieldLink("Cultures", culturesLink, "$cultureCount")
                    fieldLink("Languages", languagesLink, "$languageCount")
                    fieldLink("Races", racesLink, "$racesCount")
                }
            }
        }
    }
}
