package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editCulture
import at.orchaldir.gm.app.html.culture.parseCulture
import at.orchaldir.gm.app.html.culture.showCulture
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.culture.CULTURE_TYPE
import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.util.Rarity
import at.orchaldir.gm.core.model.util.SortCulture
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.util.sortCultures
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$CULTURE_TYPE")
class CultureRoutes : Routes<CultureId, SortCulture> {
    @Resource("all")
    class All(
        val sort: SortCulture = SortCulture.Name,
        val parent: CultureRoutes = CultureRoutes(),
    )

    @Resource("details")
    class Details(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("new")
    class New(val parent: CultureRoutes = CultureRoutes())

    @Resource("clone")
    class Clone(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("delete")
    class Delete(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("edit")
    class Edit(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("preview")
    class Preview(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    @Resource("update")
    class Update(val id: CultureId, val parent: CultureRoutes = CultureRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCulture) = call.application.href(All(sort))
    override fun clone(call: ApplicationCall, id: CultureId) = call.application.href(Clone(id))
    override fun delete(call: ApplicationCall, id: CultureId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CultureId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: CultureId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: CultureId) = call.application.href(Update(id))
}

fun Application.configureCultureRouting() {
    routing {
        get<CultureRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CultureRoutes(),
                state.sortCultures(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Calendar") { tdLink(call, state, it.calendar) },
                    Column("Languages") { tdInlineIds(call, state, it.languages.getValuesFor(Rarity.Everyone)) },
                    Column(listOf("Naming", "Convention")) { tdEnum(it.namingConvention.getType()) },
                    countCollectionColumn("Holidays") { it.holidays },
                    countCollectionColumn("Characters") { state.getCharacters(it.id) },
                ),
            )
        }
        get<CultureRoutes.Details> { details ->
            handleShowElement(details.id, CultureRoutes(), HtmlBlockTag::showCulture)
        }
        get<CultureRoutes.New> {
            handleCreateElement(STORE.getState().getCultureStorage()) { id ->
                CultureRoutes.Edit(id)
            }
        }
        get<CultureRoutes.Clone> { clone ->
            handleCloneElement(clone.id) { cloneId ->
                CultureRoutes.Edit(cloneId)
            }
        }
        get<CultureRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CultureRoutes())
        }
        get<CultureRoutes.Edit> { edit ->
            handleEditElement(edit.id, CultureRoutes(), HtmlBlockTag::editCulture)
        }
        post<CultureRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, CultureRoutes(), ::parseCulture, HtmlBlockTag::editCulture)
        }
        post<CultureRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCulture)
        }
    }
}
