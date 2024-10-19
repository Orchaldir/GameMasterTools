package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
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

    @Resource("/appearance")
    class AppearanceRoutes(val parent: RaceRoutes = RaceRoutes()) {
        @Resource("details")
        class Details(val id: RaceAppearanceId, val parent: AppearanceRoutes = AppearanceRoutes())

        @Resource("new")
        class New(val parent: AppearanceRoutes = AppearanceRoutes())

        @Resource("delete")
        class Delete(val id: RaceAppearanceId, val parent: AppearanceRoutes = AppearanceRoutes())

        @Resource("edit")
        class Edit(val id: RaceAppearanceId, val parent: AppearanceRoutes = AppearanceRoutes())

        @Resource("preview")
        class Preview(val id: RaceAppearanceId, val parent: AppearanceRoutes = AppearanceRoutes())

        @Resource("update")
        class Update(val id: RaceAppearanceId, val parent: AppearanceRoutes = AppearanceRoutes())
    }
}
