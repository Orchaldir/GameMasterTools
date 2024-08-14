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

    @Resource("/appearance")
    class Appearance(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Appearance = Appearance())

        @Resource("preview")
        class Preview(val id: CharacterId, val parent: Appearance = Appearance())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Appearance = Appearance())

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Appearance = Appearance())
    }

    @Resource("/birthday")
    class Birthday(val parent: Characters = Characters()) {

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Birthday = Birthday())
    }

    @Resource("/equipment")
    class Equipment(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Equipment = Equipment())

        @Resource("preview")
        class Preview(val id: CharacterId, val parent: Equipment = Equipment())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Equipment = Equipment())

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Equipment = Equipment())
    }

    @Resource("/languages")
    class Languages(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Languages = Languages())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Languages = Languages())
    }

    @Resource("/name")
    class Name(val parent: Characters = Characters()) {

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Name = Name())
    }

    @Resource("/relationship")
    class Relationships(val parent: Characters = Characters()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Relationships = Relationships())

        @Resource("preview")
        class Preview(val id: CharacterId, val parent: Relationships = Relationships())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Relationships = Relationships())
    }
}
