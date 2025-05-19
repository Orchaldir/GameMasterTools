package at.orchaldir.gm.app.routes.world.town

import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.model.world.town.TOWN_MAP_TYPE
import at.orchaldir.gm.core.model.world.town.TerrainType
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.utils.map.MapSize2d
import io.ktor.resources.*

@Resource("/$TOWN_MAP_TYPE")
class TownMapRoutes {
    @Resource("all")
    class All(
        val parent: TownMapRoutes = TownMapRoutes(),
    )

    @Resource("details")
    class Details(val id: TownMapId, val parent: TownMapRoutes = TownMapRoutes())

    @Resource("new")
    class New(val parent: TownMapRoutes = TownMapRoutes())

    @Resource("delete")
    class Delete(val id: TownMapId, val parent: TownMapRoutes = TownMapRoutes())

    @Resource("edit")
    class Edit(val id: TownMapId, val parent: TownMapRoutes = TownMapRoutes())

    @Resource("preview")
    class Preview(val id: TownMapId, val parent: TownMapRoutes = TownMapRoutes())

    @Resource("update")
    class Update(val id: TownMapId, val parent: TownMapRoutes = TownMapRoutes())

    @Resource("/abstract")
    class AbstractBuildingRoutes(val parent: TownMapRoutes = TownMapRoutes()) {
        @Resource("edit")
        class Edit(val id: TownMapId, val parent: AbstractBuildingRoutes = AbstractBuildingRoutes())

        @Resource("add")
        class Add(
            val town: TownMapId,
            val tileIndex: Int,
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )

        @Resource("remove")
        class Remove(
            val town: TownMapId,
            val tileIndex: Int,
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )
    }

    @Resource("/building")
    class BuildingRoutes(val parent: TownMapRoutes = TownMapRoutes()) {
        @Resource("edit")
        class Edit(val id: TownMapId, val parent: BuildingRoutes = BuildingRoutes())

        @Resource("preview")
        class Preview(val id: TownMapId, val parent: BuildingRoutes = BuildingRoutes())

        @Resource("add")
        class Add(
            val town: TownMapId,
            val tileIndex: Int,
            val size: MapSize2d,
            val parent: BuildingRoutes = BuildingRoutes(),
        )
    }

    @Resource("/street")
    class StreetRoutes(val parent: TownMapRoutes = TownMapRoutes()) {
        @Resource("edit")
        class Edit(val id: TownMapId, val parent: StreetRoutes = StreetRoutes())

        @Resource("preview")
        class Preview(val id: TownMapId, val parent: StreetRoutes = StreetRoutes())

        @Resource("add")
        class Add(
            val id: TownMapId,
            val tileIndex: Int,
            val typeId: StreetTemplateId,
            val streetId: StreetId? = null,
            val parent: StreetRoutes = StreetRoutes(),
        )

        @Resource("remove")
        class Remove(
            val id: TownMapId,
            val tileIndex: Int,
            val typeId: StreetTemplateId,
            val streetId: StreetId? = null,
            val parent: StreetRoutes = StreetRoutes(),
        )
    }

    @Resource("/terrain")
    class TerrainRoutes(val parent: TownMapRoutes = TownMapRoutes()) {
        @Resource("edit")
        class Edit(val id: TownMapId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("preview")
        class Preview(val id: TownMapId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("update")
        class Update(
            val id: TownMapId,
            val terrainType: TerrainType,
            val terrainId: Int,
            val tileIndex: Int,
            val parent: TerrainRoutes = TerrainRoutes(),
        )

        @Resource("resize")
        class Resize(val id: TownMapId, val parent: TerrainRoutes = TerrainRoutes())
    }
}
