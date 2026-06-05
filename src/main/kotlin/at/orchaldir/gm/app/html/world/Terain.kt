package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.routes.world.RegionRoutes
import at.orchaldir.gm.app.routes.world.RiverRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.world.settlement.SettlementMap
import at.orchaldir.gm.core.model.world.settlement.TerrainType
import at.orchaldir.gm.core.model.world.terrain.RegionDataType
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.Resize
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2
import kotlinx.html.p

fun HtmlBlockTag.editTerrain(
    call: ApplicationCall,
    state: State,
    terrainType: TerrainType,
    terrainId: Int,
    resize: Resize,
    settlementMap: SettlementMap,
) {
    val createMountainLink = call.application.href(RegionRoutes.New())
    val createRiverLink = call.application.href(RiverRoutes.New())
    val rivers = state.getRiverStorage().getAll()
    val mountains = state.getRegions(RegionDataType.Mountain)

    selectValue("Terrain", combine(TERRAIN, TYPE), TerrainType.entries, terrainType) { type ->
        when (type) {
            TerrainType.Hill, TerrainType.Mountain -> mountains.isEmpty()
            TerrainType.Plain -> false
            TerrainType.River -> rivers.isEmpty()
        }
    }
    when (terrainType) {
        TerrainType.Hill, TerrainType.Mountain -> selectTerrain(
            "Mountain",
            mountains,
            terrainId,
        )

        TerrainType.Plain -> doNothing()
        TerrainType.River -> selectTerrain(
            "River",
            rivers,
            terrainId,
        )
    }
    action(createMountainLink, "Create new Mountain")
    action(createRiverLink, "Create new River")

    h2 { +"Update Terrain of Tile" }

    p { +"Click on a tile to change it's terrain to the type above." }

    h2 { +"Resize" }

    field("Size", settlementMap.map.size.format())
    val maxDelta = 100
    selectInt(
        "Add/Remove Columns At Start",
        resize.widthStart,
        getMinWidthStart(settlementMap),
        maxDelta,
        1,
        combine(WIDTH, START)
    )
    selectInt(
        "Add/Remove Columns At End",
        resize.widthEnd,
        getMinWidthEnd(settlementMap),
        maxDelta,
        1,
        combine(WIDTH, END),
    )
    selectInt(
        "Add/Remove Rows At Start",
        resize.heightStart,
        getMinHeightStart(settlementMap),
        maxDelta,
        1,
        combine(HEIGHT, START),
    )
    selectInt(
        "Add/Remove Rows At End",
        resize.heightEnd,
        getMinHeightEnd(settlementMap),
        maxDelta,
        1,
        combine(HEIGHT, END),
    )
}

private fun <ID : Id<ID>> HtmlBlockTag.selectTerrain(
    text: String,
    options: Collection<ElementWithSimpleName<ID>>,
    id: Int,
) {
    selectValue(text, TERRAIN, options) { m ->
        label = m.name()
        value = m.id().value().toString()
        selected = id == m.id().value()
    }
}

fun parseTerrainResize(params: Parameters): Resize = Resize(
    parseInt(params, combine(WIDTH, START), 0),
    parseInt(params, combine(WIDTH, END), 0),
    parseInt(params, combine(HEIGHT, START), 0),
    parseInt(params, combine(HEIGHT, END), 0),
)