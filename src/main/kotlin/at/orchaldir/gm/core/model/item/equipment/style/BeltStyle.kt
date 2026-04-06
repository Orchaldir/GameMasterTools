package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromLeather
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BeltStyleType {
    BuckleAndStrap,
    Rope,
}

@Serializable
sealed class BeltStyle : MadeFromParts {

    fun getType() = when (this) {
        is BuckleAndStrap -> BeltStyleType.BuckleAndStrap
        is RopeBelt -> BeltStyleType.Rope
    }

    override fun parts() = when (this) {
        is BuckleAndStrap -> buckle.parts() + strap
        is RopeBelt -> listOf(main)
    }
}

@Serializable
@SerialName("BuckleAndStrap")
data class BuckleAndStrap(
    val buckle: Buckle = SimpleBuckle(),
    val strap: ItemPart = MadeFromLeather(Color.SaddleBrown),
    val holes: BeltHoles = NoBeltHoles,
) : BeltStyle()

@Serializable
@SerialName("Rope")
data class RopeBelt(
    val main: ItemPart,
    val length: Size = Size.Medium,
) : BeltStyle()
