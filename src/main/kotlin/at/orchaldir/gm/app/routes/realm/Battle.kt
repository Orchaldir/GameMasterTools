package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editBattle
import at.orchaldir.gm.app.html.realm.parseBattle
import at.orchaldir.gm.app.html.realm.showBattle
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.tdDestroyed
import at.orchaldir.gm.app.html.util.thDestroyed
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.religion.DomainRoutes
import at.orchaldir.gm.core.action.CreateBattle
import at.orchaldir.gm.core.action.DeleteBattle
import at.orchaldir.gm.core.action.DeleteDomain
import at.orchaldir.gm.core.action.UpdateBattle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.BATTLE_TYPE
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.util.SortBattle
import at.orchaldir.gm.core.selector.realm.canDeleteBattle
import at.orchaldir.gm.core.selector.util.sortBattles
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

@Resource("/$BATTLE_TYPE")
class BattleRoutes {
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
}

fun Application.configureBattleRouting() {
    routing {
        get<BattleRoutes.All> { all ->
            logger.info { "Get all battles" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBattles(call, STORE.getState(), all.sort)
            }
        }
        get<BattleRoutes.Details> { details ->
            logger.info { "Get details of battle ${details.id.value}" }

            val state = STORE.getState()
            val battle = state.getBattleStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBattleDetails(call, state, battle)
            }
        }
        get<BattleRoutes.New> {
            logger.info { "Add new battle" }

            STORE.dispatch(CreateBattle)

            call.respondRedirect(
                call.application.href(
                    BattleRoutes.Edit(
                        STORE.getState().getBattleStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<BattleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteBattle(delete.id), BattleRoutes())
        }
        get<BattleRoutes.Edit> { edit ->
            logger.info { "Get editor for battle ${edit.id.value}" }

            val state = STORE.getState()
            val battle = state.getBattleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBattleEditor(call, state, battle)
            }
        }
        post<BattleRoutes.Preview> { preview ->
            logger.info { "Get preview for battle ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val battle = parseBattle(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBattleEditor(call, state, battle)
            }
        }
        post<BattleRoutes.Update> { update ->
            logger.info { "Update battle ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val battle = parseBattle(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateBattle(battle))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllBattles(
    call: ApplicationCall,
    state: State,
    sort: SortBattle,
) {
    val battles = state.sortBattles(sort)
    val createLink = call.application.href(BattleRoutes.New())

    simpleHtml("Battles") {
        field("Count", battles.size)
        showSortTableLinks(call, SortBattle.entries, BattleRoutes(), BattleRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Participants" }
                thDestroyed()
            }
            battles.forEach { battle ->
                tr {
                    tdLink(call, state, battle)
                    td { showOptionalDate(call, state, battle.date) }
                    tdSkipZero(battle.participants)
                    tdDestroyed(state, battle.id)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showBattleDetails(
    call: ApplicationCall,
    state: State,
    battle: Battle,
) {
    val backLink = call.application.href(BattleRoutes.All())
    val deleteLink = call.application.href(BattleRoutes.Delete(battle.id))
    val editLink = call.application.href(BattleRoutes.Edit(battle.id))

    simpleHtmlDetails(battle) {
        showBattle(call, state, battle)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showBattleEditor(
    call: ApplicationCall,
    state: State,
    battle: Battle,
) {
    val backLink = href(call, battle.id)
    val previewLink = call.application.href(BattleRoutes.Preview(battle.id))
    val updateLink = call.application.href(BattleRoutes.Update(battle.id))

    simpleHtmlEditor(battle) {
        formWithPreview(previewLink, updateLink, backLink) {
            editBattle(state, battle)
        }
    }
}
