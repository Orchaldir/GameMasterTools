package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editRealm
import at.orchaldir.gm.app.html.realm.parseLegalCode
import at.orchaldir.gm.app.html.realm.parseRealm
import at.orchaldir.gm.app.html.realm.showRealm
import at.orchaldir.gm.app.html.util.displayVitalStatus
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.DeleteRealm
import at.orchaldir.gm.core.action.UpdateRealm
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.REALM_TYPE
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.util.SortRealm
import at.orchaldir.gm.core.selector.realm.countOwnedTowns
import at.orchaldir.gm.core.selector.util.sortRealms
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

@Resource("/$REALM_TYPE")
class RealmRoutes {
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
}

fun Application.configureRealmRouting() {
    routing {
        get<RealmRoutes.All> { all ->
            logger.info { "Get all realms" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllRealms(call, STORE.getState(), all.sort)
            }
        }
        get<RealmRoutes.Details> { details ->
            logger.info { "Get details of realm ${details.id.value}" }

            val state = STORE.getState()
            val realm = state.getRealmStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRealmDetails(call, state, realm)
            }
        }
        get<RealmRoutes.New> {
            handleCreateElement(STORE.getState().getRealmStorage()) { id ->
                RealmRoutes.Edit(id)
            }
        }
        get<RealmRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteRealm(delete.id), RealmRoutes())
        }
        get<RealmRoutes.Edit> { edit ->
            logger.info { "Get editor for realm ${edit.id.value}" }

            val state = STORE.getState()
            val realm = state.getRealmStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRealmEditor(call, state, realm)
            }
        }
        post<RealmRoutes.Preview> { preview ->
            logger.info { "Get preview for realm ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val realm = parseRealm(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showRealmEditor(call, state, realm)
            }
        }
        post<RealmRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRealm)
        }
    }
}

private fun HTML.showAllRealms(
    call: ApplicationCall,
    state: State,
    sort: SortRealm,
) {
    val realms = state.sortRealms(sort)
    val createLink = call.application.href(RealmRoutes.New())

    simpleHtml("Realms") {
        field("Count", realms.size)
        showSortTableLinks(call, SortRealm.entries, RealmRoutes(), RealmRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Founder" }
                thMultiLines(listOf("Founding", "Date"))
                thMultiLines(listOf("End", "Date"))
                th { +"Age" }
                th { +"End" }
                th { +"Capital" }
                th { +"Owner" }
                th { +"Currency" }
                th { +"Legal Code" }
                th { +"Population" }
                th { +"Towns" }
            }
            realms.forEach { realm ->
                tr {
                    tdLink(call, state, realm)
                    td { showReference(call, state, realm.founder, false) }
                    td { showOptionalDate(call, state, realm.startDate()) }
                    td { showOptionalDate(call, state, realm.endDate()) }
                    tdSkipZero(realm.getAgeInYears(state))
                    td { displayVitalStatus(call, state, realm.status, false) }
                    tdLink(call, state, realm.capital.current)
                    tdLink(call, state, realm.owner.current)
                    tdLink(call, state, realm.currency.current)
                    tdLink(call, state, realm.legalCode.current)
                    tdSkipZero(realm.population.getTotalPopulation())
                    tdSkipZero(state.countOwnedTowns(realm.id))
                }
            }
        }

        showCreatorCount(call, state, realms, "Founders")

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showRealmDetails(
    call: ApplicationCall,
    state: State,
    realm: Realm,
) {
    val backLink = call.application.href(RealmRoutes.All())
    val deleteLink = call.application.href(RealmRoutes.Delete(realm.id))
    val editLink = call.application.href(RealmRoutes.Edit(realm.id))

    simpleHtmlDetails(realm) {
        showRealm(call, state, realm)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showRealmEditor(
    call: ApplicationCall,
    state: State,
    realm: Realm,
) {
    val backLink = href(call, realm.id)
    val previewLink = call.application.href(RealmRoutes.Preview(realm.id))
    val updateLink = call.application.href(RealmRoutes.Update(realm.id))

    simpleHtmlEditor(realm) {
        formWithPreview(previewLink, updateLink, backLink) {
            editRealm(call, state, realm)
        }
    }
}
