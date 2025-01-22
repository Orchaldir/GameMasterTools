package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AppearanceType {
    Body,
    HeadOnly,
}

@Serializable
sealed class Appearance {

    fun getSize() = when (this) {
        is HeadOnly -> this.height
        is HumanoidBody -> this.height
        UndefinedAppearance -> Distance.fromMeters(1.0f)
    }

    fun getSize2d() = Size2d.square(getSize())

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
) : Appearance()

@Serializable
@SerialName("Humanoid")
data class HumanoidBody(
    val body: Body,
    val head: Head,
    val height: Distance,
) : Appearance()
