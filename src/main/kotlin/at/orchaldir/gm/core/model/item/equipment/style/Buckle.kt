package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BuckleType {
    NoBuckle,
    Simple,
}

@Serializable
sealed class Buckle : MadeFromParts {

    fun getType() = when (this) {
        NoBuckle -> BuckleType.NoBuckle
        is SimpleBuckle -> BuckleType.Simple
    }
}

@Serializable
@SerialName("NoBuckle")
data object NoBuckle : Buckle()

@Serializable
@SerialName("Simple")
data class SimpleBuckle(
    val shape: BuckleShape = BuckleShape.Rectangle,
    val size: Size = Size.Small,
    val part: ItemPart = MadeFromWood(Color.Gray),
) : Buckle() {

    override fun parts() = listOf(part)
}
