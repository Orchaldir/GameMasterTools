package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Ornament
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentShape
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleOrnament
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeOrnament(
    state: CharacterRenderState,
    ornament: Ornament,
    position: Point2d,
    radius: Distance,
) = when (ornament) {
    is SimpleOrnament -> visualizeSimpleOrnament(state, ornament, position, radius)
    is OrnamentWithBorder -> visualizeBorderOrnament(state, ornament, position, radius)
}

private fun visualizeSimpleOrnament(
    state: CharacterRenderState,
    ornament: SimpleOrnament,
    position: Point2d,
    radius: Distance,
) {
    val options = NoBorder(ornament.color.toRender())

    visualizeOrnament(state, position, radius, ornament.shape, options)
}

private fun visualizeBorderOrnament(
    state: CharacterRenderState,
    ornament: OrnamentWithBorder,
    position: Point2d,
    radius: Distance,
) {
    val options = FillAndBorder(ornament.color.toRender(), LineOptions(ornament.borderColor.toRender(), radius / 3.0f))

    visualizeOrnament(state, position, radius, ornament.shape, options)
}

private fun visualizeOrnament(
    state: CharacterRenderState,
    position: Point2d,
    radius: Distance,
    shape: OrnamentShape,
    options: RenderOptions,
) {
    val renderer = state.renderer.getLayer()

    when (shape) {
        OrnamentShape.Circle -> renderer.renderCircle(position, radius, options)
        OrnamentShape.Diamond -> doNothing()
        OrnamentShape.Square -> renderer.renderRectangle(AABB.fromCenter(position, radius * 2.0f), options)
    }
}