package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editCulture
import at.orchaldir.gm.app.html.culture.parseCulture
import at.orchaldir.gm.app.html.culture.showCulture
import at.orchaldir.gm.app.routes.handleCloneElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CloneCulture
import at.orchaldir.gm.core.action.CreateCulture
import at.orchaldir.gm.core.action.DeleteCulture
import at.orchaldir.gm.core.action.UpdateCulture
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.selector.character.getCharacters
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
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$CULTURE_TYPE")
class CultureRoutes {
    @Resource("details")
    class Details(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("new")
    class New(val parent: CultureRoutes = CultureRoutes())

    @Resource("clone")
    class Clone(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("delete")
    class Delete(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("edit")
    class Edit(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("preview")
    class Preview(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("update")
    class Update(val id: CultureId, val parent: CultureRoutes = CultureRoutes())
}

fun Application.configureCultureRouting() {
    routing {
        get<CultureRoutes> {
            logger.info { "Get all cultures" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCultures(call, STORE.getState())
            }
        }
        get<CultureRoutes.Details> { details ->
            logger.info { "Get details of culture ${details.id.value}" }

            val state = STORE.getState()
            val culture = state.getCultureStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureDetails(call, state, culture)
            }
        }
        get<CultureRoutes.New> {
            logger.info { "Add new culture" }

            STORE.dispatch(CreateCulture)

            call.respondRedirect(call.application.href(CultureRoutes.Edit(STORE.getState().getCultureStorage().lastId)))

            STORE.getState().save()
        }
        get<CultureRoutes.Clone> { clone ->
            handleCloneElement(clone.id, CloneCulture(clone.id)) { cloneId ->
                CultureRoutes.Edit(cloneId)
            }
        }
        get<CultureRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteCulture(delete.id), CultureRoutes())
        }
        get<CultureRoutes.Edit> { edit ->
            logger.info { "Get editor for culture ${edit.id.value}" }

            val state = STORE.getState()
            val culture = state.getCultureStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, state, culture)
            }
        }
        post<CultureRoutes.Preview> { preview ->
            logger.info { "Get preview for culture ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val culture = parseCulture(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCultureEditor(call, STORE.getState(), culture)
            }
        }
        post<CultureRoutes.Update> { update ->
            logger.info { "Update culture ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val culture = parseCulture(formParameters, update.id)

            STORE.dispatch(UpdateCulture(culture))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCultures(
    call: ApplicationCall,
    state: State,
) {
    val cultures = STORE.getState().getCultureStorage().getAll().sortedBy { it.name.text }
    val count = cultures.size
    val createLink = call.application.href(CultureRoutes.New())

    simpleHtml("Cultures") {
        field("Count", count)

        table {
            tr {
                th { +"Name" }
                th { +"Calendar" }
                th { +"Languages" }
                thMultiLines(listOf("Naming", "Convention"))
                th { +"Holidays" }
                th { +"Characters" }
            }
            cultures.forEach { culture ->
                tr {
                    tdLink(call, state, culture.id)
                    tdLink(call, state, culture.calendar)
                    tdInlineIds(call, state, culture.languages.getValuesFor(Rarity.Everyone))
                    tdEnum(culture.namingConvention.getType())
                    tdSkipZero(culture.holidays)
                    tdSkipZero(state.getCharacters(culture.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCultureDetails(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink = call.application.href(CultureRoutes())
    val cloneLink = call.application.href(CultureRoutes.Clone(culture.id))
    val deleteLink = call.application.href(CultureRoutes.Delete(culture.id))
    val editLink = call.application.href(CultureRoutes.Edit(culture.id))

    simpleHtmlDetails(culture) {
        showCulture(call, state, culture)

        action(editLink, "Edit")
        action(cloneLink, "Clone")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showCultureEditor(
    call: ApplicationCall,
    state: State,
    culture: Culture,
) {
    val backLink = href(call, culture.id)
    val previewLink = call.application.href(CultureRoutes.Preview(culture.id))
    val updateLink = call.application.href(CultureRoutes.Update(culture.id))

    simpleHtmlEditor(culture) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCulture(state, culture)
        }
    }
}


