package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editGod
import at.orchaldir.gm.app.html.religion.parseGod
import at.orchaldir.gm.app.html.religion.showGod
import at.orchaldir.gm.app.html.util.showAuthenticity
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GOD_TYPE
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.util.SortGod
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.religion.getPantheonsContaining
import at.orchaldir.gm.core.selector.util.getBelievers
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
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.tr
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
            logger.info { "Get all gods" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllGods(call, STORE.getState(), all.sort)
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

private fun HTML.showAllGods(
    call: ApplicationCall,
    state: State,
    sort: SortGod,
) {
    val gods = state.sortGods(sort)
    val createLink = call.application.href(GodRoutes.New())

    simpleHtml("Gods") {
        field("Count", gods.size)
        showSortTableLinks(call, SortGod.entries, GodRoutes())

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Pantheons" }
                th { +"Gender" }
                th { +"Personality" }
                th { +"Domain" }
                th { +"Authenticity" }
                th { +"Believers" }
                th { +"Organizations" }
            }
            gods.forEach { god ->
                tr {
                    val pantheons = state.getPantheonsContaining(god.id)
                        .sortedBy { it.name.text }
                    val personality = state.getPersonalityTraitStorage()
                        .get(god.personality)
                        .sortedBy { it.name.text }
                    val domains = state.getDomainStorage()
                        .get(god.domains)
                        .sortedBy { it.name.text }

                    tdLink(call, state, god)
                    tdString(god.title)
                    tdLinks(call, state, pantheons)
                    tdEnum(god.gender)
                    tdLinks(call, state, personality)
                    tdLinks(call, state, domains)
                    td { showAuthenticity(call, state, god.authenticity, false) }
                    tdSkipZero(getBelievers(state.getCharacterStorage(), god.id))
                    tdSkipZero(getBelievers(state.getOrganizationStorage(), god.id))
                }
            }
        }

        action(createLink, "Add")
        back("/")

        showDomainCount(call, state, state.getGodStorage().getAll())
        showPersonalityCountForGods(call, state, state.getGodStorage().getAll())
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

