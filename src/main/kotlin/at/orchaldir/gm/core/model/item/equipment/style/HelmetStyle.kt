package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HelmetStyleType {
    ChainmailHood,
    GreatHelm,
    SkullCap,
}

@Serializable
sealed class HelmetStyle : MadeFromParts {

    fun getType() = when (this) {
        is ChainmailHood -> HelmetStyleType.ChainmailHood
        is GreatHelm -> HelmetStyleType.GreatHelm
        is SkullCap -> HelmetStyleType.SkullCap
    }

    override fun parts() = when (this) {
        is ChainmailHood -> listOf(main)
        is GreatHelm -> listOf(main)
        is SkullCap -> listOf(main)
    }

    override fun mainMaterial() = when (this) {
        is ChainmailHood -> main.material
        is GreatHelm -> main.material()
        is SkullCap -> main.material()
    }
}

@Serializable
@SerialName("Hood")
data class ChainmailHood(
    val shape: HoodBodyShape? = HoodBodyShape.Straight,
    val main: MadeFromMetal = MadeFromMetal(),
) : HelmetStyle()

@Serializable
@SerialName("GreatHelm")
data class GreatHelm(
    val shape: HelmetShape = HelmetShape.Round,
    val eyeHole: EyeHoleShape = EyeHoleShape.Almond,
    val main: ItemPart = MadeFromMetal(),
) : HelmetStyle()

@Serializable
@SerialName("SkullCap")
data class SkullCap(
    val shape: HelmetShape = HelmetShape.Round,
    val front: HelmetFront = NoHelmetFront,
    val main: ItemPart = MadeFromMetal(),
) : HelmetStyle()