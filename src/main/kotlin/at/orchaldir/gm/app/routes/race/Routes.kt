package at.orchaldir.gm.app.routes.race

import at.orchaldir.gm.app.routes.item.TextRoutes
import at.orchaldir.gm.core.model.race.RACE_TYPE
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.race.appearance.RaceAppearanceId
import at.orchaldir.gm.core.model.util.SortRace
import io.ktor.resources.*

@Resource("/$RACE_TYPE")
class RaceRoutes {
    @Resource("all")
    class All(
        val sort: SortRace = SortRace.Name,
        val parent: RaceRoutes = RaceRoutes(),
    )

    @Resource("gallery")
    class Gallery(val parent: RaceRoutes = RaceRoutes())

    @Resource("details")
    class Details(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

    @Resource("new")
    class New(val parent: RaceRoutes = RaceRoutes())

    @Resource("clone")
    class Clone(val id: RaceId, val parent: RaceRoutes = RaceRoutes())

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

        @Resource("clone")
        class Clone(val id: RaceAppearanceId, val parent: AppearanceRoutes = AppearanceRoutes())

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
