package at.orchaldir.gm.app.routes.utls

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.tdSkipZero
import at.orchaldir.gm.app.html.util.name.editNameList
import at.orchaldir.gm.app.html.util.name.parseNameList
import at.orchaldir.gm.app.html.util.name.showNameList
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.util.SortNameList
import at.orchaldir.gm.core.model.util.name.NAME_LIST_TYPE
import at.orchaldir.gm.core.model.util.name.NameListId
import at.orchaldir.gm.core.selector.util.sortNameLists
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$NAME_LIST_TYPE")
class NameListRoutes : Routes<NameListId, SortNameList> {
    @Resource("all")
    class All(
        val sort: SortNameList = SortNameList.Name,
        val parent: NameListRoutes = NameListRoutes(),
    )

    @Resource("details")
    class Details(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("new")
    class New(val parent: NameListRoutes = NameListRoutes())

    @Resource("delete")
    class Delete(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("edit")
    class Edit(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("preview")
    class Preview(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    @Resource("update")
    class Update(val id: NameListId, val parent: NameListRoutes = NameListRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortNameList) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: NameListId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: NameListId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: NameListId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: NameListId) = call.application.href(Update(id))
}

fun Application.configureNameListRouting() {
    routing {
        get<NameListRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                NameListRoutes(),
                state.sortNameLists(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Count") { tdSkipZero(it.names) },
                ),
            )
        }
        get<NameListRoutes.Details> { details ->
            handleShowElement(details.id, NameListRoutes(), HtmlBlockTag::showNameList)
        }
        get<NameListRoutes.New> {
            handleCreateElement(NameListRoutes(), STORE.getState().getNameListStorage())
        }
        get<NameListRoutes.Delete> { delete ->
            handleDeleteElement(NameListRoutes(), delete.id)
        }
        get<NameListRoutes.Edit> { edit ->
            handleEditElement(edit.id, NameListRoutes(), HtmlBlockTag::editNameList)
        }
        post<NameListRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, NameListRoutes(), ::parseNameList, HtmlBlockTag::editNameList)
        }
        post<NameListRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseNameList)
        }
    }
}
