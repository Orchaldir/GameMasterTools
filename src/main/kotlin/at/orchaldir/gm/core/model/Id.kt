package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.illness.ILLNESS_TYPE
import at.orchaldir.gm.core.model.magic.SPELL_TYPE
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface BaseId<ID> : Id<ID>

@JvmInline
@Serializable
value class IllnessId(val value: Int) : BaseId<IllnessId> {

    override fun next() = IllnessId(value + 1)
    override fun type() = ILLNESS_TYPE
    override fun plural() = "Illnesses"
    override fun value() = value

}

@JvmInline
@Serializable
value class SpellId(val value: Int) : BaseId<SpellId> {

    override fun next() = SpellId(value + 1)
    override fun type() = SPELL_TYPE
    override fun value() = value

}