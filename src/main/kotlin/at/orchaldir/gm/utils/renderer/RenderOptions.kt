package at.orchaldir.gm.utils.renderer

import kotlinx.serialization.Serializable

@Serializable
sealed class RenderOptions
data class FillAndBorder(
    val fill: RenderColor,
    val border: RenderColor,
    val lineWidth: UInt,
) : RenderColor()

data class NoBorder(val fill: RenderColor) : RenderColor()
data class BorderOnly(val border: RenderColor, val lineWidth: UInt) : RenderColor()
