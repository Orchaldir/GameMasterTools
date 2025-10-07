package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editGod
import at.orchaldir.gm.app.html.religion.parseGod
import at.orchaldir.gm.app.html.religion.showGod
import at.orchaldir.gm.app.html.util.showAuthenticity
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.Column.Companion.tdColumn
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GOD_TYPE
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.util.SortGod
import at.orchaldir.gm.core.selector.religion.getPantheonsContaining
import at.orchaldir.gm.core.selector.util.sortGods
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

@Resource("/$GOD_TYPE")
class GodRoutes : Routes<GodId,SortGod> {
    @Resource("all")
    class All(
        val sort: SortGod = SortGod.Name,
        val parent: GodRoutes = GodRoutes(),
    )

    @Resource("details")
    class Details(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("new")
    class New(val parent: GodRoutes = GodRoutes())

    @Resource("delete")
    class Delete(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("edit")
    class Edit(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("preview")
    class Preview(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("update")
    class Update(val id: GodId, val parent: GodRoutes = GodRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortGod) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: GodId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: GodId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureGodRouting() {
    routing {
        get<GodRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                GodRoutes(),
                state.sortGods(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Title") { tdString(it.title) },
                    Column("Pantheons") {
                        val pantheons = state.getPantheonsContaining(it.id)
                            .sortedBy { it.name.text }
                        tdLinks(call, state, pantheons)
                     },
                    Column("Gender") { tdEnum(it.gender) },
                    Column("Personality") {
                        val personality = state.getPersonalityTraitStorage()
                            .get(it.personality)
                            .sortedBy { it.name.text }
                        tdLinks(call, state, personality)
                    },
                    Column("Domains") {
                        val domains = state.getDomainStorage()
                            .get(it.domains)
                            .sortedBy { it.name.text }
                        tdLinks(call, state, domains)
                    },
                    tdColumn("Believers") { showAuthenticity(call, state, it.authenticity, false) },
                    Column("Believers") { tdBelievers(state.getCharacterStorage(), it.id) },
                    Column("Organization") { tdBelievers(state.getOrganizationStorage(), it.id) },
                ),
            ) {
                showDomainCount(call, state, it)
                showPersonalityCountForGods(call, state, it)
            }
        }
        get<GodRoutes.Details> { details ->
            handleShowElement(details.id, GodRoutes(), HtmlBlockTag::showGod)
        }
        get<GodRoutes.New> {
            handleCreateElement(STORE.getState().getGodStorage()) { id ->
                GodRoutes.Edit(id)
            }
        }
        get<GodRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, GodRoutes.All())
        }
        get<GodRoutes.Edit> { edit ->
            logger.info { "Get editor for god ${edit.id.value}" }

            val state = STORE.getState()
            val god = state.getGodStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showGodEditor(call, state, god)
            }
        }
        post<GodRoutes.Preview> { preview ->
            logger.info { "Get preview for god ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val god = parseGod(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showGodEditor(call, state, god)
            }
        }
        post<GodRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseGod)
        }
    }
}

private fun HTML.showGodEditor(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    val backLink = href(call, god.id)
    val previewLink = call.application.href(GodRoutes.Preview(god.id))
    val updateLink = call.application.href(GodRoutes.Update(god.id))

    simpleHtmlEditor(god) {
        formWithPreview(previewLink, updateLink, backLink) {
            editGod(call, state, god)
        }
    }
}

