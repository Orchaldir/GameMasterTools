package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AuthenticityType {
    Undefined,
    Authentic,
    Invented,
    Mask,
}

@Serializable
sealed class Authenticity {

    fun getType() = when (this) {
        UndefinedAuthenticity -> AuthenticityType.Undefined
        Invented -> AuthenticityType.Invented
        is MaskOfOtherGod -> AuthenticityType.Mask
        Authentic -> AuthenticityType.Authentic
    }
}

@Serializable
@SerialName("Undefined")
data object UndefinedAuthenticity : Authenticity()

@Serializable
@SerialName("Authentic")
data object Authentic : Authenticity()

@Serializable
@SerialName("Invented")
data object Invented : Authenticity()

@Serializable
@SerialName("Mask")
data class MaskOfOtherGod(
    val god: GodId,
) : Authenticity()
