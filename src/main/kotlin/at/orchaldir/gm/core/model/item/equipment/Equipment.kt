package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.CalculatedPrice
import at.orchaldir.gm.core.model.economy.PriceLookup
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.render.ColorSchemeId
import at.orchaldir.gm.core.reducer.item.validateEquipment
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.CalculatedWeight
import at.orchaldir.gm.utils.math.unit.WeightLookup
import kotlinx.serialization.Serializable

const val EQUIPMENT_TYPE = "Equipment"
const val MIN_EQUIPMENT_WEIGHT = 10L
const val MAX_EQUIPMENT_WEIGHT = 1_000_000L

const val MIN_EQUIPMENT_PRICE = 0
const val MAX_EQUIPMENT_PRICE = 1_000_000_000

@JvmInline
@Serializable
value class EquipmentId(val value: Int) : Id<EquipmentId> {

    override fun next() = EquipmentId(value + 1)
    override fun type() = EQUIPMENT_TYPE
    override fun value() = value

}

@Serializable
data class Equipment(
    val id: EquipmentId,
    val name: Name = Name.init(id),
    val data: EquipmentData = Belt(),
    val weight: WeightLookup = CalculatedWeight,
    val colorSchemes: Set<ColorSchemeId> = emptySet(),
) : ElementWithSimpleName<EquipmentId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = validateEquipment(state, this)

    fun slots() = data.slots()

    fun canEquip() = data.slots().isNotEmpty()

    fun areColorSchemesValid() = data.requiredSchemaColors() == 0 || colorSchemes.isNotEmpty()

}