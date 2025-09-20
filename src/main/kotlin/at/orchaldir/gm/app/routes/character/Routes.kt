package at.orchaldir.gm.app.routes.character

import at.orchaldir.gm.core.model.character.CHARACTER_TYPE
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.util.SortCharacter
import io.ktor.resources.*

@Resource("/$CHARACTER_TYPE")
class CharacterRoutes {
    @Resource("all")
    class All(
        val sort: SortCharacter = SortCharacter.Name,
        val parent: CharacterRoutes = CharacterRoutes(),
    )

    @Resource("gallery")
    class Gallery(val parent: CharacterRoutes = CharacterRoutes())

    @Resource("details")
    class Details(val id: CharacterId, val parent: CharacterRoutes = CharacterRoutes())

    @Resource("new")
    class New(val parent: CharacterRoutes = CharacterRoutes())

    @Resource("delete")
    class Delete(val id: CharacterId, val parent: CharacterRoutes = CharacterRoutes())

    @Resource("edit")
    class Edit(val id: CharacterId, val parent: CharacterRoutes = CharacterRoutes())

    @Resource("preview")
    class Preview(val id: CharacterId, val parent: CharacterRoutes = CharacterRoutes())

    @Resource("update")
    class Update(val id: CharacterId, val parent: CharacterRoutes = CharacterRoutes())

    @Resource("/appearance")
    class Appearance(val parent: CharacterRoutes = CharacterRoutes()) {

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
    class Birthday(val parent: CharacterRoutes = CharacterRoutes()) {

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Birthday = Birthday())
    }

    @Resource("/equipment")
    class Equipment(val parent: CharacterRoutes = CharacterRoutes()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Equipment = Equipment())

        @Resource("preview")
        class Preview(val id: CharacterId, val parent: Equipment = Equipment())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Equipment = Equipment())

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Equipment = Equipment())
    }

    @Resource("/name")
    class Name(val parent: CharacterRoutes = CharacterRoutes()) {

        @Resource("generate")
        class Generate(val id: CharacterId, val parent: Name = Name())
    }

    @Resource("/relationship")
    class Relationships(val parent: CharacterRoutes = CharacterRoutes()) {

        @Resource("edit")
        class Edit(val id: CharacterId, val parent: Relationships = Relationships())

        @Resource("preview")
        class Preview(val id: CharacterId, val parent: Relationships = Relationships())

        @Resource("update")
        class Update(val id: CharacterId, val parent: Relationships = Relationships())
    }
}
