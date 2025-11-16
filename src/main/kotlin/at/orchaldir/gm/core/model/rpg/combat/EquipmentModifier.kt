package at.orchaldir.gm.core.model.rpg.combat

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val EQUIPMENT_MODIFIER_TYPE = "Equipment Modifier"

@JvmInline
@Serializable
value class EquipmentModifierId(val value: Int) : Id<EquipmentModifierId> {

    override fun next() = EquipmentModifierId(value + 1)
    override fun type() = EQUIPMENT_MODIFIER_TYPE
    override fun value() = value

}

@Serializable
data class EquipmentModifier(
    val id: EquipmentModifierId,
    val name: Name = Name.init("$EQUIPMENT_MODIFIER_TYPE ${id.value}"),
    val effect: EquipmentModifierEffect = UndefinedEquipmentModifierEffect,
) : ElementWithSimpleName<EquipmentModifierId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = doNothing()
}