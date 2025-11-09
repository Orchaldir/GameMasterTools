package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.showOptionalColor
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.util.color.editColorScheme
import at.orchaldir.gm.app.html.util.color.parseColorScheme
import at.orchaldir.gm.app.html.util.color.showColorScheme
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortColorScheme
import at.orchaldir.gm.core.model.util.render.COLOR_SCHEME_TYPE
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.selector.item.countEquipment
import at.orchaldir.gm.core.selector.util.sortColorSchemes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: ColorSchemeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: ColorSchemeId) = call.application.href(Update(id))
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
            handleCreateElement(ColorSchemeRoutes(), STORE.getState().getColorSchemeStorage())
        }
        get<ColorSchemeRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ColorSchemeRoutes())
        }
        get<ColorSchemeRoutes.Edit> { edit ->
            handleEditElement(edit.id, ColorSchemeRoutes(), HtmlBlockTag::editColorScheme)
        }
        post<ColorSchemeRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, ColorSchemeRoutes(), ::parseColorScheme, HtmlBlockTag::editColorScheme)
        }
        post<ColorSchemeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseColorScheme)
        }
    }
}
