package at.orchaldir.gm.utils.renderer

import kotlinx.serialization.Serializable

@Serializable
data class LineOptions(val color: RenderColor, val width: Float)

@Serializable
sealed class RenderOptions
data class FillAndBorder(
    val fill: RenderColor,
    val border: LineOptions,
) : RenderOptions()

data class NoBorder(val fill: RenderColor) : RenderOptions()
data class BorderOnly(val border: LineOptions) : RenderOptions()
