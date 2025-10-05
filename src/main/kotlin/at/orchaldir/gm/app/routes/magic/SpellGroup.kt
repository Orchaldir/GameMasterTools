package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.magic.editSpellGroup
import at.orchaldir.gm.app.html.magic.parseSpellGroup
import at.orchaldir.gm.app.html.magic.showSpellGroup
import at.orchaldir.gm.app.html.simpleHtmlEditor
import at.orchaldir.gm.app.routes.*
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
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$SPELL_GROUP_TYPE")
class SpellGroupRoutes : Routes<SpellGroupId,SortSpellGroup> {
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
    override fun all(call: ApplicationCall, sort: SortSpellGroup) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: SpellGroupId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: SpellGroupId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureSpellGroupRouting() {
    routing {
        get<SpellGroupRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                SpellGroupRoutes(),
                state.sortSpellGroups(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createSkipZeroColumnFromCollection("Spells", SpellGroup::spells),
                ),
            )
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

