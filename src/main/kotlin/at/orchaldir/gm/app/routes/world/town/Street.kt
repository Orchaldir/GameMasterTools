package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.STREET
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.action.RemoveStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.town.TownMap
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureStreetEditorRouting() {
    routing {
        get<TownMapRoutes.StreetRoutes.Edit> { edit ->
            logger.info { "Get the street editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val townMap = state.getTownMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, townMap, StreetTemplateId(0), null)
            }
        }
        post<TownMapRoutes.StreetRoutes.Preview> { preview ->
            logger.info { "Preview the street editor for town ${preview.id.value}" }

            val state = STORE.getState()
            val townMap = state.getTownMapStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val typeId = parseInt(params, TYPE, 0)
            val streetId = parseSimpleOptionalInt(params, STREET)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, townMap, StreetTemplateId(typeId), streetId?.let { StreetId(it) })
            }
        }
        get<TownMapRoutes.StreetRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to street ${add.typeId.value} for town ${add.id.value}" }

            STORE.dispatch(AddStreetTile(add.id, add.tileIndex, add.typeId, add.streetId))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownMapStorage().getOrThrow(add.id)
                showStreetEditor(call, state, town, add.typeId, add.streetId)
            }
        }
        get<TownMapRoutes.StreetRoutes.Remove> { remove ->
            logger.info { "Remove street from tile ${remove.tileIndex} for town ${remove.id.value}" }

            STORE.dispatch(RemoveStreetTile(remove.id, remove.tileIndex))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownMapStorage().getOrThrow(remove.id)
                showStreetEditor(call, state, town, remove.typeId, remove.streetId)
            }
        }
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
    selectedType: StreetTemplateId,
    selectedStreetId: StreetId?,
) {
    val selectedStreet = state.getStreetStorage().getOptional(selectedStreetId)
    val backLink = href(call, townMap.id)
    val previewLink = call.application.href(TownMapRoutes.StreetRoutes.Preview(townMap.id))
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Edit Streets of Town Map ${townMap.name(state)}") {
        split({
            formWithPreview(previewLink, createLink, backLink, "Create new Street") {
                selectElement(state, "Type", TYPE, state.getStreetTemplateStorage().getAll(), selectedType)
                selectOptionalValue(
                    "Street",
                    STREET,
                    selectedStreet,
                    state.getStreetStorage().getAll(),
                ) { street ->
                    label = street.name(state)
                    value = street.id.value.toString()
                }
            }
        }, {
            svg(visualizeStreetEditor(call, state, townMap, selectedType, selectedStreetId), 90)
        })
    }
}

fun visualizeStreetEditor(
    call: ApplicationCall,
    state: State,
    townMap: TownMap,
    selectedType: StreetTemplateId,
    selectedStreet: StreetId?,
) = visualizeTown(
    townMap, state.getBuildingsIn(townMap.id),
    tileLinkLookup = { index, tile ->
        if (tile.canBuild()) {
            call.application.href(TownMapRoutes.StreetRoutes.Add(townMap.id, index, selectedType, selectedStreet))
        } else {
            null
        }
    },
    streetColorLookup = { street, _ ->
        if (selectedStreet == null) {
            state.getStreetTemplateStorage().getOrThrow(street.templateId).color
        } else if (street.streetId == selectedStreet) {
            Color.Gold
        } else {
            Color.Gray
        }
    },
    streetLinkLookup = { _, index ->
        call.application.href(TownMapRoutes.StreetRoutes.Remove(townMap.id, index, selectedType, selectedStreet))
    },
    streetTooltipLookup = { street, _ ->
        state.getStreetStorage().getOptional(street.streetId)?.name(state)
    },
)

