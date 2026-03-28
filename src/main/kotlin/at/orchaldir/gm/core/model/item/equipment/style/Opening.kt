package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALL_OPENINGS = OpeningType.entries - OpeningType.NoOpening

enum class OpeningType {
    NoOpening,
    SingleBreasted,
    DoubleBreasted,
    LaceUp,
    Zipper,
}

@Serializable
sealed class Opening : MadeFromParts {

    fun getType() = when (this) {
        NoOpening -> OpeningType.NoOpening
        is SingleBreasted -> OpeningType.SingleBreasted
        is DoubleBreasted -> OpeningType.DoubleBreasted
        is LaceUp -> OpeningType.LaceUp
        is Zipper -> OpeningType.Zipper
    }

    override fun parts() = when (this) {
        NoOpening -> emptyList()
        is SingleBreasted -> buttons.parts()
        is DoubleBreasted -> buttons.parts()
        is LaceUp -> pattern.parts()
        is Zipper -> listOf(main)
    }
}

@Serializable
@SerialName("NoOpening")
data object NoOpening : Opening()

@Serializable
@SerialName("SingleBreasted")
data class SingleBreasted(
    val buttons: ButtonColumn = ButtonColumn(),
) : Opening()

@Serializable
@SerialName("DoubleBreasted")
data class DoubleBreasted(
    val buttons: ButtonColumn = ButtonColumn(),
    val width: Size = Size.Medium,
) : Opening()

@Serializable
@SerialName("Lace")
data class LaceUp(
    val pattern: SewingPattern,
) : Opening()

@Serializable
@SerialName("Zipper")
data class Zipper(
    val main: ItemPart = MadeFromMetal(),
) : Opening()