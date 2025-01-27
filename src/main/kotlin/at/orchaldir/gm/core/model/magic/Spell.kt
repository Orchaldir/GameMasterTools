package at.orchaldir.gm.core.model.magic

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val SPELL_TYPE = "Spell"

@JvmInline
@Serializable
value class SpellId(val value: Int) : Id<SpellId> {

    override fun next() = SpellId(value + 1)
    override fun type() = SPELL_TYPE
    override fun value() = value

}

@Serializable
data class Spell(
    val id: SpellId,
    val name: String = "Spell ${id.value}",
    val date: Date? = null,
) : ElementWithSimpleName<SpellId>, HasStartDate {

    override fun id() = id
    override fun name() = name
    override fun startDate() = date

}