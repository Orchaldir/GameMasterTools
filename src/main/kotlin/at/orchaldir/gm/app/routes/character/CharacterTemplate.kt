package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.editCharacterTemplate
import at.orchaldir.gm.app.html.character.parseCharacterTemplate
import at.orchaldir.gm.app.html.character.showCharacterTemplate
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CHARACTER_TEMPLATE_TYPE
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.character.CharacterTemplateId
import at.orchaldir.gm.core.model.util.SortCharacterTemplate
import at.orchaldir.gm.core.selector.util.sortCharacterTemplates
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

@Resource("/$CHARACTER_TEMPLATE_TYPE")
class CharacterTemplateRoutes : Routes<CharacterTemplateId, SortCharacterTemplate> {
    @Resource("all")
    class All(
        val sort: SortCharacterTemplate = SortCharacterTemplate.Name,
        val parent: CharacterTemplateRoutes = CharacterTemplateRoutes(),
    )

    @Resource("details")
    class Details(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("new")
    class New(val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("clone")
    class Clone(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("delete")
    class Delete(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("edit")
    class Edit(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("preview")
    class Preview(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("update")
    class Update(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCharacterTemplate) = call.application.href(All(sort))
    override fun clone(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CharacterTemplateId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureCharacterTemplateRouting() {
    routing {
        get<CharacterTemplateRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CharacterTemplateRoutes(),
                state.sortCharacterTemplates(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Race") { tdLink(call, state, it.race) },
                    Column("Culture") { tdLink(call, state, it.culture) },
                    createBeliefColumn(call, state),
                    Column("Uniform") { tdLink(call, state, it.uniform) },
                    createSkipZeroColumn("Cost") { it.statblock.calculateCost(state) },
                ),
            )
        }
        get<CharacterTemplateRoutes.Details> { details ->
            handleShowElement(details.id, CharacterTemplateRoutes(), HtmlBlockTag::showCharacterTemplate)
        }
        get<CharacterTemplateRoutes.New> {
            handleCreateElement(STORE.getState().getCharacterTemplateStorage()) { id ->
                CharacterTemplateRoutes.Edit(id)
            }
        }
        get<CharacterTemplateRoutes.Clone> { clone ->
            handleCloneElement(clone.id) { cloneId ->
                CharacterTemplateRoutes.Edit(cloneId)
            }
        }
        get<CharacterTemplateRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CharacterTemplateRoutes.All())
        }
        get<CharacterTemplateRoutes.Edit> { edit ->
            logger.info { "Get editor for template ${edit.id.value}" }

            val state = STORE.getState()
            val template = state.getCharacterTemplateStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterTemplateEditor(call, state, template)
            }
        }
        post<CharacterTemplateRoutes.Preview> { preview ->
            logger.info { "Get preview for template ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val template = parseCharacterTemplate(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterTemplateEditor(call, state, template)
            }
        }
        post<CharacterTemplateRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCharacterTemplate)
        }
    }
}

private fun HTML.showCharacterTemplateEditor(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val backLink = href(call, template.id)
    val previewLink = call.application.href(CharacterTemplateRoutes.Preview(template.id))
    val updateLink = call.application.href(CharacterTemplateRoutes.Update(template.id))

    simpleHtmlEditor(template) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCharacterTemplate(call, state, template)
        }
    }
}

