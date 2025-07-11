package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editLegalCode
import at.orchaldir.gm.app.html.realm.parseLegalCode
import at.orchaldir.gm.app.html.realm.showLegalCode
import at.orchaldir.gm.app.html.util.showCreator
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.core.action.CreateLegalCode
import at.orchaldir.gm.core.action.DeleteLegalCode
import at.orchaldir.gm.core.action.UpdateLegalCode
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LEGAL_CODE_TYPE
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.util.SortLegalCode
import at.orchaldir.gm.core.selector.realm.canDeleteLegalCode
import at.orchaldir.gm.core.selector.realm.countRealmsWithLegalCodeAtAnyTime
import at.orchaldir.gm.core.selector.util.sortLegalCodes
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

@Resource("/$LEGAL_CODE_TYPE")
class LegalCodeRoutes {
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
}

fun Application.configureLegalCodeRouting() {
    routing {
        get<LegalCodeRoutes.All> { all ->
            logger.info { "Get all legal codes" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllLegalCodes(call, STORE.getState(), all.sort)
            }
        }
        get<LegalCodeRoutes.Details> { details ->
            logger.info { "Get details of legal code ${details.id.value}" }

            val state = STORE.getState()
            val code = state.getLegalCodeStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLegalCodeDetails(call, state, code)
            }
        }
        get<LegalCodeRoutes.New> {
            logger.info { "Add new legal code" }

            STORE.dispatch(CreateLegalCode)

            call.respondRedirect(
                call.application.href(
                    LegalCodeRoutes.Edit(
                        STORE.getState().getLegalCodeStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<LegalCodeRoutes.Delete> { delete ->
            logger.info { "Delete legal code ${delete.id.value}" }

            STORE.dispatch(DeleteLegalCode(delete.id))

            call.respondRedirect(call.application.href(LegalCodeRoutes.All()))

            STORE.getState().save()
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
            val code = parseLegalCode(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showLegalCodeEditor(call, state, code)
            }
        }
        post<LegalCodeRoutes.Update> { update ->
            logger.info { "Update legal code ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val code = parseLegalCode(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateLegalCode(code))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllLegalCodes(
    call: ApplicationCall,
    state: State,
    sort: SortLegalCode,
) {
    val codes = state.sortLegalCodes(sort)
    val createLink = call.application.href(LegalCodeRoutes.New())

    simpleHtml("Legal Codes") {
        field("Count", codes.size)
        showSortTableLinks(call, SortLegalCode.entries, LegalCodeRoutes(), LegalCodeRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Creator" }
                th { +"Realms" }
            }
            codes.forEach { code ->
                tr {
                    tdLink(call, state, code)
                    td { showOptionalDate(call, state, code.date) }
                    td { showCreator(call, state, code.creator, false) }
                    tdSkipZero(state.countRealmsWithLegalCodeAtAnyTime(code.id))
                }
            }
        }

        showCreatorCount(call, state, codes, "Creators")

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showLegalCodeDetails(
    call: ApplicationCall,
    state: State,
    code: LegalCode,
) {
    val backLink = call.application.href(LegalCodeRoutes.All())
    val deleteLink = call.application.href(LegalCodeRoutes.Delete(code.id))
    val editLink = call.application.href(LegalCodeRoutes.Edit(code.id))

    simpleHtmlDetails(code) {
        showLegalCode(call, state, code)

        action(editLink, "Edit")

        if (state.canDeleteLegalCode(code.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
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
