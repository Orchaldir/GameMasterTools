package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editLegalCode
import at.orchaldir.gm.app.html.realm.parseLegalCode
import at.orchaldir.gm.app.html.realm.showLegalCode
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.LEGAL_CODE_TYPE
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.model.util.SortLegalCode
import at.orchaldir.gm.core.selector.realm.countRealmsWithLegalCodeAtAnyTime
import at.orchaldir.gm.core.selector.util.sortLegalCodes
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: LegalCodeId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: LegalCodeId) = call.application.href(Update(id))
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
                    countColumnForId("Population", state::countRealmsWithLegalCodeAtAnyTime),
                ),
            ) {
                showCreatorCount(call, state, it, "Creators")
            }
        }
        get<LegalCodeRoutes.Details> { details ->
            handleShowElement(details.id, LegalCodeRoutes(), HtmlBlockTag::showLegalCode)
        }
        get<LegalCodeRoutes.New> {
            handleCreateElement(LegalCodeRoutes(), STORE.getState().getLegalCodeStorage())
        }
        get<LegalCodeRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, LegalCodeRoutes.All())
        }
        get<LegalCodeRoutes.Edit> { edit ->
            handleEditElement(edit.id, LegalCodeRoutes(), HtmlBlockTag::editLegalCode)
        }
        post<LegalCodeRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, LegalCodeRoutes(), ::parseLegalCode, HtmlBlockTag::editLegalCode)
        }
        post<LegalCodeRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseLegalCode)
        }
    }
}
