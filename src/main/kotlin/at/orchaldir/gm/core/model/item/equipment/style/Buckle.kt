package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
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
    val part: FillItemPart = FillItemPart(Color.Gray),
) : Buckle() {

    override fun parts() = listOf(part)
}
