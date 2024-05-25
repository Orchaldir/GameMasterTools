package at.orchaldir.gm.app.plugins.character

import at.orchaldir.gm.core.model.character.CharacterId
import io.ktor.resources.*

@Resource("/characters")
class Characters {

    @Resource("details")
    class Details(val id: CharacterId, val parent: Characters = Characters())

    @Resource("new")
    class New(val parent: Characters = Characters())

    @Resource("delete")
    class Delete(val id: CharacterId, val parent: Characters = Characters())

    @Resource("edit")
    class Edit(val id: CharacterId, val parent: Characters = Characters())

    @Resource("preview")
    class Preview(val id: CharacterId, val parent: Characters = Characters())

    @Resource("update")
    class Update(val id: CharacterId, val parent: Characters = Characters())

    @Resource("/languages")
    class Languages(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Languages = Languages())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Languages = Languages())
    }

    @Resource("/relationships")
    class Relationships(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Relationships = Relationships())

        @Resource("preview")
        class Preview(val id: CharacterId, val parent: Relationships = Relationships())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Relationships = Relationships())
    }
}
