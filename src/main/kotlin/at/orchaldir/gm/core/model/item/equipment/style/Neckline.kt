package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val NECKLINES_WITH_SLEEVES = setOf(
    NecklineType.Crew,
    NecklineType.None,
    NecklineType.V,
)

enum class NecklineType {
    Asymmetrical,
    Crew,
    Halter,
    None,
    Opening,
    Strapless,
    V,
}

@Serializable
sealed class Neckline : MadeFromParts {

    fun getType() = when (this) {
        Asymmetrical -> NecklineType.Asymmetrical
        Crew -> NecklineType.Crew
        Halter -> NecklineType.Halter
        NoNeckline -> NecklineType.None
        is NecklineWithOpening -> NecklineType.Opening
        Strapless -> NecklineType.Strapless
        is VNeck -> NecklineType.V
    }

    override fun parts() = when (this) {
        Asymmetrical -> emptyList()
        Crew -> emptyList()
        Halter -> emptyList()
        NoNeckline -> emptyList()
        is NecklineWithOpening -> opening.parts()
        Strapless -> emptyList()
        is VNeck -> emptyList()
    }

    fun addTop() = when (this) {
        Asymmetrical, Halter, Strapless -> false
        else -> true
    }

    fun renderBack() = this is Asymmetrical

    fun supportsSleeves() = NECKLINES_WITH_SLEEVES.contains(getType())

    fun getSupportedSleevesStyles() = if (supportsSleeves()) {
        SleeveStyle.entries
    } else {
        setOf(SleeveStyle.None)
    }
}

@Serializable
@SerialName("Asymmetrical")
data object Asymmetrical : Neckline()

@Serializable
@SerialName("Crew")
data object Crew : Neckline()

@Serializable
@SerialName("Halter")
data object Halter : Neckline()

@Serializable
@SerialName("None")
data object NoNeckline : Neckline()

@Serializable
@SerialName("Opening")
data class NecklineWithOpening(
    val opening: Opening,
) : Neckline()

@Serializable
@SerialName("Strapless")
data object Strapless : Neckline()

@Serializable
@SerialName("V")
data class VNeck(
    val size: Size = Size.Medium,
) : Neckline()
