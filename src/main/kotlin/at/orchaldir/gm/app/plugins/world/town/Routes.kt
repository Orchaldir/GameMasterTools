package at.orchaldir.gm.app.plugins.world.town

import at.orchaldir.gm.app.plugins.CultureRoutes
import at.orchaldir.gm.app.plugins.race.RaceRoutes
import at.orchaldir.gm.core.model.culture.CultureId
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

        @Resource("preview")
        class Preview(val id: TownId, val parent: TerrainRoutes = TerrainRoutes())

        @Resource("update")
        class Update(val id: TownId, val parent: TerrainRoutes = TerrainRoutes())
    }
}
