package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.realm.displayWarStatus
import at.orchaldir.gm.app.html.realm.editWar
import at.orchaldir.gm.app.html.realm.parseWar
import at.orchaldir.gm.app.html.realm.showWar
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.WAR_TYPE
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarId
import at.orchaldir.gm.core.model.util.SortWar
import at.orchaldir.gm.core.selector.realm.countBattles
import at.orchaldir.gm.core.selector.util.sortWars
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$WAR_TYPE")
class WarRoutes : Routes<WarId, SortWar> {
    @Resource("all")
    class All(
        val sort: SortWar = SortWar.Name,
        val parent: WarRoutes = WarRoutes(),
    )

    @Resource("details")
    class Details(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("new")
    class New(val parent: WarRoutes = WarRoutes())

    @Resource("delete")
    class Delete(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("edit")
    class Edit(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("preview")
    class Preview(val id: WarId, val parent: WarRoutes = WarRoutes())

    @Resource("update")
    class Update(val id: WarId, val parent: WarRoutes = WarRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortWar) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: WarId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: WarId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: WarId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: WarId) = call.application.href(Update(id))
}

fun Application.configureWarRouting() {
    routing {
        get<WarRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                WarRoutes(),
                state.sortWars(all.sort),
                listOf<Column<War>>(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state, "Start"),
                    createEndDateColumn(call, state, "End"),
                    createAgeColumn(state, "Years"),
                    tdColumn("Status") { displayWarStatus(call, state, it) },
                    createIdColumn(call, state, "Treaty") { it.status.treaty() },
                    countColumnForId("Battles", state::countBattles),
                ) + createDestroyedColumns(state),
            )
        }
        get<WarRoutes.Details> { details ->
            handleShowElement(details.id, WarRoutes(), HtmlBlockTag::showWar)
        }
        get<WarRoutes.New> {
            handleCreateElement(WarRoutes(), STORE.getState().getWarStorage())
        }
        get<WarRoutes.Delete> { delete ->
            handleDeleteElement(WarRoutes(), delete.id)
        }
        get<WarRoutes.Edit> { edit ->
            handleEditElement(edit.id, WarRoutes(), HtmlBlockTag::editWar)
        }
        post<WarRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, WarRoutes(), ::parseWar, HtmlBlockTag::editWar)
        }
        post<WarRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseWar)
        }
    }
}
