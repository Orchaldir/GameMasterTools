package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val SPELL_GROUP_TYPE = "Spell Group"

@JvmInline
@Serializable
value class SpellGroupId(val value: Int) : Id<SpellGroupId> {

    override fun next() = SpellGroupId(value + 1)
    override fun type() = SPELL_GROUP_TYPE
    override fun value() = value

}

@Serializable
data class SpellGroup(
    val id: SpellGroupId,
    val name: Name = Name.init("Spell Group ${id.value}"),
    val spells: Set<SpellId> = emptySet(),
) : ElementWithSimpleName<SpellGroupId> {

    override fun id() = id
    override fun name() = name.text

}