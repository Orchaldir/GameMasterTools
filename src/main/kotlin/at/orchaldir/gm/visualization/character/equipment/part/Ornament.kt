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
    val fill = ornament.part.getFill(state.state, state.colors)
    val options = if (showBorder) {
        state.config.getLineOptions(fill)
    } else {
        NoBorder(fill.toRender())
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
    val centerFill = ornament.center.getFill(state.state, state.colors)
    val borderColor = ornament.border.getColor(state.state, state.colors)
    val options = FillAndBorder(centerFill.toRender(), LineOptions(borderColor.toRender(), radius / 3.0f))

    visualizeComplexShape(renderer, position, radius, ornament.shape, options)
}

