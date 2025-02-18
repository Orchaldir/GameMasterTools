package at.orchaldir.gm.core.model.world.plane

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PlaneAlignmentPatternType {
    Cycle,
    Fixed,
    Random,
}

@Serializable
sealed class PlaneAlignmentPattern {

    fun getType() = when (this) {
        is FixedAlignment -> PlaneAlignmentPatternType.Fixed
        is PlanarCycle -> PlaneAlignmentPatternType.Cycle
        RandomAlignment -> PlaneAlignmentPatternType.Random
    }
}

@Serializable
@SerialName("Cycle")
data class PlanarCycle(
    val waxing: Int,
    val coterminous: Int,
    val waning: Int,
    val remote: Int,
) : PlaneAlignmentPattern() {

    fun getLength() = waxing + coterminous + waning + remote

    fun getValue(alignment: PlanarAlignment) = when (alignment) {
        PlanarAlignment.Waxing -> waxing
        PlanarAlignment.Coterminous -> coterminous
        PlanarAlignment.Waning -> waning
        PlanarAlignment.Remote -> remote
    }

    fun getAlignment(year: Int): PlanarAlignment {
        var relativeYear = year % getLength()

        if (relativeYear < waxing) {
            return PlanarAlignment.Waxing
        }

        relativeYear -= waxing

        if (relativeYear < coterminous) {
            return PlanarAlignment.Coterminous
        }

        relativeYear -= coterminous

        if (relativeYear < waning) {
            return PlanarAlignment.Waning
        }

        return PlanarAlignment.Remote
    }

}

@Serializable
@SerialName("Fixed")
data class FixedAlignment(val alignment: PlanarAlignment) : PlaneAlignmentPattern()

@Serializable
@SerialName("Random")
data object RandomAlignment : PlaneAlignmentPattern()
