package at.orchaldir.gm.core.model.rpg.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ParryingType {
    Normal,
    None,
    Unbalanced,
    Undefined,
}

@Serializable
sealed class Parrying {

    fun getType() = when (this) {
        is NormalParrying -> ParryingType.Normal
        is NoParrying -> ParryingType.None
        is UnbalancedParrying -> ParryingType.Unbalanced
        is UndefinedParrying -> ParryingType.Undefined
    }
}

@Serializable
@SerialName("Normal")
data class NormalParrying(
    val modifier: Int = 0,
) : Parrying()

@Serializable
@SerialName("None")
data object NoParrying : Parrying()

@Serializable
@SerialName("Unbalanced")
data class UnbalancedParrying(
    val modifier: Int = 0,
) : Parrying()


@Serializable
@SerialName("Undefined")
data object UndefinedParrying : Parrying()
