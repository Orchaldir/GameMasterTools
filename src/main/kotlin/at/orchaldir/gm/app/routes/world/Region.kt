package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.world.editRegion
import at.orchaldir.gm.app.html.model.world.parseRegion
import at.orchaldir.gm.app.html.model.world.showRegion
import at.orchaldir.gm.core.action.CreateRegion
import at.orchaldir.gm.core.action.DeleteRegion
import at.orchaldir.gm.core.action.UpdateRegion
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortPlane
import at.orchaldir.gm.core.model.util.SortRegion
import at.orchaldir.gm.core.model.world.terrain.REGION_TYPE
import at.orchaldir.gm.core.model.world.terrain.Region
import at.orchaldir.gm.core.model.world.terrain.RegionId
import at.orchaldir.gm.core.selector.util.sortRegions
import at.orchaldir.gm.core.selector.world.canDeleteRegion
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
    @Resource("all")
    class All(
        val sort: SortRegion = SortRegion.Name,
        val parent: RegionRoutes = RegionRoutes(),
    )

    @Resource("details")
    class Details(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("new")
    class New(val parent: RegionRoutes = RegionRoutes())

    @Resource("delete")
    class Delete(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("edit")
    class Edit(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("preview")
    class Preview(val id: RegionId, val parent: RegionRoutes = RegionRoutes())

    @Resource("update")
    class Update(val id: RegionId, val parent: RegionRoutes = RegionRoutes())
}

fun Application.configureMountainRouting() {
    routing {
        get<RegionRoutes.All> { all ->
            logger.info { "Get all regions" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMountains(call, STORE.getState(), all.sort)
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
        post<RegionRoutes.Preview> { preview ->
            logger.info { "Get preview for region ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val region = parseRegion(preview.id, formParameters)

            call.respondHtml(HttpStatusCode.OK) {
                showMountainEditor(call, state, region)
            }
        }
        post<RegionRoutes.Update> { update ->
            logger.info { "Update region ${update.id.value}" }

            val region = parseRegion(update.id, call.receiveParameters())

            STORE.dispatch(UpdateRegion(region))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllMountains(
    call: ApplicationCall,
    state: State,
    sort: SortRegion = SortRegion.Name,
) {
    val regions = state.sortRegions(sort)
    val createLink = call.application.href(RegionRoutes.New())

    simpleHtml("Mountains") {
        field("Count", regions.size)
        showSortTableLinks(call, SortRegion.entries, RegionRoutes(), RegionRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Type" }
                th { +"Resources" }
            }
            regions.forEach { region ->
                tr {
                    tdLink(call, state, region)
                    tdEnum(region.data.getType())
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
        showRegion(call, state, region)

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

    val backLink = href(call, region.id)
    val previewLink = call.application.href(RegionRoutes.Preview(region.id))
    val updateLink = call.application.href(RegionRoutes.Update(region.id))

    simpleHtmlEditor(region) {
        formWithPreview(previewLink, updateLink, backLink) {
            editRegion(state, region)
        }
    }
}
