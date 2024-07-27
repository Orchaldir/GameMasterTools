package at.orchaldir.gm.core.model.appearance

import kotlinx.serialization.Serializable

@Serializable
sealed class Fill<C>

data class Solid<C>(
    val color: C,
) : Fill<C>()

data class VerticalStripes<C>(
    val color0: C,
    val color1: C,
    val width0: Size,
    val width1: Size,
) : Fill<C>()
