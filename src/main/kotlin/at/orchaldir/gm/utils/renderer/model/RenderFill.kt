package at.orchaldir.gm.utils.renderer.model

import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.map.MapSize2d

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

data class RenderTiles(
    val fill: RenderColor,
    val background: RenderColor?,
    val width: UByte = 1u,
    val border: UByte = 1u,
) : RenderFill()

fun Fill.toRender(): RenderFill = when (this) {
    is Solid -> RenderSolid(color.toRender())
    is VerticalStripes -> RenderVerticalStripes(color0.toRender(), color1.toRender(), width)
    is HorizontalStripes -> RenderHorizontalStripes(color0.toRender(), color1.toRender(), width)
    is Tiles -> RenderTiles(fill.toRender(), background?.toRender(), width, border)
}

