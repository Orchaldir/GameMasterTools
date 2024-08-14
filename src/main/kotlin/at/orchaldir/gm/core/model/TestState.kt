package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

data class TestState(
    val map: Map<ElementType, Storage<*, *>> = ElementType.entries.associateWith { it.createStorage() },
) {
    fun getCharacters() = getStorage<CharacterId, Character>(ElementType.Character)

    private fun <ID : Id<ID>, ELEMENT : Element<ID>> getStorage(type: ElementType): Storage<ID, ELEMENT> {
        val storage = map[type]

        if (storage != null) {
            return storage as Storage<ID, ELEMENT>
        }

        error("fail")
    }
}
