package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Fill<C>

@Serializable
@SerialName("Solid")
data class Solid<C>(
    val color: C,
) : Fill<C>()

@Serializable
@SerialName("VerticalStripes")
data class VerticalStripes<C>(
    val color0: C,
    val color1: C,
    val width: Size,
) : Fill<C>()
