package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.core.model.world.railway.RailwayTypeId
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.TileConnection
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.utils.map.MapSize2d
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

    @Resource("/building")
    class BuildingRoutes(val parent: TownRoutes = TownRoutes()) {
        @Resource("edit")
        class Edit(val id: TownId, val parent: BuildingRoutes = BuildingRoutes())

        @Resource("preview")
        class Preview(val id: TownId, val parent: BuildingRoutes = BuildingRoutes())

        @Resource("add")
        class Add(
            val town: TownId,
            val tileIndex: Int,
            val size: MapSize2d,
            val parent: BuildingRoutes = BuildingRoutes(),
        )
    }

    @Resource("/railway")
    class RailwayRoutes(val parent: TownRoutes = TownRoutes()) {
        @Resource("edit")
        class Edit(val town: TownId, val parent: RailwayRoutes = RailwayRoutes())

        @Resource("preview")
        class Preview(val town: TownId, val parent: RailwayRoutes = RailwayRoutes())

        @Resource("add")
        class Add(
            val town: TownId,
            val tileIndex: Int,
            val railway: RailwayTypeId,
            val connection: TileConnection,
            val parent: RailwayRoutes = RailwayRoutes(),
        )

        @Resource("remove")
        class Remove(
            val town: TownId,
            val tileIndex: Int,
            val remove: RailwayTypeId,
            val railway: RailwayTypeId,
            val connection: TileConnection,
            val parent: RailwayRoutes = RailwayRoutes(),
        )
    }

    @Resource("/street")
    class StreetRoutes(val parent: TownRoutes = TownRoutes()) {
        @Resource("edit")
        class Edit(val id: TownId, val parent: StreetRoutes = StreetRoutes())

        @Resource("preview")
        class Preview(val id: TownId, val parent: StreetRoutes = StreetRoutes())

        @Resource("add")
        class Add(
            val id: TownId,
            val tileIndex: Int,
            val streetId: StreetId,
            val connection: TileConnection,
            val parent: StreetRoutes = StreetRoutes(),
        )

        @Resource("remove")
        class Remove(
            val id: TownId,
            val tileIndex: Int,
            val remove: StreetId,
            val street: StreetId,
            val connection: TileConnection,
            val parent: StreetRoutes = StreetRoutes(),
        )
    }

    @Resource("/terrain")
    class TerrainRoutes(val parent: TownRoutes = TownRoutes()) {
        @Resource("edit")
        class Edit(val id: TownId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("preview")
        class Preview(val id: TownId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("update")
        class Update(
            val id: TownId,
            val terrainType: TerrainType,
            val terrainId: Int,
            val tileIndex: Int,
            val parent: TerrainRoutes = TerrainRoutes(),
        )

        @Resource("resize")
        class Resize(val id: TownId, val parent: TerrainRoutes = TerrainRoutes())
    }
}
