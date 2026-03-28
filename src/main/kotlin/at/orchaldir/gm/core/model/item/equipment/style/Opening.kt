package at.orchaldir.gm.core.model.item.equipment.style

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
    Zipper,
}

@Serializable
sealed class Opening : MadeFromParts {

    fun getType() = when (this) {
        NoOpening -> OpeningType.NoOpening
        is SingleBreasted -> OpeningType.SingleBreasted
        is DoubleBreasted -> OpeningType.DoubleBreasted
        is Zipper -> OpeningType.Zipper
    }

    override fun parts() = when (this) {
        NoOpening -> emptyList()
        is SingleBreasted -> buttons.parts()
        is DoubleBreasted -> buttons.parts()
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
    val spaceBetweenColumns: Size = Size.Medium,
) : Opening()

@Serializable
@SerialName("Zipper")
data class Zipper(
    val main: ItemPart = MadeFromMetal(),
) : Opening()