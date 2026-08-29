package at.orchaldir.gm.core.model.util.quantity

import at.orchaldir.gm.utils.math.RangeInt
import kotlinx.serialization.Serializable

@Serializable
data class ModifiedDiceRange(
    val dice: RangeInt,
    val modifier: RangeInt,
) {
    fun validate() {
        dice.validate("dice")
        modifier.validate("modifier")
    }
}
