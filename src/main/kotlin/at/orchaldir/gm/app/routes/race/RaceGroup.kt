package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column.Companion.tdColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.race.editRaceGroup
import at.orchaldir.gm.app.html.race.parseRaceGroup
import at.orchaldir.gm.app.html.race.showRaceGroup
import at.orchaldir.gm.app.html.showInlineList
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.race.RACE_GROUP_TYPE
import at.orchaldir.gm.core.model.race.RaceGroupId
import at.orchaldir.gm.core.model.util.SortRaceGroup
import at.orchaldir.gm.core.selector.util.sortRaceGroups
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$RACE_GROUP_TYPE")
class RaceGroupRoutes : Routes<RaceGroupId, SortRaceGroup> {
    @Resource("all")
    class All(
        val sort: SortRaceGroup = SortRaceGroup.Name,
        val parent: RaceGroupRoutes = RaceGroupRoutes(),
    )

    @Resource("details")
    class Details(val id: RaceGroupId, val parent: RaceGroupRoutes = RaceGroupRoutes())

    @Resource("new")
    class New(val parent: RaceGroupRoutes = RaceGroupRoutes())

    @Resource("delete")
    class Delete(val id: RaceGroupId, val parent: RaceGroupRoutes = RaceGroupRoutes())

    @Resource("edit")
    class Edit(val id: RaceGroupId, val parent: RaceGroupRoutes = RaceGroupRoutes())

    @Resource("preview")
    class Preview(val id: RaceGroupId, val parent: RaceGroupRoutes = RaceGroupRoutes())

    @Resource("update")
    class Update(val id: RaceGroupId, val parent: RaceGroupRoutes = RaceGroupRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortRaceGroup) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: RaceGroupId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: RaceGroupId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: RaceGroupId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: RaceGroupId) = call.application.href(Update(id))
}

fun Application.configureRaceGroupRouting() {
    routing {
        get<RaceGroupRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                RaceGroupRoutes(),
                state.sortRaceGroups(all.sort),
                listOf(
                    createNameColumn(call, state),
                    tdColumn("Races") {
                        showInlineList(it.races) { raceId ->
                            link(call, state, raceId)
                        }
                    },
                ),
            )
        }
        get<RaceGroupRoutes.Details> { details ->
            handleShowElement(details.id, RaceGroupRoutes(), HtmlBlockTag::showRaceGroup)
        }
        get<RaceGroupRoutes.New> {
            handleCreateElement(RaceGroupRoutes(), STORE.getState().getRaceGroupStorage())
        }
        get<RaceGroupRoutes.Delete> { delete ->
            handleDeleteElement(RaceGroupRoutes(), delete.id)
        }
        get<RaceGroupRoutes.Edit> { edit ->
            handleEditElement(
                edit.id,
                RaceGroupRoutes(),
                HtmlBlockTag::editRaceGroup,
            )
        }
        post<RaceGroupRoutes.Preview> { preview ->
            handlePreviewElement(
                preview.id,
                RaceGroupRoutes(),
                ::parseRaceGroup,
                HtmlBlockTag::editRaceGroup,
            )
        }
        post<RaceGroupRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseRaceGroup)
        }
    }
}
