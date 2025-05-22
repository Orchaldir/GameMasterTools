package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.util.render.Fill
import at.orchaldir.gm.core.model.util.render.HorizontalStripes
import at.orchaldir.gm.core.model.util.render.Solid
import at.orchaldir.gm.core.model.util.render.Tiles
import at.orchaldir.gm.core.model.util.render.Transparent
import at.orchaldir.gm.core.model.util.render.VerticalStripes
import at.orchaldir.gm.utils.math.Factor

sealed class RenderFill

data class RenderSolid(
    val color: RenderColor,
) : RenderFill()

data class RenderTransparent(
    val color: RenderColor,
    val opacity: Factor,
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

data class RenderTiles(
    val fill: RenderColor,
    val background: RenderColor?,
    val width: Float,
    val borderPercentage: Factor,
) : RenderFill()

fun Fill.toRender(): RenderFill = when (this) {
    is Solid -> RenderSolid(color.toRender())
    is Transparent -> RenderTransparent(color.toRender(), opacity)
    is VerticalStripes -> RenderVerticalStripes(color0.toRender(), color1.toRender(), width)
    is HorizontalStripes -> RenderHorizontalStripes(color0.toRender(), color1.toRender(), width)
    is Tiles -> RenderTiles(fill.toRender(), background?.toRender(), width, borderPercentage)
}

