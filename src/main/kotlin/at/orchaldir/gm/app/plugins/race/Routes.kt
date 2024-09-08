package at.orchaldir.gm.app.plugins.race

import at.orchaldir.gm.core.model.race.RaceId
import io.ktor.resources.*

@Resource("/races")
class RaceRoutes {
    @Resource("details")
    class Details(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("new")
    class New(val parent: RaceRoutes = RaceRoutes())

    @Resource("delete")
    class Delete(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("edit")
    class Edit(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("preview")
    class Preview(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("update")
    class Update(val id: RaceId, val parent: RaceRoutes = RaceRoutes())
}
