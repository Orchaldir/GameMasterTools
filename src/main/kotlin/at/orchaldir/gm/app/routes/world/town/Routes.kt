package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.core.model.util.SortTown
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.terrain.TerrainType
import at.orchaldir.gm.core.model.world.town.TOWN_TYPE
import at.orchaldir.gm.core.model.world.town.TownId
import at.orchaldir.gm.utils.map.MapSize2d
import io.ktor.resources.*

@Resource("/$TOWN_TYPE")
class TownRoutes {
    @Resource("all")
    class All(
        val sort: SortTown = SortTown.Name,
        val parent: TownRoutes = TownRoutes(),
    )

    @Resource("details")
    class Details(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("new")
    class New(val parent: TownRoutes = TownRoutes())

    @Resource("delete")
    class Delete(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("edit")
    class Edit(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("preview")
    class Preview(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("update")
    class Update(val id: TownId, val parent: TownRoutes = TownRoutes())

    @Resource("/abstract")
    class AbstractBuildingRoutes(val parent: TownRoutes = TownRoutes()) {
        @Resource("edit")
        class Edit(val id: TownId, val parent: AbstractBuildingRoutes = AbstractBuildingRoutes())

        @Resource("add")
        class Add(
            val town: TownId,
            val tileIndex: Int,
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )

        @Resource("remove")
        class Remove(
            val town: TownId,
            val tileIndex: Int,
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )
    }

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
            val typeId: StreetTemplateId,
            val streetId: StreetId? = null,
            val parent: StreetRoutes = StreetRoutes(),
        )

        @Resource("remove")
        class Remove(
            val id: TownId,
            val tileIndex: Int,
            val typeId: StreetTemplateId,
            val streetId: StreetId? = null,
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
