package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Equipped
import at.orchaldir.gm.core.model.character.UndefinedEquipped
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.character.validateEquipped
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val UNIFORM_TYPE = "Uniform"

@JvmInline
@Serializable
value class UniformId(val value: Int) : Id<UniformId> {

    override fun next() = UniformId(value + 1)
    override fun type() = UNIFORM_TYPE
    override fun value() = value

}

@Serializable
data class Uniform(
    val id: UniformId,
    val name: Name = Name.init(id),
    val equipped: Equipped = UndefinedEquipped,
) : ElementWithSimpleName<UniformId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
        state.getUniformStorage().require(id)

        validateEquipped(state, equipped, UndefinedStatblockLookup)
    }
}