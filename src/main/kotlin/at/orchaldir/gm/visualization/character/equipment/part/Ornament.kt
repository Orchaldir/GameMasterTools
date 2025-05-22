package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.Ornament
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleOrnament
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.createCross
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.*
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeOrnament(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    ornament: Ornament,
    position: Point2d,
    radius: Distance,
) = when (ornament) {
    is SimpleOrnament -> visualizeSimpleOrnament(state, renderer, ornament, position, radius)
    is OrnamentWithBorder -> visualizeBorderOrnament(state, renderer, ornament, position, radius)
}

private fun visualizeSimpleOrnament(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    ornament: SimpleOrnament,
    position: Point2d,
    radius: Distance,
) {
    val fill = ornament.part.getFill(state.state)
    val options = NoBorder(fill.toRender())

    visualizeOrnament(renderer, position, radius, ornament.shape, options)
}

private fun visualizeBorderOrnament(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    ornament: OrnamentWithBorder,
    position: Point2d,
    radius: Distance,
) {
    val centerFill = ornament.center.getFill(state.state)
    val borderColor = ornament.border.getColor(state.state, state.colors)
    val options = FillAndBorder(centerFill.toRender(), LineOptions(borderColor.toRender(), radius / 3.0f))

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
        OrnamentShape.Cross -> {
            val polygon = createCross(position, radius * 2.0f)
            renderer.renderPolygon(polygon, options)
        }

        OrnamentShape.Diamond -> renderer.renderDiamond(AABB.fromCenter(position, radius * 2.0f), options)
        OrnamentShape.Square -> renderer.renderRectangle(AABB.fromCenter(position, radius * 2.0f), options)
        OrnamentShape.Teardrop -> renderer.renderTeardrop(
            AABB.fromWidthAndHeight(position, radius, radius * 2.0f),
            options
        )
    }
}
