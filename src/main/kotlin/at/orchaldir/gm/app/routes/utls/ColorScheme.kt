package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.util.color.editColorScheme
import at.orchaldir.gm.app.html.util.color.parseColorScheme
import at.orchaldir.gm.app.html.util.color.showColorScheme
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortColorScheme
import at.orchaldir.gm.core.model.util.render.COLOR_SCHEME_TYPE
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.util.sortColorSchemes
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$COLOR_SCHEME_TYPE")
class ColorSchemeRoutes : Routes<ColorSchemeId, SortColorScheme> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortColorScheme) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ColorSchemeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ColorSchemeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureColorSchemeRouting() {
    routing {
        get<ColorSchemeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ColorSchemeRoutes(),
                state.sortColorSchemes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("1.Color") { showOptionalColor(it.data.color0()) },
                    tdColumn("2.Color") { showOptionalColor(it.data.color1()) },
                    Column("Equipment") { tdSkipZero(state.countEquipment(it.id)) },
                ),
            )
        }
        get<ColorSchemeRoutes.Details> { details ->
            handleShowElement(details.id, ColorSchemeRoutes(), HtmlBlockTag::showColorScheme)
        }
        get<ColorSchemeRoutes.New> {
            handleCreateElement(STORE.getState().getColorSchemeStorage()) { id ->
                ColorSchemeRoutes.Edit(id)
            }
        }
        get<ColorSchemeRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ColorSchemeRoutes.All())
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
            val scheme = parseColorScheme(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showColorSchemeEditor(call, state, scheme)
            }
        }
        post<ColorSchemeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseColorScheme)
        }
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
