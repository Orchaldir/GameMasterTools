package at.orchaldir.gm.core.model.rpg.loot

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val LOOT_TYPE = "Loot"

@JvmInline
@Serializable
value class LootId(val value: Int) : Id<LootId> {

    override fun next() = LootId(value + 1)
    override fun type() = LOOT_TYPE
    override fun value() = value

}

@Serializable
data class Loot(
    val id: LootId,
    val name: Name = Name.init(id),
    val entry: LootEntry = NoLoot,
) : ElementWithSimpleName<LootId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        entry.validate(state, id)
    }
}