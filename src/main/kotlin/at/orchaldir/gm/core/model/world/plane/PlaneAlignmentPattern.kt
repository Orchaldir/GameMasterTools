package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.core.model.world.plane.PlanarAlignment.*
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
        Waxing -> waxing
        Coterminous -> coterminous
        Waning -> waning
        Remote -> remote
    }

    fun getAlignment(year: Int): PlanarAlignment {
        val length = getLength()
        var relativeYear = year % length

        if (relativeYear < 0) {
            relativeYear += length
        }

        if (relativeYear < waxing) {
            return Waxing
        }

        relativeYear -= waxing

        if (relativeYear < coterminous) {
            return Coterminous
        }

        relativeYear -= coterminous

        if (relativeYear < waning) {
            return Waning
        }

        return Remote
    }

}

@Serializable
@SerialName("Fixed")
data class FixedAlignment(val alignment: PlanarAlignment) : PlaneAlignmentPattern()

@Serializable
@SerialName("Random")
data object RandomAlignment : PlaneAlignmentPattern()
