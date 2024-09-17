package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.TownId
import io.ktor.resources.*

@Resource("/town")
class TownRoutes {
    @Resource("details")
    class Details(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("new")
    class New(val parent: TownRoutes = TownRoutes())

    @Resource("delete")
    class Delete(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("edit")
    class Edit(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("update")
    class Update(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("/terrain")
    class TerrainRoutes(val parent: TownRoutes = TownRoutes()) {
        @Resource("edit")
        class Edit(val id: TownId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("update")
        class Update(
            val id: TownId,
            val terrainType: TerrainType,
            val terrainId: Int,
            val tileIndex: Int,
            val parent: TerrainRoutes = TerrainRoutes(),
        )
    }
}
