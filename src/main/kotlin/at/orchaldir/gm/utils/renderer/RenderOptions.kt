package at.orchaldir.gm.utils.renderer

import kotlinx.serialization.Serializable

@Serializable
sealed class RenderOptions
data class FillAndBorder(
    val fill: RenderColor,
    val border: RenderColor,
    val lineWidth: UInt,
) : RenderOptions()

data class NoBorder(val fill: RenderColor) : RenderOptions()
data class BorderOnly(val border: RenderColor, val lineWidth: UInt) : RenderOptions()
