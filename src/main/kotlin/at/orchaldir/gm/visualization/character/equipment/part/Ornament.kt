package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Ornament
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleOrnament
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.utils.visualizeComplexShape

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
    val fill = ornament.part.getFill(state.state, state.colors)
    val options = NoBorder(fill.toRender())

    visualizeComplexShape(renderer, position, radius, ornament.shape, options)
}

private fun visualizeBorderOrnament(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    ornament: OrnamentWithBorder,
    position: Point2d,
    radius: Distance,
) {
    val centerFill = ornament.center.getFill(state.state, state.colors)
    val borderColor = ornament.border.getColor(state.state, state.colors)
    val options = FillAndBorder(centerFill.toRender(), LineOptions(borderColor.toRender(), radius / 3.0f))

    visualizeComplexShape(renderer, position, radius, ornament.shape, options)
}

