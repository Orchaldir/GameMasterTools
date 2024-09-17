package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.TERRAIN
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.action.UpdateTown
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.Town
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.visualization.town.visualizeTown
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureTerrainRouting() {
    routing {
        get<TownRoutes.TerrainRoutes.Edit> { edit ->
            logger.info { "Get the terrain editor for town ${edit.id.value}" }

            val state = STORE.getState()
            val town = state.getTownStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town)
            }
        }
        post<TownRoutes.TerrainRoutes.Update> { update ->
            logger.info { "Update the terrain for town ${update.id.value}" }

            val state = STORE.getState()
            val oldTown = state.getTownStorage().getOrThrow(update.id)
            val town = oldTown

            STORE.dispatch(UpdateTown(town))

            STORE.getState().save()

            call.respondHtml(HttpStatusCode.OK) {
                showTerrainEditor(call, state, town)
            }
        }
    }
}

private fun HTML.showTerrainEditor(
    call: ApplicationCall,
    state: State,
    town: Town,
    terrain: TerrainType = TerrainType.Plain,
    id: Int = 0,
) {
    val backLink = href(call, town.id)
    val updateLink = call.application.href(TownRoutes.Update(town.id))

    simpleHtml("Edit Terrain of Town ${town.name}") {
        split({
            form {
                selectValue("Terrain", combine(TERRAIN, TYPE), TerrainType.entries, true) { type ->
                    label = type.toString()
                    value = type.toString()
                    selected = type == terrain
                }
                when (terrain) {
                    TerrainType.Hill, TerrainType.Mountain -> selectTerrain(
                        "Mountain",
                        state.getMountainStorage().getAll(),
                        id
                    )

                    TerrainType.Plain -> doNothing()
                    TerrainType.River -> selectTerrain("River", state.getMountainStorage().getAll(), id)
                }
                p {
                    submitInput {
                        value = "Update"
                        formAction = updateLink
                        formMethod = InputFormMethod.post
                    }
                }
            }
            back(backLink)
        }, {
            svg(visualizeTown(town), 90)
        })
    }
}

private fun <ID : Id<ID>> FORM.selectTerrain(text: String, options: Collection<Element<ID>>, id: Int) {
    selectValue(text, TERRAIN, options, true) { m ->
        label = m.name()
        value = m.id().value().toString()
        selected = id == m.id().value()
    }
}
