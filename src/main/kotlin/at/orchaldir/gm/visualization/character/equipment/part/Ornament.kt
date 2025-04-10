package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Ornament
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleOrnament
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions

fun visualizeOrnament(
    renderer: LayerRenderer,
    ornament: Ornament,
    position: Point2d,
    radius: Distance,
) = when (ornament) {
    is SimpleOrnament -> visualizeSimpleOrnament(renderer, ornament, position, radius)
    is OrnamentWithBorder -> visualizeBorderOrnament(renderer, ornament, position, radius)
}

private fun visualizeSimpleOrnament(
    renderer: LayerRenderer,
    ornament: SimpleOrnament,
    position: Point2d,
    radius: Distance,
) {
    val options = NoBorder(ornament.color.toRender())

    visualizeOrnament(renderer, position, radius, ornament.shape, options)
}

private fun visualizeBorderOrnament(
    renderer: LayerRenderer,
    ornament: OrnamentWithBorder,
    position: Point2d,
    radius: Distance,
) {
    val options = FillAndBorder(ornament.color.toRender(), LineOptions(ornament.borderColor.toRender(), radius / 3.0f))

    visualizeOrnament(renderer, position, radius, ornament.shape, options)
}

private fun visualizeOrnament(
    renderer: LayerRenderer,
    position: Point2d,
    radius: Distance,
    shape: OrnamentShape,
    options: RenderOptions,
) {
    when (shape) {
        OrnamentShape.Circle -> renderer.renderCircle(position, radius, options)
        OrnamentShape.Diamond -> renderer.renderDiamond(AABB.fromCenter(position, radius * 2.0f), options)
        OrnamentShape.Square -> renderer.renderRectangle(AABB.fromCenter(position, radius * 2.0f), options)
        OrnamentShape.Teardrop -> renderer.renderTeardrop(
            AABB.fromWidthAndHeight(position, radius, radius * 2.0f),
            options
        )
    }
}

fun visualizeWire(
    renderer: LayerRenderer,
    top: Point2d,
    bottom: Point2d,
    thickness: Distance,
    color: Color,
) {
    val wireOptions = LineOptions(color.toRender(), thickness)

    renderer.renderLine(listOf(top, bottom), wireOptions)
}