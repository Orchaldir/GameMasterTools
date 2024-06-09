package at.orchaldir.gm.app.plugins

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.core.selector.getCharacters
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/cultures")
class Cultures {
    @Resource("details")
    class Details(val id: CultureId, val parent: Cultures = Cultures())

    @Resource("new")
    class New(val parent: Cultures = Cultures())

    @Resource("delete")
    class Delete(val id: CultureId, val parent: Cultures = Cultures())

    @Resource("edit")
    class Edit(val id: CultureId, val parent: Cultures = Cultures())

    @Resource("update")
    class Update(val id: CultureId, val parent: Cultures = Cultures())
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

            val state = STORE.getState()
            val culture = state.cultures.getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, state, culture)
            }
        }
        get<Cultures.New> {
            logger.info { "Add new culture" }

            STORE.dispatch(CreateCulture)

            call.respondRedirect(call.application.href(Cultures.Edit(STORE.getState().cultures.lastId)))

            STORE.getState().save()
        }
        get<Cultures.Delete> { delete ->
            logger.info { "Delete culture ${delete.id.value}" }

            STORE.dispatch(DeleteCulture(delete.id))

            call.respondRedirect(call.application.href(Cultures()))

            STORE.getState().save()
        }
        get<Cultures.Edit> { edit ->
            logger.info { "Get editor for culture ${edit.id.value}" }

            val culture = STORE.getState().cultures.getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, culture)
            }
        }
        post<Cultures.Update> { update ->
            logger.info { "Update culture ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")

            STORE.dispatch(UpdateCulture(update.id, name))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCultures(call: ApplicationCall) {
    val cultures = STORE.getState().cultures.getAll().sortedBy { it.name }
    val count = cultures.size
    val createLink = call.application.href(Cultures.New(Cultures()))

    simpleHtml("Cultures") {
        field("Count", count.toString())
        showList(cultures) { culture ->
            link(call, culture)
        }
        p { a(createLink) { +"Add" } }
        p { a("/") { +"Back" } }
    }
}

private fun HTML.showCultureDetails(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink = call.application.href(Cultures())
    val deleteLink = call.application.href(Cultures.Delete(culture.id))
    val editLink = call.application.href(Cultures.Edit(culture.id))

    simpleHtml("Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        field("Name", culture.name)
        field("Characters") {
            showList(state.getCharacters(culture.id)) { character ->
                link(call, character)
            }
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
    culture: Culture,
) {
    val backLink = href(call, culture.id)
    val updateLink = call.application.href(Cultures.Update(culture.id))

    simpleHtml("Edit Culture: ${culture.name}") {
        field("Id", culture.id.value.toString())
        form {
            field("Name") {
                textInput(name = "name") {
                    value = culture.name
                }
            }
            p {
                submitInput {
                    value = "Update"
                    formAction = updateLink
                    formMethod = InputFormMethod.post
                }
            }
        }
        p { a(backLink) { +"Back" } }
    }
}