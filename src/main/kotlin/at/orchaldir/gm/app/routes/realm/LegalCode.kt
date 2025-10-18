package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editLegalCode
import at.orchaldir.gm.app.html.realm.parseLegalCode
import at.orchaldir.gm.app.html.realm.showLegalCode
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LEGAL_CODE_TYPE
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.util.SortLegalCode
import at.orchaldir.gm.core.selector.realm.countRealmsWithLegalCodeAtAnyTime
import at.orchaldir.gm.core.selector.util.sortLegalCodes
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

@Resource("/$LEGAL_CODE_TYPE")
class LegalCodeRoutes : Routes<LegalCodeId, SortLegalCode> {
    @Resource("all")
    class All(
        val sort: SortLegalCode = SortLegalCode.Name,
        val parent: LegalCodeRoutes = LegalCodeRoutes(),
    )

    @Resource("details")
    class Details(val id: LegalCodeId, val parent: LegalCodeRoutes = LegalCodeRoutes())

    @Resource("new")
    class New(val parent: LegalCodeRoutes = LegalCodeRoutes())

    @Resource("delete")
    class Delete(val id: LegalCodeId, val parent: LegalCodeRoutes = LegalCodeRoutes())

    @Resource("edit")
    class Edit(val id: LegalCodeId, val parent: LegalCodeRoutes = LegalCodeRoutes())

    @Resource("preview")
    class Preview(val id: LegalCodeId, val parent: LegalCodeRoutes = LegalCodeRoutes())

    @Resource("update")
    class Update(val id: LegalCodeId, val parent: LegalCodeRoutes = LegalCodeRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortLegalCode) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: LegalCodeId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: LegalCodeId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureLegalCodeRouting() {
    routing {
        get<LegalCodeRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                LegalCodeRoutes(),
                state.sortLegalCodes(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createCreatorColumn(call, state),
                    createSkipZeroColumnForId("Population", state::countRealmsWithLegalCodeAtAnyTime),
                ),
            ) {
                showCreatorCount(call, state, it, "Creators")
            }
        }
        get<LegalCodeRoutes.Details> { details ->
            handleShowElement(details.id, LegalCodeRoutes(), HtmlBlockTag::showLegalCode)
        }
        get<LegalCodeRoutes.New> {
            handleCreateElement(STORE.getState().getLegalCodeStorage()) { id ->
                LegalCodeRoutes.Edit(id)
            }
        }
        get<LegalCodeRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, LegalCodeRoutes.All())
        }
        get<LegalCodeRoutes.Edit> { edit ->
            logger.info { "Get editor for legal code ${edit.id.value}" }

            val state = STORE.getState()
            val code = state.getLegalCodeStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLegalCodeEditor(call, state, code)
            }
        }
        post<LegalCodeRoutes.Preview> { preview ->
            logger.info { "Get preview for legal code ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val code = parseLegalCode(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLegalCodeEditor(call, state, code)
            }
        }
        post<LegalCodeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseLegalCode)
        }
    }
}

private fun HTML.showLegalCodeEditor(
    call: ApplicationCall,
    state: State,
    code: LegalCode,
) {
    val backLink = href(call, code.id)
    val previewLink = call.application.href(LegalCodeRoutes.Preview(code.id))
    val updateLink = call.application.href(LegalCodeRoutes.Update(code.id))

    simpleHtmlEditor(code) {
        formWithPreview(previewLink, updateLink, backLink) {
            editLegalCode(state, code)
        }
    }
}
