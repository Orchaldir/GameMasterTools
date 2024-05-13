package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Culture
import at.orchaldir.gm.core.model.character.CultureId
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/cultures")
class Cultures {
    @Resource("details")
    class Details(val parent: Cultures = Cultures(), val id: CultureId)

    @Resource("new")
    class New(val parent: Cultures = Cultures())

    @Resource("delete")
    class Delete(val parent: Cultures = Cultures(), val id: CultureId)

    @Resource("edit")
    class Edit(val parent: Cultures = Cultures(), val id: CultureId)

    @Resource("update")
    class Update(val parent: Cultures = Cultures(), val id: CultureId)
}

fun Application.configureCultureRouting() {
    routing {
        get<Cultures> {
            logger.info { "Get all cultures" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCultures(call)
            }
        }
        get<Cultures.Details> { details ->
            logger.info { "Get details of culture ${details.id.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, details.id)
            }
        }
        get<Cultures.New> {
            logger.info { "Add new culture" }

            STORE.dispatch(CreateCulture)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, STORE.getState().cultures.lastId)
            }
        }
        get<Cultures.Delete> { delete ->
            logger.info { "Delete culture ${delete.id.value}" }

            STORE.dispatch(DeleteCulture(delete.id))

            call.respondHtml(HttpStatusCode.OK) {
                showAllCultures(call)
            }
        }
        get<Cultures.Edit> { edit ->
            logger.info { "Get editor for culture ${edit.id.value}" }

            call.respondHtml(HttpStatusCode.OK) {
                val culture = STORE.getState().cultures.get(edit.id)

                if (culture != null) {
                    showCultureEditor(call, culture)
                } else {
                    showAllCultures(call)
                }
            }
        }
        post<Cultures.Update> { update ->
            logger.info { "Update culture ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")

            STORE.dispatch(UpdateCulture(update.id, name))

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, update.id)
            }
        }
    }
}

private fun HTML.showAllCultures(call: ApplicationCall) {
    val cultures = STORE.getState().cultures
    val count = cultures.getSize()
    val createLink: String = call.application.href(Cultures.New(Cultures()))

    simpleHtml("Cultures") {
        field("Count", count.toString())
        ul {
            cultures.getAll().forEach { culture ->
                li {
                    val cultureLink = call.application.href(Cultures.Details(Cultures(), culture.id))
                    a(cultureLink) { +culture.name }
                }
            }
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCultureDetails(
    call: ApplicationCall,
    id: CultureId,
) {
    val state = STORE.getState()
    val culture = state.cultures.get(id)

    if (culture != null) {
        showCultureDetails(call, state, culture)
    } else {
        showAllCultures(call)
    }
}

private fun HTML.showCultureDetails(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink: String = call.application.href(Cultures())
    val deleteLink: String = call.application.href(Cultures.Delete(Cultures(), culture.id))
    val editLink: String = call.application.href(Cultures.Edit(Cultures(), culture.id))

    simpleHtml("Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        field("Name", culture.name)
        p {
            b { +"Characters: " }
            characterList(call, state.getCharacters(culture.id))
        }
        p { a(editLink) { +"Edit" } }

        if (state.canDelete(culture.id)) {
            p { a(deleteLink) { +"Delete" } }
        }

        p { a(backLink) { +"Back" } }
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    id: CultureId,
) {
    val culture = STORE.getState().cultures.get(id)

    if (culture != null) {
        showCultureEditor(call, culture)
    } else {
        showAllCultures(call)
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    culture: Culture,
) {
    val backLink: String = call.application.href(Cultures())
    val updateLink: String = call.application.href(Cultures.Update(Cultures(), culture.id))

    simpleHtml("Edit Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        form {
            p {
                b { +"Name: " }
                textInput(name = "name") {
                    value = culture.name
                }
            }
            p {
                submitInput {
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}