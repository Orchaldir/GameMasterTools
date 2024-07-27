package at.orchaldir.gm.core.model.appearance

import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.Serializable

@Serializable
sealed class Fill

data class Solid(
    val color: Color,
) : Fill()

data class VerticalStripes(
    val color0: Color,
    val color1: Color,
    val width0: Size,
    val width1: Size,
) : Fill()
