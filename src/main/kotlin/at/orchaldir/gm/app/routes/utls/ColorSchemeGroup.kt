package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.tdInlineIds
import at.orchaldir.gm.app.html.util.color.editColorSchemeGroup
import at.orchaldir.gm.app.html.util.color.parseColorSchemeGroup
import at.orchaldir.gm.app.html.util.color.showColorSchemeGroup
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortColorSchemeGroup
import at.orchaldir.gm.core.model.util.render.COLOR_SCHEME_GROUP_TYPE
import at.orchaldir.gm.core.model.util.render.ColorSchemeGroupId
import at.orchaldir.gm.core.selector.util.sortColorSchemeGroups
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$COLOR_SCHEME_GROUP_TYPE")
class ColorSchemeGroupRoutes : Routes<ColorSchemeGroupId, SortColorSchemeGroup> {
    @Resource("all")
    class All(
        val sort: SortColorSchemeGroup = SortColorSchemeGroup.Name,
        val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes(),
    )

    @Resource("details")
    class Details(val id: ColorSchemeGroupId, val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes())

    @Resource("new")
    class New(val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes())

    @Resource("delete")
    class Delete(val id: ColorSchemeGroupId, val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes())

    @Resource("edit")
    class Edit(val id: ColorSchemeGroupId, val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes())

    @Resource("preview")
    class Preview(val id: ColorSchemeGroupId, val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes())

    @Resource("update")
    class Update(val id: ColorSchemeGroupId, val parent: ColorSchemeGroupRoutes = ColorSchemeGroupRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortColorSchemeGroup) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ColorSchemeGroupId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ColorSchemeGroupId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: ColorSchemeGroupId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: ColorSchemeGroupId) = call.application.href(Update(id))
}

fun Application.configureColorSchemeGroupRouting() {
    routing {
        get<ColorSchemeGroupRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ColorSchemeGroupRoutes(),
                state.sortColorSchemeGroups(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Equipment") { tdInlineIds(call, state, it.schemes, 5) },
                ),
            )
        }
        get<ColorSchemeGroupRoutes.Details> { details ->
            handleShowElement(details.id, ColorSchemeGroupRoutes(), HtmlBlockTag::showColorSchemeGroup)
        }
        get<ColorSchemeGroupRoutes.New> {
            handleCreateElement(ColorSchemeGroupRoutes(), STORE.getState().getColorSchemeGroupStorage())
        }
        get<ColorSchemeGroupRoutes.Delete> { delete ->
            handleDeleteElement(ColorSchemeGroupRoutes(), delete.id)
        }
        get<ColorSchemeGroupRoutes.Edit> { edit ->
            handleEditElement(edit.id, ColorSchemeGroupRoutes(), HtmlBlockTag::editColorSchemeGroup)
        }
        post<ColorSchemeGroupRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                ColorSchemeGroupRoutes(),
                ::parseColorSchemeGroup,
                HtmlBlockTag::editColorSchemeGroup
            )
        }
        post<ColorSchemeGroupRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseColorSchemeGroup)
        }
    }
}
