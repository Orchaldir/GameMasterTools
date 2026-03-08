package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.CircularArrangement
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.shape.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOWED_FLAIL_HEADS = listOf(
    ClubHeadType.MorningStar,
    ClubHeadType.Simple,
    ClubHeadType.SpikedMace,
)

enum class ClubHeadType {
    None,
    Simple,
    SimpleFlanged,
    ComplexFlanged,
    SpikedMace,
    Flail,
    MorningStar,
    Warhammer,
}

@Serializable
sealed interface ClubHead : MadeFromParts {

    fun getType() = when (this) {
        is NoClubHead -> ClubHeadType.None
        is SimpleClubHead -> ClubHeadType.Simple
        is SimpleFlangedHead -> ClubHeadType.SimpleFlanged
        is ComplexFlangedHead -> ClubHeadType.ComplexFlanged
        is SpikedMaceHead -> ClubHeadType.SpikedMace
        is FlailHead -> ClubHeadType.Flail
        is MorningStarHead -> ClubHeadType.MorningStar
        is WarhammerHead -> ClubHeadType.Warhammer
    }

    override fun parts(): List<ItemPart> = when (this) {
        is NoClubHead -> emptyList()
        is SimpleClubHead -> listOf(main)
        is SimpleFlangedHead -> listOf(main)
        is ComplexFlangedHead -> listOf(main)
        is SpikedMaceHead -> listOf(spike.main)
        is FlailHead -> connection.parts() + head.parts()
        is MorningStarHead -> listOf(main, spikes.item.main)
        is WarhammerHead -> listOf(main, spike.main)
    }

    override fun mainMaterial(): MaterialId? = when (this) {
        is NoClubHead -> null
        is SimpleClubHead -> main.material()
        is SimpleFlangedHead -> main.material()
        is ComplexFlangedHead -> main.material()
        is SpikedMaceHead -> spike.main.material()
        is FlailHead -> head.mainMaterial()
        is MorningStarHead -> spikes.item.main.material()
        is WarhammerHead -> main.material()
    }
}

@Serializable
@SerialName("None")
data object NoClubHead : ClubHead

@Serializable
@SerialName("Simple")
data class SimpleClubHead(
    val shape: ComplexShape = UsingCircularShape(),
    val main: ItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("SimpleFlanged")
data class SimpleFlangedHead(
    val shape: ComplexShape = UsingCircularShape(),
    val main: ItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("ComplexFlanged")
data class ComplexFlangedHead(
    val shape: RotatedShape,
    val main: ItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("SpikedMace")
data class SpikedMaceHead(
    val spike: Spike,
    val main: Int,
) : ClubHead

@Serializable
@SerialName("MorningStar")
data class MorningStarHead(
    val spikes: CircularArrangement<Spike>,
    val main: ItemPart = ColorSchemeItemPart(),
) : ClubHead

@Serializable
@SerialName("Flail")
data class FlailHead(
    val head: ClubHead,
    val connection: LineStyle,
) : ClubHead

@Serializable
@SerialName("Warhammer")
data class WarhammerHead(
    val spike: Spike,
    val shape: ComplexShape = UsingRectangularShape(RectangularShape.Rectangle, FULL),
    val main: ItemPart = ColorSchemeItemPart(),
) : ClubHead