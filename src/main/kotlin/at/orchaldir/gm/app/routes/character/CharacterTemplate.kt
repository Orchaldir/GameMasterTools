package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.editCharacterTemplate
import at.orchaldir.gm.app.html.character.parseCharacterTemplate
import at.orchaldir.gm.app.html.character.showCharacterTemplate
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CreateCharacterTemplate
import at.orchaldir.gm.core.action.DeleteCharacterTemplate
import at.orchaldir.gm.core.action.UpdateCharacterTemplate
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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$CHARACTER_TEMPLATE_TYPE")
class CharacterTemplateRoutes {
    @Resource("all")
    class All(
        val sort: SortCharacterTemplate = SortCharacterTemplate.Name,
        val parent: CharacterTemplateRoutes = CharacterTemplateRoutes(),
    )

    @Resource("details")
    class Details(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("new")
    class New(val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("delete")
    class Delete(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("edit")
    class Edit(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("preview")
    class Preview(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())

    @Resource("update")
    class Update(val id: CharacterTemplateId, val parent: CharacterTemplateRoutes = CharacterTemplateRoutes())
}

fun Application.configureCharacterTemplateRouting() {
    routing {
        get<CharacterTemplateRoutes.All> { all ->
            logger.info { "Get all templates" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllCharacterTemplates(call, STORE.getState(), all.sort)
            }
        }
        get<CharacterTemplateRoutes.Details> { details ->
            logger.info { "Get details of template ${details.id.value}" }

            val state = STORE.getState()
            val template = state.getCharacterTemplateStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCharacterTemplateDetails(call, state, template)
            }
        }
        get<CharacterTemplateRoutes.New> {
            logger.info { "Add new template" }

            STORE.dispatch(CreateCharacterTemplate)

            call.respondRedirect(
                call.application.href(
                    CharacterTemplateRoutes.Edit(
                        STORE.getState().getCharacterTemplateStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<CharacterTemplateRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteCharacterTemplate(delete.id), CharacterTemplateRoutes())
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
            logger.info { "Update template ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val template = parseCharacterTemplate(state, formParameters, update.id)

            STORE.dispatch(UpdateCharacterTemplate(template))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllCharacterTemplates(
    call: ApplicationCall,
    state: State,
    sort: SortCharacterTemplate,
) {
    val templates = state.sortCharacterTemplates(sort)
    val createLink = call.application.href(CharacterTemplateRoutes.New())

    simpleHtml("Character Templates") {
        field("Count", templates.size)
        showSortTableLinks(call, SortCharacterTemplate.entries, CharacterTemplateRoutes(), CharacterTemplateRoutes::All)

        table {
            tr {
                th { +"Name" }
            }
            templates.forEach { template ->
                tr {
                    tdLink(call, state, template)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showCharacterTemplateDetails(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) {
    val backLink = call.application.href(CharacterTemplateRoutes.All())
    val deleteLink = call.application.href(CharacterTemplateRoutes.Delete(template.id))
    val editLink = call.application.href(CharacterTemplateRoutes.Edit(template.id))

    simpleHtmlDetails(template) {
        showCharacterTemplate(call, state, template)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
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
            editCharacterTemplate(state, template)
        }
    }
}

