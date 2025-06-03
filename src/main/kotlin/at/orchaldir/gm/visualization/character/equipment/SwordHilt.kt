package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.SimpleHilt
import at.orchaldir.gm.core.model.item.equipment.style.SwordHilt
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState

fun visualizeSwordHilt(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    hilt: SwordHilt,
    aabb: AABB,
) {
    when (hilt) {
        is SimpleHilt -> visualizeSimpleHilt(state, renderer, config, hilt, aabb)
    }
}

private fun visualizeSimpleHilt(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    hilt: SimpleHilt,
    aabb: AABB,
) {
    val fill = hilt.grip.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    renderer.renderRectangle(aabb, options)
}
