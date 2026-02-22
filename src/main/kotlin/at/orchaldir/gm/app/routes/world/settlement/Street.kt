package at.orchaldir.gm.app.routes.world.settlement

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
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.visualization.settlement.visualizeSettlementMap
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
        get<SettlementMapRoutes.StreetRoutes.Edit> { edit ->
            logger.info { "Get the street editor for settlement ${edit.id.value}" }

            val state = STORE.getState()
            val settlementMap = state.getSettlementMapStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, settlementMap, StreetTemplateId(0), null)
            }
        }
        post<SettlementMapRoutes.StreetRoutes.Preview> { preview ->
            logger.info { "Preview the street editor for settlement ${preview.id.value}" }

            val state = STORE.getState()
            val settlementMap = state.getSettlementMapStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val typeId = parseInt(params, TYPE, 0)
            val streetId = parseSimpleOptionalInt(params, STREET)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, settlementMap, StreetTemplateId(typeId), streetId?.let { StreetId(it) })
            }
        }
        get<SettlementMapRoutes.StreetRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to street ${add.typeId.value} for settlement ${add.id.value}" }

            STORE.dispatch(AddStreetTile(add.id, add.tileIndex, add.typeId, add.streetId))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val settlement = state.getSettlementMapStorage().getOrThrow(add.id)
                showStreetEditor(call, state, settlement, add.typeId, add.streetId)
            }
        }
        get<SettlementMapRoutes.StreetRoutes.Remove> { remove ->
            logger.info { "Remove street from tile ${remove.tileIndex} for settlement ${remove.id.value}" }

            STORE.dispatch(RemoveStreetTile(remove.id, remove.tileIndex))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val settlement = state.getSettlementMapStorage().getOrThrow(remove.id)
                showStreetEditor(call, state, settlement, remove.typeId, remove.streetId)
            }
        }
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
    selectedType: StreetTemplateId,
    selectedStreetId: StreetId?,
) {
    val selectedStreet = state.getStreetStorage().getOptional(selectedStreetId)
    val backLink = href(call, settlementMap.id)
    val previewLink = call.application.href(SettlementMapRoutes.StreetRoutes.Preview(settlementMap.id))
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Edit Streets of Settlement Map ${settlementMap.name(state)}") {
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
            svg(visualizeStreetEditor(call, state, settlementMap, selectedType, selectedStreetId), 90)
        })
    }
}

fun visualizeStreetEditor(
    call: ApplicationCall,
    state: State,
    settlementMap: SettlementMap,
    selectedType: StreetTemplateId,
    selectedStreet: StreetId?,
) = visualizeSettlementMap(
    settlementMap, state.getBuildingsIn(settlementMap.id),
    tileLinkLookup = { index, tile ->
        if (tile.canBuild()) {
            call.application.href(SettlementMapRoutes.StreetRoutes.Add(settlementMap.id, index, selectedType, selectedStreet))
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
        call.application.href(SettlementMapRoutes.StreetRoutes.Remove(settlementMap.id, index, selectedType, selectedStreet))
    },
    streetTooltipLookup = { street, _ ->
        state.getStreetStorage().getOptional(street.streetId)?.name(state)
    },
)

