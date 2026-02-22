package at.orchaldir.gm.app.routes.world.settlement

import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.core.model.util.SortSettlementMap
import at.orchaldir.gm.core.model.world.settlement.SETTLEMENT_MAP_TYPE
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.model.world.settlement.TerrainType
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.utils.map.MapSize2d
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*

@Resource("/$SETTLEMENT_MAP_TYPE")
class SettlementMapRoutes : Routes<SettlementMapId, SortSettlementMap> {
    @Resource("all")
    class All(
        val sort: SortSettlementMap = SortSettlementMap.Name,
        val parent: SettlementMapRoutes = SettlementMapRoutes(),
    )

    @Resource("details")
    class Details(val id: SettlementMapId, val parent: SettlementMapRoutes = SettlementMapRoutes())

    @Resource("new")
    class New(val parent: SettlementMapRoutes = SettlementMapRoutes())

    @Resource("delete")
    class Delete(val id: SettlementMapId, val parent: SettlementMapRoutes = SettlementMapRoutes())

    @Resource("edit")
    class Edit(val id: SettlementMapId, val parent: SettlementMapRoutes = SettlementMapRoutes())

    @Resource("preview")
    class Preview(val id: SettlementMapId, val parent: SettlementMapRoutes = SettlementMapRoutes())

    @Resource("update")
    class Update(val id: SettlementMapId, val parent: SettlementMapRoutes = SettlementMapRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortSettlementMap) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: SettlementMapId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: SettlementMapId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: SettlementMapId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: SettlementMapId) = call.application.href(Update(id))

    @Resource("/abstract")
    class AbstractBuildingRoutes(val parent: SettlementMapRoutes = SettlementMapRoutes()) {
        @Resource("edit")
        class Edit(
            val id: SettlementMapId,
            val size: MapSize2d = MapSize2d.square(1),
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )

        @Resource("preview")
        class Preview(val id: SettlementMapId, val parent: AbstractBuildingRoutes = AbstractBuildingRoutes())

        @Resource("add")
        class Add(
            val settlement: SettlementMapId,
            val tileIndex: Int,
            val size: MapSize2d,
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )

        @Resource("remove")
        class Remove(
            val settlement: SettlementMapId,
            val tileIndex: Int,
            val parent: AbstractBuildingRoutes = AbstractBuildingRoutes(),
        )
    }

    @Resource("/building")
    class BuildingRoutes(val parent: SettlementMapRoutes = SettlementMapRoutes()) {
        @Resource("edit")
        class Edit(val id: SettlementMapId, val parent: BuildingRoutes = BuildingRoutes())

        @Resource("preview")
        class Preview(val id: SettlementMapId, val parent: BuildingRoutes = BuildingRoutes())

        @Resource("add")
        class Add(
            val settlement: SettlementMapId,
            val tileIndex: Int,
            val size: MapSize2d,
            val parent: BuildingRoutes = BuildingRoutes(),
        )
    }

    @Resource("/street")
    class StreetRoutes(val parent: SettlementMapRoutes = SettlementMapRoutes()) {
        @Resource("edit")
        class Edit(val id: SettlementMapId, val parent: StreetRoutes = StreetRoutes())

        @Resource("preview")
        class Preview(val id: SettlementMapId, val parent: StreetRoutes = StreetRoutes())

        @Resource("add")
        class Add(
            val id: SettlementMapId,
            val tileIndex: Int,
            val typeId: StreetTemplateId,
            val streetId: StreetId? = null,
            val parent: StreetRoutes = StreetRoutes(),
        )

        @Resource("remove")
        class Remove(
            val id: SettlementMapId,
            val tileIndex: Int,
            val typeId: StreetTemplateId,
            val streetId: StreetId? = null,
            val parent: StreetRoutes = StreetRoutes(),
        )
    }

    @Resource("/terrain")
    class TerrainRoutes(val parent: SettlementMapRoutes = SettlementMapRoutes()) {
        @Resource("edit")
        class Edit(val id: SettlementMapId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("preview")
        class Preview(val id: SettlementMapId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("update")
        class Update(
            val id: SettlementMapId,
            val terrainType: TerrainType,
            val terrainId: Int,
            val tileIndex: Int,
            val parent: TerrainRoutes = TerrainRoutes(),
        )

        @Resource("resize")
        class Resize(val id: SettlementMapId, val parent: TerrainRoutes = TerrainRoutes())
    }
}
