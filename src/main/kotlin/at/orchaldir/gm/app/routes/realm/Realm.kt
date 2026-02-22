package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editRealm
import at.orchaldir.gm.app.html.realm.parseRealm
import at.orchaldir.gm.app.html.realm.showRealm
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.REALM_TYPE
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.util.SortRealm
import at.orchaldir.gm.core.selector.realm.countOwnedSettlements
import at.orchaldir.gm.core.selector.util.sortRealms
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$REALM_TYPE")
class RealmRoutes : Routes<RealmId, SortRealm> {
    @Resource("all")
    class All(
        val sort: SortRealm = SortRealm.Name,
        val parent: RealmRoutes = RealmRoutes(),
    )

    @Resource("details")
    class Details(val id: RealmId, val parent: RealmRoutes = RealmRoutes())

    @Resource("new")
    class New(val parent: RealmRoutes = RealmRoutes())

    @Resource("delete")
    class Delete(val id: RealmId, val parent: RealmRoutes = RealmRoutes())

    @Resource("edit")
    class Edit(val id: RealmId, val parent: RealmRoutes = RealmRoutes())

    @Resource("preview")
    class Preview(val id: RealmId, val parent: RealmRoutes = RealmRoutes())

    @Resource("update")
    class Update(val id: RealmId, val parent: RealmRoutes = RealmRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortRealm) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: RealmId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RealmId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: RealmId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RealmId) = call.application.href(Update(id))
}

fun Application.configureRealmRouting() {
    routing {
        get<RealmRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RealmRoutes(),
                state.sortRealms(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createCreatorColumn(call, state, "Founder"),
                    createStartDateColumn(call, state, "Founding"),
                    createEndDateColumn(call, state),
                    createAgeColumn(state),
                    createVitalColumn(call, state, true),
                    createIdColumn(call, state, "Capital") { it.capital.current },
                    createIdColumn(call, state, "Owner") { it.owner.current },
                    createIdColumn(call, state, "Currency") { it.currency.current },
                    createIdColumn(call, state, "Legal Code") { it.legalCode.current },
                    createAreaColumn(state),
                    createPopulationColumn(),
                    createPopulationDensityColumn(state),
                    createRacesOfPopulationColumn(call, state),
                    createCulturesOfPopulationColumn(call, state),
                    createEconomyColumn(),
                    countColumnForId("Settlements", state::countOwnedSettlements),
                ),
            ) {
                showCreatorCount(call, state, it, "Founders")
            }
        }
        get<RealmRoutes.Details> { details ->
            handleShowElement(details.id, RealmRoutes(), HtmlBlockTag::showRealm)
        }
        get<RealmRoutes.New> {
            handleCreateElement(RealmRoutes(), STORE.getState().getRealmStorage())
        }
        get<RealmRoutes.Delete> { delete ->
            handleDeleteElement(RealmRoutes(), delete.id)
        }
        get<RealmRoutes.Edit> { edit ->
            handleEditElement(edit.id, RealmRoutes(), HtmlBlockTag::editRealm)
        }
        post<RealmRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, RealmRoutes(), ::parseRealm, HtmlBlockTag::editRealm)
        }
        post<RealmRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRealm)
        }
    }
}
