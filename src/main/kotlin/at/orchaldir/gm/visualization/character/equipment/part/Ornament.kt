package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Ornament
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentWithBorder
import at.orchaldir.gm.core.model.item.equipment.style.SimpleOrnament
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.utils.visualizeComplexShape

fun <T> visualizeOrnament(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    ornament: Ornament,
    aabb: AABB,
    showBorder: Boolean = false,
) = visualizeOrnament(
    state,
    renderer,
    ornament,
    aabb.getCenter(),
    aabb.getInnerRadius(),
    showBorder,
)

fun <T> visualizeOrnament(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    ornament: Ornament,
    position: Point2d,
    radius: Distance,
    showBorder: Boolean = false,
) = when (ornament) {
    is SimpleOrnament -> visualizeSimpleOrnament(state, renderer, ornament, position, radius, showBorder)
    is OrnamentWithBorder -> visualizeBorderOrnament(state, renderer, ornament, position, radius)
}

private fun <T> visualizeSimpleOrnament(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    ornament: SimpleOrnament,
    position: Point2d,
    radius: Distance,
    showBorder: Boolean = false,
) {
    val options = if (showBorder) {
        state.getFillAndBorder(ornament.part)
    } else {
        state.getNoBorder(ornament.part)
    }

    visualizeComplexShape(renderer, position, radius, ornament.shape, options)
}

private fun <T> visualizeBorderOrnament(
    state: CharacterRenderState<T>,
    renderer: LayerRenderer,
    ornament: OrnamentWithBorder,
    position: Point2d,
    radius: Distance,
) {
    val centerFill = state.getNoBorder(ornament.center)
    val borderColor = state.getNoBorder(ornament.border)

    visualizeComplexShape(renderer, position, radius, ornament.shape, borderColor)
    visualizeComplexShape(renderer, position, radius * 0.66f, ornament.shape, centerFill)
}

