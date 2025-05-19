package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.MATERIAL
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.world.parseMountain
import at.orchaldir.gm.core.action.CreateRegion
import at.orchaldir.gm.core.action.DeleteRegion
import at.orchaldir.gm.core.action.UpdateRegion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.REGION_TYPE
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.selector.util.sortMaterial
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.core.selector.world.canDeleteRegion
import at.orchaldir.gm.core.selector.world.getTowns
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

@Resource("/$REGION_TYPE")
class RegionRoutes {
    @Resource("details")
    class Details(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("new")
    class New(val parent: RegionRoutes = RegionRoutes())

    @Resource("delete")
    class Delete(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("edit")
    class Edit(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("update")
    class Update(val id: RegionId, val parent: RegionRoutes = RegionRoutes())
}

fun Application.configureMountainRouting() {
    routing {
        get<RegionRoutes> {
            logger.info { "Get all regions" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMountains(call, STORE.getState())
            }
        }
        get<RegionRoutes.Details> { details ->
            logger.info { "Get details of region ${details.id.value}" }

            val state = STORE.getState()
            val region = state.getRegionStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMountainDetails(call, state, region)
            }
        }
        get<RegionRoutes.New> {
            logger.info { "Add new region" }

            STORE.dispatch(CreateRegion)

            call.respondRedirect(
                call.application.href(
                    RegionRoutes.Edit(
                        STORE.getState().getRegionStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<RegionRoutes.Delete> { delete ->
            logger.info { "Delete region ${delete.id.value}" }

            STORE.dispatch(DeleteRegion(delete.id))

            call.respondRedirect(call.application.href(RegionRoutes()))

            STORE.getState().save()
        }
        get<RegionRoutes.Edit> { edit ->
            logger.info { "Get editor for region ${edit.id.value}" }

            val state = STORE.getState()
            val region = state.getRegionStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMountainEditor(call, state, region)
            }
        }
        post<RegionRoutes.Update> { update ->
            logger.info { "Update region ${update.id.value}" }

            val region = parseMountain(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRegion(region))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllMountains(
    call: ApplicationCall,
    state: State,
) {
    val regions = state.getRegionStorage().getAll().sortedBy { it.name.text }
    val createLink = call.application.href(RegionRoutes.New())

    simpleHtml("Mountains") {
        field("Count", regions.size)

        table {
            tr {
                th { +"Name" }
                th { +"Resources" }
            }
            regions.forEach { region ->
                tr {
                    tdLink(call, state, region)
                    tdInlineIds(call, state, region.resources)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showMountainDetails(
    call: ApplicationCall,
    state: State,
    region: Region,
) {
    val backLink = call.application.href(RegionRoutes())
    val deleteLink = call.application.href(RegionRoutes.Delete(region.id))
    val editLink = call.application.href(RegionRoutes.Edit(region.id))

    simpleHtmlDetails(region) {
        fieldName(region.name)
        fieldIdList(call, state, "Resources", region.resources)
        fieldList(call, state, state.getTowns(region.id))

        action(editLink, "Edit")

        if (state.canDeleteRegion(region.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showMountainEditor(
    call: ApplicationCall,
    state: State,
    region: Region,
) {
    val materials = state.sortMaterial()
    val backLink = href(call, region.id)
    val updateLink = call.application.href(RegionRoutes.Update(region.id))

    simpleHtmlEditor(region) {
        form {
            selectName(region.name)
            selectElements(state, "Resources", MATERIAL, materials, region.resources)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
