package at.orchaldir.gm.app.routes.character.title

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.title.editTitle
import at.orchaldir.gm.app.html.model.character.title.parseTitle
import at.orchaldir.gm.app.html.model.character.title.showTitle
import at.orchaldir.gm.core.action.CreateTitle
import at.orchaldir.gm.core.action.DeleteTitle
import at.orchaldir.gm.core.action.UpdateTitle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.title.TITLE_TYPE
import at.orchaldir.gm.core.model.character.title.Title
import at.orchaldir.gm.core.model.character.title.TitleId
import at.orchaldir.gm.core.model.util.SortTitle
import at.orchaldir.gm.core.selector.character.canDeleteTitle
import at.orchaldir.gm.core.selector.util.sortTitles
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$TITLE_TYPE")
class TitleRoutes {
    @Resource("all")
    class All(
        val sort: SortTitle = SortTitle.Name,
        val parent: TitleRoutes = TitleRoutes(),
    )

    @Resource("details")
    class Details(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("new")
    class New(val parent: TitleRoutes = TitleRoutes())

    @Resource("delete")
    class Delete(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("edit")
    class Edit(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("preview")
    class Preview(val id: TitleId, val parent: TitleRoutes = TitleRoutes())

    @Resource("update")
    class Update(val id: TitleId, val parent: TitleRoutes = TitleRoutes())
}

fun Application.configureTitleRouting() {
    routing {
        get<TitleRoutes.All> { all ->
            logger.info { "Get all title" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTitlees(call, STORE.getState(), all.sort)
            }
        }
        get<TitleRoutes.Details> { details ->
            logger.info { "Get details of title ${details.id.value}" }

            val state = STORE.getState()
            val title = state.getTitleStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTitleDetails(call, state, title)
            }
        }
        get<TitleRoutes.New> {
            logger.info { "Add new title" }

            STORE.dispatch(CreateTitle)

            call.respondRedirect(
                call.application.href(
                    TitleRoutes.Edit(
                        STORE.getState().getTitleStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<TitleRoutes.Delete> { delete ->
            logger.info { "Delete title ${delete.id.value}" }

            STORE.dispatch(DeleteTitle(delete.id))

            call.respondRedirect(call.application.href(TitleRoutes()))

            STORE.getState().save()
        }
        get<TitleRoutes.Edit> { edit ->
            logger.info { "Get editor for title ${edit.id.value}" }

            val state = STORE.getState()
            val title = state.getTitleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTitleEditor(call, state, title)
            }
        }
        post<TitleRoutes.Preview> { preview ->
            logger.info { "Preview title ${preview.id.value}" }

            val state = STORE.getState()
            val title = parseTitle(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTitleEditor(call, state, title)
            }
        }
        post<TitleRoutes.Update> { update ->
            logger.info { "Update title ${update.id.value}" }

            val title = parseTitle(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateTitle(title))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllTitlees(
    call: ApplicationCall,
    state: State,
    sort: SortTitle,
) {
    val titles = state.sortTitles(sort)
    val createLink = call.application.href(TitleRoutes.New())
    val sortNameLink = call.application.href(TitleRoutes.All())

    simpleHtml("Titles") {
        field("Count", titles.size)
        field("Sort") {
            link(sortNameLink, "Name")
        }
        table {
            tr {
                th { +"Name" }
                th { +"Text" }
                th { +"Position" }
            }
            titles.forEach { title ->
                tr {
                    td { link(call, state, title) }
                    tdString(title.text)
                    tdEnum(title.position)
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTitleDetails(
    call: ApplicationCall,
    state: State,
    title: Title,
) {
    val backLink = call.application.href(TitleRoutes.All())
    val deleteLink = call.application.href(TitleRoutes.Delete(title.id))
    val editLink = call.application.href(TitleRoutes.Edit(title.id))

    simpleHtmlDetails(title) {
        showTitle(title)

        action(editLink, "Edit")
        if (state.canDeleteTitle(title.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showTitleEditor(
    call: ApplicationCall,
    state: State,
    title: Title,
) {
    val backLink = href(call, title.id)
    val previewLink = call.application.href(TitleRoutes.Preview(title.id))
    val updateLink = call.application.href(TitleRoutes.Update(title.id))

    simpleHtmlEditor(title) {
        formWithPreview(previewLink, updateLink, backLink) {
            editTitle(title)
        }
    }
}
