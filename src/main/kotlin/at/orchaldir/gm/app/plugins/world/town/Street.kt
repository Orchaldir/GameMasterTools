package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.STREET
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.plugins.world.StreetRoutes
import at.orchaldir.gm.core.action.AddStreetTile
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.town.NoConstruction
import at.orchaldir.gm.core.model.world.town.StreetTile
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.core.model.world.town.TownTile
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.renderer.TileMap2dRenderer
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.town.getColor
import at.orchaldir.gm.visualization.town.renderStreet
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
                showStreetEditor(call, state, town, StreetId(0))
            }
        }
        post<TownRoutes.StreetRoutes.Preview> { preview ->
            logger.info { "Preview the street editor for town ${preview.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val streetId: Int = parseInt(params, TERRAIN, 0)

            call.respondHtml(HttpStatusCode.OK) {
                showStreetEditor(call, state, town, StreetId(streetId))
            }
        }
        get<TownRoutes.StreetRoutes.Add> { add ->
            logger.info { "Set tile ${add.tileIndex} to street ${add.streetId.value} for town ${add.id.value}" }

            STORE.dispatch(AddStreetTile(add.id, add.tileIndex, add.streetId))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                val state = STORE.getState()
                val town = state.getTownStorage().getOrThrow(add.id)
                showStreetEditor(call, state, town, add.streetId)
            }
        }
    }
}

private fun HTML.showStreetEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    streetId: StreetId,
) {
    val backLink = href(call, town.id)
    val previewLink = call.application.href(TownRoutes.StreetRoutes.Preview(town.id))
    val createLink = call.application.href(StreetRoutes.New())

    simpleHtml("Edit Streets of Town ${town.name}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectValue("Street", STREET, state.getStreetStorage().getAll(), true) { street ->
                    label = street.name
                    value = street.id.toString()
                    selected = street.id == streetId
                }
            }
            action(createLink, "Create new Street")
            back(backLink)
        }, {
            svg(visualizeStreetEditor(call, town, streetId), 90)
        })
    }
}

fun visualizeStreetEditor(
    call: ApplicationCall,
    town: Town,
    streetId: StreetId,
): Svg {
    val tileMapRenderer = TileMap2dRenderer(Distance(20.0f), Distance(1.0f))
    val svgBuilder = SvgBuilder(tileMapRenderer.calculateMapSize(town.map))

    tileMapRenderer.renderWithLinks(svgBuilder, town.map, TownTile::getColor) { index, tile ->
        if (tile.construction is NoConstruction) {
            call.application.href(TownRoutes.StreetRoutes.Add(town.id, index, streetId))
        } else {
            null
        }
    }

    tileMapRenderer.render(town.map) { _, _, _, aabb, tile ->
        if (tile.construction is StreetTile) {
            if (tile.construction.street == streetId) {
                renderStreet(svgBuilder, aabb, Color.Gold)
            } else {
                renderStreet(svgBuilder, aabb)
            }
        }
    }

    return svgBuilder.finish()
}

