package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.editSpellGroup
import at.orchaldir.gm.app.html.magic.parseSpellGroup
import at.orchaldir.gm.app.html.magic.showSpellGroup
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SPELL_GROUP_TYPE
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.model.util.SortSpellGroup
import at.orchaldir.gm.core.selector.util.sortSpellGroups
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
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$SPELL_GROUP_TYPE")
class SpellGroupRoutes : Routes<SpellGroupId> {
    @Resource("all")
    class All(
        val sort: SortSpellGroup = SortSpellGroup.Name,
        val parent: SpellGroupRoutes = SpellGroupRoutes(),
    )

    @Resource("details")
    class Details(val id: SpellGroupId, val parent: SpellGroupRoutes = SpellGroupRoutes())

    @Resource("new")
    class New(val parent: SpellGroupRoutes = SpellGroupRoutes())

    @Resource("delete")
    class Delete(val id: SpellGroupId, val parent: SpellGroupRoutes = SpellGroupRoutes())

    @Resource("edit")
    class Edit(val id: SpellGroupId, val parent: SpellGroupRoutes = SpellGroupRoutes())

    @Resource("preview")
    class Preview(val id: SpellGroupId, val parent: SpellGroupRoutes = SpellGroupRoutes())

    @Resource("update")
    class Update(val id: SpellGroupId, val parent: SpellGroupRoutes = SpellGroupRoutes())


    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: SpellGroupId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: SpellGroupId) = call.application.href(Edit(id))
}

fun Application.configureSpellGroupRouting() {
    routing {
        get<SpellGroupRoutes.All> { all ->
            logger.info { "Get all groups" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllSpellGroups(call, STORE.getState(), all.sort)
            }
        }
        get<SpellGroupRoutes.Details> { details ->
            handleShowElement(details.id, SpellGroupRoutes(), HtmlBlockTag::showSpellGroup)
        }
        get<SpellGroupRoutes.New> {
            handleCreateElement(STORE.getState().getSpellGroupStorage()) { id ->
                SpellGroupRoutes.Edit(id)
            }
        }
        get<SpellGroupRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, SpellGroupRoutes.All())
        }
        get<SpellGroupRoutes.Edit> { edit ->
            logger.info { "Get editor for group ${edit.id.value}" }

            val state = STORE.getState()
            val group = state.getSpellGroupStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showSpellGroupEditor(call, state, group)
            }
        }
        post<SpellGroupRoutes.Preview> { preview ->
            logger.info { "Get preview for group ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val group = parseSpellGroup(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showSpellGroupEditor(call, state, group)
            }
        }
        post<SpellGroupRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseSpellGroup)
        }
    }
}

private fun HTML.showAllSpellGroups(
    call: ApplicationCall,
    state: State,
    sort: SortSpellGroup,
) {
    val groups = state.sortSpellGroups(sort)
    val createLink = call.application.href(SpellGroupRoutes.New())

    simpleHtml("Spell Groups") {
        field("Count", groups.size)
        showSortTableLinks(call, SortSpellGroup.entries, SpellGroupRoutes(), SpellGroupRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Spells" }
            }
            groups.forEach { group ->
                tr {
                    tdLink(call, state, group)
                    tdSkipZero(group.spells)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showSpellGroupEditor(
    call: ApplicationCall,
    state: State,
    group: SpellGroup,
) {
    val backLink = href(call, group.id)
    val previewLink = call.application.href(SpellGroupRoutes.Preview(group.id))
    val updateLink = call.application.href(SpellGroupRoutes.Update(group.id))

    simpleHtmlEditor(group) {
        formWithPreview(previewLink, updateLink, backLink) {
            editSpellGroup(state, group)
        }
    }
}

