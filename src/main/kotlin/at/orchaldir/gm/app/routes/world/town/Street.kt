package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.STREET
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.app.routes.world.StreetRoutes
import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.action.RemoveStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTypeId
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.form
import kotlinx.html.id
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureStreetEditorRouting() {
    routing {
        get<TownRoutes.StreetRoutes.Edit> { edit ->
            logger.info { "Get the street editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, town, StreetTypeId(0), null)
            }
        }
        post<TownRoutes.StreetRoutes.Preview> { preview ->
            logger.info { "Preview the street editor for town ${preview.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val typeId = parseInt(params, TYPE, 0)
            val streetId = parseOptionalInt(params, STREET)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, town, StreetTypeId(typeId), streetId?.let { StreetId(it) })
            }
        }
        get<TownRoutes.StreetRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to street ${add.typeId.value} for town ${add.id.value}" }

            STORE.dispatch(AddStreetTile(add.id, add.tileIndex, add.typeId, add.streetId))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(add.id)
                showStreetEditor(call, state, town, add.typeId, add.streetId)
            }
        }
        get<TownRoutes.StreetRoutes.Remove> { remove ->
            logger.info { "Remove street from tile ${remove.tileIndex} for town ${remove.id.value}" }

            STORE.dispatch(RemoveStreetTile(remove.id, remove.tileIndex))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(remove.id)
                showStreetEditor(call, state, town, remove.typeId, remove.streetId)
            }
        }
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    selectedType: StreetTypeId,
    selectedStreetId: StreetId?,
) {
    val selectedStreet = state.getStreetStorage().getOptional(selectedStreetId)
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.StreetRoutes.Preview(town.id))
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Edit Streets of Town ${town.name(state)}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectValue("Type", TYPE, state.getStreetTypeStorage().getAll(), true) { type ->
                    label = type.name
                    value = type.id.value.toString()
                    selected = selectedType == type.id
                }
                selectOptionalValue(
                    "Street",
                    STREET,
                    selectedStreet,
                    state.getStreetStorage().getAll(),
                    true
                ) { street ->
                    label = street.name(state)
                    value = street.id.value.toString()
                }
            }
            action(createLink, "Create new Street")
            back(backLink)
        }, {
            svg(visualizeStreetEditor(call, state, town, selectedType, selectedStreetId), 90)
        })
    }
}

fun visualizeStreetEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    selectedType: StreetTypeId,
    selectedStreet: StreetId?,
) = visualizeTown(
    town, state.getBuildings(town.id),
    tileLinkLookup = { index, tile ->
        if (tile.canBuild()) {
            call.application.href(TownRoutes.StreetRoutes.Add(town.id, index, selectedType, selectedStreet))
        } else {
            null
        }
    },
    streetColorLookup = { street, _ ->
        if (selectedStreet == null) {
            state.getStreetTypeStorage().getOrThrow(street.typeId).color
        } else if (street.streetId == selectedStreet) {
            Color.Gold
        } else {
            Color.Gray
        }
    },
    streetLinkLookup = { _, index ->
        call.application.href(TownRoutes.StreetRoutes.Remove(town.id, index, selectedType, selectedStreet))
    },
    streetTooltipLookup = { street, _ ->
        state.getStreetStorage().getOptional(street.streetId)?.name(state)
    },
)

