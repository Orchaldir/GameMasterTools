package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editSettlement
import at.orchaldir.gm.app.html.realm.parseSettlement
import at.orchaldir.gm.app.html.realm.showSettlement
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.SETTLEMENT_TYPE
import at.orchaldir.gm.core.model.realm.SettlementId
import at.orchaldir.gm.core.model.util.SortSettlement
import at.orchaldir.gm.core.selector.character.countResidents
import at.orchaldir.gm.core.selector.realm.getDistricts
import at.orchaldir.gm.core.selector.util.sortSettlements
import at.orchaldir.gm.core.selector.world.countBuildings
import at.orchaldir.gm.core.selector.world.getCurrentSettlementMap
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$SETTLEMENT_TYPE")
class SettlementRoutes : Routes<SettlementId, SortSettlement> {
    @Resource("all")
    class All(
        val sort: SortSettlement = SortSettlement.Name,
        val parent: SettlementRoutes = SettlementRoutes(),
    )

    @Resource("details")
    class Details(val id: SettlementId, val parent: SettlementRoutes = SettlementRoutes())

    @Resource("new")
    class New(val parent: SettlementRoutes = SettlementRoutes())

    @Resource("delete")
    class Delete(val id: SettlementId, val parent: SettlementRoutes = SettlementRoutes())

    @Resource("edit")
    class Edit(val id: SettlementId, val parent: SettlementRoutes = SettlementRoutes())

    @Resource("preview")
    class Preview(val id: SettlementId, val parent: SettlementRoutes = SettlementRoutes())

    @Resource("update")
    class Update(val id: SettlementId, val parent: SettlementRoutes = SettlementRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortSettlement) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: SettlementId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: SettlementId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: SettlementId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: SettlementId) = call.application.href(Update(id))
}

fun Application.configureSettlementRouting() {
    routing {
        get<SettlementRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                SettlementRoutes(),
                state.sortSettlements(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    createCreatorColumn(call, state, "Founder"),
                    createStartDateColumn(call, state, "Founding"),
                    createEndDateColumn(call, state),
                    createAgeColumn(state),
                    createVitalColumn(call, state, true),
                    createIdColumn(call, state, "Owner") { it.owner.current },
                    createIdColumn(call, state, "Map") { state.getCurrentSettlementMap(it.id)?.id },
                    createAreaColumn(state),
                    createPopulationColumn(),
                    createPopulationDensityColumn(state),
                    createRacesOfPopulationColumn(call, state),
                    createCulturesOfPopulationColumn(call, state),
                    countColumnForId("Characters", state::countResidents),
                    countColumnForId("Buildings", state::countBuildings),
                    createEconomyColumn(),
                    countCollectionColumn("Districts") { state.getDistricts(it.id) },
                ),
            ) {
                showCreatorCount(call, state, it, "Founders")
            }
        }
        get<SettlementRoutes.Details> { details ->
            handleShowElement(details.id, SettlementRoutes(), HtmlBlockTag::showSettlement)
        }
        get<SettlementRoutes.New> {
            handleCreateElement(SettlementRoutes(), STORE.getState().getSettlementStorage())
        }
        get<SettlementRoutes.Delete> { delete ->
            handleDeleteElement(SettlementRoutes(), delete.id)
        }
        get<SettlementRoutes.Edit> { edit ->
            handleEditElement(edit.id, SettlementRoutes(), HtmlBlockTag::editSettlement)
        }
        post<SettlementRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, SettlementRoutes(), ::parseSettlement, HtmlBlockTag::editSettlement)
        }
        post<SettlementRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseSettlement)
        }
    }
}
