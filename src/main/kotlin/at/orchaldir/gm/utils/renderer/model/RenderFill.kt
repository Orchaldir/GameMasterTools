package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.HorizontalStripes
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.core.model.util.VerticalStripes

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

