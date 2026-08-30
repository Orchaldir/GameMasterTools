package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ParryingType {
    Fencing,
    None,
    Normal,
    Unbalanced,
    Undefined,
}

@Serializable
sealed class Parrying {

    fun getType() = when (this) {
        is FencingParrying -> ParryingType.Fencing
        is NoParrying -> ParryingType.None
        is NormalParrying -> ParryingType.Normal
        is UnbalancedParrying -> ParryingType.Unbalanced
        is UndefinedParrying -> ParryingType.Undefined
    }
}

@Serializable
@SerialName("Fencing")
data class FencingParrying(
    val modifier: Int = 0,
) : Parrying()

@Serializable
@SerialName("None")
data object NoParrying : Parrying()

@Serializable
@SerialName("Normal")
data class NormalParrying(
    val modifier: Int = 0,
) : Parrying()

@Serializable
@SerialName("Unbalanced")
data class UnbalancedParrying(
    val modifier: Int = 0,
) : Parrying()

@Serializable
@SerialName("Undefined")
data object UndefinedParrying : Parrying()
