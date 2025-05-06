package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.EquipmentMap
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
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
    val name: Name = Name.init("Uniform ${id.value}"),
    val equipmentMap: EquipmentMap<EquipmentId> = EquipmentMap(),
) : ElementWithSimpleName<UniformId> {

    override fun id() = id
    override fun name() = name.text
}