package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createDestroyedColumns
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.realm.editBattle
import at.orchaldir.gm.app.html.realm.parseBattle
import at.orchaldir.gm.app.html.realm.showBattle
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.BATTLE_TYPE
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.util.SortBattle
import at.orchaldir.gm.core.selector.util.sortBattles
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$BATTLE_TYPE")
class BattleRoutes : Routes<BattleId, SortBattle> {
    @Resource("all")
    class All(
        val sort: SortBattle = SortBattle.Name,
        val parent: BattleRoutes = BattleRoutes(),
    )

    @Resource("details")
    class Details(val id: BattleId, val parent: BattleRoutes = BattleRoutes())

    @Resource("new")
    class New(val parent: BattleRoutes = BattleRoutes())

    @Resource("delete")
    class Delete(val id: BattleId, val parent: BattleRoutes = BattleRoutes())

    @Resource("edit")
    class Edit(val id: BattleId, val parent: BattleRoutes = BattleRoutes())

    @Resource("preview")
    class Preview(val id: BattleId, val parent: BattleRoutes = BattleRoutes())

    @Resource("update")
    class Update(val id: BattleId, val parent: BattleRoutes = BattleRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortBattle) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: BattleId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: BattleId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: BattleId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: BattleId) = call.application.href(Update(id))
}

fun Application.configureBattleRouting() {
    routing {
        get<BattleRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                BattleRoutes(),
                state.sortBattles(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    countCollectionColumn("Participants", Battle::participants)
                ) + createDestroyedColumns(state),
            )
        }
        get<BattleRoutes.Details> { details ->
            handleShowElement(details.id, BattleRoutes(), HtmlBlockTag::showBattle)
        }
        get<BattleRoutes.New> {
            handleCreateElement(BattleRoutes(), STORE.getState().getBattleStorage())
        }
        get<BattleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, BattleRoutes.All())
        }
        get<BattleRoutes.Edit> { edit ->
            handleEditElement(edit.id, BattleRoutes(), HtmlBlockTag::editBattle)
        }
        post<BattleRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, BattleRoutes(), ::parseBattle, HtmlBlockTag::editBattle)
        }
        post<BattleRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseBattle)
        }
    }
}
