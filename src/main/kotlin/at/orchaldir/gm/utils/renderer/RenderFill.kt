package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.core.model.appearance.*

sealed class RenderFill

data class RenderSolid(
    val color: RenderColor,
) : RenderFill()

data class RenderVerticalStripes(
    val color0: RenderColor,
    val color1: RenderColor,
    val width: UByte,
) : RenderFill()

data class RenderHorizontalStripes(
    val color0: RenderColor,
    val color1: RenderColor,
    val width: UByte,
) : RenderFill()

fun Fill.toRender(): RenderFill = when (this) {
    is Solid -> RenderSolid(this.color.toRender())
    is VerticalStripes -> RenderVerticalStripes(this.color0.toRender(), this.color1.toRender(), this.width)
    is HorizontalStripes -> RenderHorizontalStripes(this.color0.toRender(), this.color1.toRender(), this.width)
}

