package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ShaftType {
    Simple,
}

@Serializable
sealed class Shaft : MadeFromParts {

    fun getType() = when (this) {
        is SimpleShaft -> ShaftType.Simple
    }

    override fun mainMaterial() = when (this) {
        is SimpleShaft -> part.material()
    }
}

@Serializable
@SerialName("Simple")
data class SimpleShaft(
    val part: ItemPart = MadeFromWood(),
) : Shaft() {

    override fun parts() = listOf(part)

}
