package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Appearance {

    fun getSize() = Size2d.square(
        when (this) {
            is HeadOnly -> this.height
            is HumanoidBody -> this.height
            UndefinedAppearance -> Distance(1.0f)
        }
    )
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
