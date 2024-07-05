package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.item.EquipmentSlot.Bottom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquipmentType {
    None,
    Pants,
}

@Serializable
sealed class Equipment {
    open fun slots(): Set<EquipmentSlot> = emptySet()
}

@Serializable
@SerialName("None")
data object NoEquipment : Equipment()

enum class PantsStyle {
    Bermuda,
    HotPants,
    Regular,
    Shorts,
}

@Serializable
@SerialName("Pants")
data class Pants(
    val style: PantsStyle = PantsStyle.Regular,
    val color: Color = Color.SaddleBrown,
) : Equipment() {

    override fun slots() = setOf(Bottom)
}


