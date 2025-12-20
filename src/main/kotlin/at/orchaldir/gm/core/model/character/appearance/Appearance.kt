package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.tail.NoTails
import at.orchaldir.gm.core.model.character.appearance.tail.Tails
import at.orchaldir.gm.core.model.character.appearance.wing.NoWings
import at.orchaldir.gm.core.model.character.appearance.wing.Wings
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DEFAULT_HUMANOID_HEIGHT = Distance.fromCentimeters(180)

enum class AppearanceType {
    Body,
    HeadOnly,
}

@Serializable
sealed class Appearance {

    fun getType() = when (this) {
        is HeadOnly -> AppearanceType.HeadOnly
        else -> AppearanceType.Body
    }

    fun getHeightFromSub() = when (this) {
        is HeadOnly -> this.height
        is HumanoidBody -> this.height
        UndefinedAppearance -> Distance.fromMillimeters(1)
    }

    fun getSize2d() = Size2d.square(getHeightFromSub())

    fun with(size: Distance): Appearance = when (this) {
        is HeadOnly -> this.copy(height = size)
        is HumanoidBody -> this.copy(height = size)
        UndefinedAppearance -> UndefinedAppearance
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedAppearance : Appearance()

@Serializable
@SerialName("HeadOnly")
data class HeadOnly(
    val head: Head,
    val height: Distance,
    val skin: Skin = NormalSkin(),
) : Appearance()

@Serializable
@SerialName("Humanoid")
data class HumanoidBody(
    val body: Body = Body(),
    val head: Head = Head(),
    val height: Distance = DEFAULT_HUMANOID_HEIGHT,
    val skin: Skin = NormalSkin(),
    val tails: Tails = NoTails,
    val wings: Wings = NoWings,
) : Appearance()
