package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editRealm
import at.orchaldir.gm.app.html.realm.parseRealm
import at.orchaldir.gm.app.html.realm.showRealm
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
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
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

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
                    createEndDateColumn(call, state, "End"),
                    createAgeColumn(state),
                    createVitalColumn(call, state),
                    createIdColumn(call, state, "Capital") { it.capital.current },
                    createIdColumn(call, state, "Owner") { it.owner.current },
                    createIdColumn(call, state, "Currency") { it.currency.current },
                    createIdColumn(call, state, "Legal Code") { it.legalCode.current },
                    createPopulationColumn(),
                    countColumnForId("Towns", state::countOwnedTowns),
                ),
            ) {
                showCreatorCount(call, state, it, "Founders")
            }
        }
        get<RealmRoutes.Details> { details ->
            handleShowElement(details.id, RealmRoutes(), HtmlBlockTag::showRealm)
        }
        get<RealmRoutes.New> {
            handleCreateElement(STORE.getState().getRealmStorage()) { id ->
                RealmRoutes.Edit(id)
            }
        }
        get<RealmRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, RealmRoutes.All())
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
