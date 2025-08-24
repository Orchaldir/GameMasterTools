package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.religion.GodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AuthenticityType {
    Undefined,
    Authentic,
    Invented,
    Mask,
    Secret,
}

@Serializable
sealed class Authenticity {

    fun getType() = when (this) {
        UndefinedAuthenticity -> AuthenticityType.Undefined
        Authentic -> AuthenticityType.Authentic
        Invented -> AuthenticityType.Invented
        is MaskOfOtherGod -> AuthenticityType.Mask
        is SecretIdentity -> AuthenticityType.Secret
    }

    fun isMaskOf(god: GodId) = this is MaskOfOtherGod && this.god == god
    fun isSecretIdentityOf(character: CharacterId) = this is SecretIdentity && this.character == character
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

@Serializable
@SerialName("Secret")
data class SecretIdentity(
    val character: CharacterId,
) : Authenticity()
