package at.orchaldir.gm.app.routes.rpg

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.rpg.encounter.editEncounter
import at.orchaldir.gm.app.html.rpg.encounter.parseEncounter
import at.orchaldir.gm.app.html.rpg.encounter.showEncounter
import at.orchaldir.gm.app.html.tdEnum
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.rpg.encounter.ENCOUNTER_TYPE
import at.orchaldir.gm.core.model.rpg.encounter.EncounterId
import at.orchaldir.gm.core.model.util.SortEncounter
import at.orchaldir.gm.core.selector.util.sortEncounters
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$ENCOUNTER_TYPE")
class EncounterRoutes : Routes<EncounterId, SortEncounter> {
    @Resource("all")
    class All(
        val sort: SortEncounter = SortEncounter.Name,
        val parent: EncounterRoutes = EncounterRoutes(),
    )

    @Resource("details")
    class Details(val id: EncounterId, val parent: EncounterRoutes = EncounterRoutes())

    @Resource("new")
    class New(val parent: EncounterRoutes = EncounterRoutes())

    @Resource("delete")
    class Delete(val id: EncounterId, val parent: EncounterRoutes = EncounterRoutes())

    @Resource("edit")
    class Edit(val id: EncounterId, val parent: EncounterRoutes = EncounterRoutes())

    @Resource("preview")
    class Preview(val id: EncounterId, val parent: EncounterRoutes = EncounterRoutes())

    @Resource("update")
    class Update(val id: EncounterId, val parent: EncounterRoutes = EncounterRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortEncounter) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: EncounterId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: EncounterId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: EncounterId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: EncounterId) = call.application.href(Update(id))
}

fun Application.configureEncounterRouting() {
    routing {
        get<EncounterRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                EncounterRoutes(),
                state.sortEncounters(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Type") { tdEnum(it.entry.getType()) },
                ),
            )
        }
        get<EncounterRoutes.Details> { details ->
            handleShowElement(details.id, EncounterRoutes(), HtmlBlockTag::showEncounter)
        }
        get<EncounterRoutes.New> {
            handleCreateElement(EncounterRoutes(), STORE.getState().getEncounterStorage())
        }
        get<EncounterRoutes.Delete> { delete ->
            handleDeleteElement(EncounterRoutes(), delete.id)
        }
        get<EncounterRoutes.Edit> { edit ->
            handleEditElement(edit.id, EncounterRoutes(), HtmlBlockTag::editEncounter)
        }
        post<EncounterRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, EncounterRoutes(), ::parseEncounter, HtmlBlockTag::editEncounter)
        }
        post<EncounterRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseEncounter)
        }
    }
}
