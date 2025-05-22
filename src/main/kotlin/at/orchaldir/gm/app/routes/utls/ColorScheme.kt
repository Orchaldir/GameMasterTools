package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.color.editColorScheme
import at.orchaldir.gm.app.html.util.color.parseColorScheme
import at.orchaldir.gm.app.html.util.color.showColorScheme
import at.orchaldir.gm.core.action.CreateColorScheme
import at.orchaldir.gm.core.action.DeleteColorScheme
import at.orchaldir.gm.core.action.UpdateColorScheme
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortColorScheme
import at.orchaldir.gm.core.model.util.render.COLOR_SCHEME_TYPE
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.util.canDeleteColorScheme
import at.orchaldir.gm.core.selector.util.sortColorSchemes
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

@Resource("/$COLOR_SCHEME_TYPE")
class ColorSchemeRoutes {
    @Resource("all")
    class All(
        val sort: SortColorScheme = SortColorScheme.Name,
        val parent: ColorSchemeRoutes = ColorSchemeRoutes(),
    )

    @Resource("details")
    class Details(val id: ColorSchemeId, val parent: ColorSchemeRoutes = ColorSchemeRoutes())

    @Resource("new")
    class New(val parent: ColorSchemeRoutes = ColorSchemeRoutes())

    @Resource("delete")
    class Delete(val id: ColorSchemeId, val parent: ColorSchemeRoutes = ColorSchemeRoutes())

    @Resource("edit")
    class Edit(val id: ColorSchemeId, val parent: ColorSchemeRoutes = ColorSchemeRoutes())

    @Resource("preview")
    class Preview(val id: ColorSchemeId, val parent: ColorSchemeRoutes = ColorSchemeRoutes())

    @Resource("update")
    class Update(val id: ColorSchemeId, val parent: ColorSchemeRoutes = ColorSchemeRoutes())
}

fun Application.configureColorSchemeRouting() {
    routing {
        get<ColorSchemeRoutes.All> { all ->
            logger.info { "Get all color schemes" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllColorSchemes(call, STORE.getState(), all.sort)
            }
        }
        get<ColorSchemeRoutes.Details> { details ->
            logger.info { "Get details of color scheme ${details.id.value}" }

            val state = STORE.getState()
            val scheme = state.getColorSchemeStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showColorSchemeDetails(call, state, scheme)
            }
        }
        get<ColorSchemeRoutes.New> {
            logger.info { "Add new color scheme" }

            STORE.dispatch(CreateColorScheme)

            call.respondRedirect(
                call.application.href(
                    ColorSchemeRoutes.Edit(
                        STORE.getState().getColorSchemeStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<ColorSchemeRoutes.Delete> { delete ->
            logger.info { "Delete color scheme ${delete.id.value}" }

            STORE.dispatch(DeleteColorScheme(delete.id))

            call.respondRedirect(call.application.href(ColorSchemeRoutes.All()))

            STORE.getState().save()
        }
        get<ColorSchemeRoutes.Edit> { edit ->
            logger.info { "Get editor for color scheme ${edit.id.value}" }

            val state = STORE.getState()
            val scheme = state.getColorSchemeStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showColorSchemeEditor(call, state, scheme)
            }
        }
        post<ColorSchemeRoutes.Preview> { preview ->
            logger.info { "Get preview for color scheme ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val scheme = parseColorScheme(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showColorSchemeEditor(call, state, scheme)
            }
        }
        post<ColorSchemeRoutes.Update> { update ->
            logger.info { "Update color scheme ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val scheme = parseColorScheme(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateColorScheme(scheme))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllColorSchemes(
    call: ApplicationCall,
    state: State,
    sort: SortColorScheme,
) {
    val schemes = state.sortColorSchemes(sort)
    val createLink = call.application.href(ColorSchemeRoutes.New())

    simpleHtml("ColorSchemes") {
        field("Count", schemes.size)
        showSortTableLinks(call, SortColorScheme.entries, ColorSchemeRoutes(), ColorSchemeRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"1.Color" }
                th { +"2.Color" }
            }
            schemes.forEach { scheme ->
                tr {
                    tdLink(call, state, scheme)
                    tdEnum(scheme.data.color0())
                    tdEnum(scheme.data.color1())
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showColorSchemeDetails(
    call: ApplicationCall,
    state: State,
    scheme: ColorScheme,
) {
    val backLink = call.application.href(ColorSchemeRoutes.All())
    val deleteLink = call.application.href(ColorSchemeRoutes.Delete(scheme.id))
    val editLink = call.application.href(ColorSchemeRoutes.Edit(scheme.id))

    simpleHtmlDetails(scheme) {
        showColorScheme(call, state, scheme)

        action(editLink, "Edit")

        if (state.canDeleteColorScheme(scheme.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showColorSchemeEditor(
    call: ApplicationCall,
    state: State,
    scheme: ColorScheme,
) {
    val backLink = href(call, scheme.id)
    val previewLink = call.application.href(ColorSchemeRoutes.Preview(scheme.id))
    val updateLink = call.application.href(ColorSchemeRoutes.Update(scheme.id))

    simpleHtmlEditor(scheme) {
        formWithPreview(previewLink, updateLink, backLink) {
            editColorScheme(state, scheme)
        }
    }
}
