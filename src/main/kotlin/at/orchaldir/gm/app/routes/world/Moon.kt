package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.world.editMoon
import at.orchaldir.gm.app.html.world.parseMoon
import at.orchaldir.gm.app.html.world.showMoon
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortMoon
import at.orchaldir.gm.core.model.world.moon.MOON_TYPE
import at.orchaldir.gm.core.model.world.moon.MoonId
import at.orchaldir.gm.core.selector.util.sortMoons
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$MOON_TYPE")
class MoonRoutes : Routes<MoonId, SortMoon> {
    @Resource("all")
    class All(
        val sort: SortMoon = SortMoon.Name,
        val parent: MoonRoutes = MoonRoutes(),
    )

    @Resource("details")
    class Details(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("new")
    class New(val parent: MoonRoutes = MoonRoutes())

    @Resource("delete")
    class Delete(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("edit")
    class Edit(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("preview")
    class Preview(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    @Resource("update")
    class Update(val id: MoonId, val parent: MoonRoutes = MoonRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMoon) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MoonId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MoonId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: MoonId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: MoonId) = call.application.href(Update(id))
}

fun Application.configureMoonRouting() {
    routing {
        get<MoonRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MoonRoutes(),
                state.sortMoons(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    createVitalColumn(call, state),
                    createPositionColumn(call, state),
                    tdColumn("Duration") { +"${it.getCycle()} days" },
                    tdColumn("Color") { showOptionalColor(it.color) },
                    Column(listOf("Associated", "Plane")) { tdLink(call, state, it.plane) },
                ),
            )
        }
        get<MoonRoutes.Details> { details ->
            handleShowElement(details.id, MoonRoutes(), HtmlBlockTag::showMoon)
        }
        get<MoonRoutes.New> {
            handleCreateElement(MoonRoutes(), STORE.getState().getMoonStorage())
        }
        get<MoonRoutes.Delete> { delete ->
            handleDeleteElement(MoonRoutes(), delete.id)
        }
        get<MoonRoutes.Edit> { edit ->
            handleEditElement(edit.id, MoonRoutes(), HtmlBlockTag::editMoon)
        }
        post<MoonRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, MoonRoutes(), ::parseMoon, HtmlBlockTag::editMoon)
        }
        post<MoonRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMoon)
        }
    }
}

