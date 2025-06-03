package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.BladeShape
import at.orchaldir.gm.core.model.item.equipment.style.SimpleBlade
import at.orchaldir.gm.core.model.item.equipment.style.SimpleHilt
import at.orchaldir.gm.core.model.item.equipment.style.SwordGrip
import at.orchaldir.gm.core.model.item.equipment.style.SwordGripShape
import at.orchaldir.gm.core.model.item.equipment.style.SwordHilt
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.THIRD
import at.orchaldir.gm.utils.math.TWO_THIRD
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
    visualizeGrip(state, renderer, config, hilt.grip, aabb)
}

private fun visualizeGrip(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: SwordConfig,
    grip: SwordGrip,
    aabb: AABB,
) {
    val fill = grip.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val polygon = createGripPolygon(config, grip, aabb)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createGripPolygon(
    config: SwordConfig,
    grip: SwordGrip,
    aabb: AABB,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (grip.shape) {
        SwordGripShape.Oval -> builder
            .addLeftPoint(aabb, CENTER, START)
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, END)
            .addLeftPoint(aabb, CENTER, END)

        SwordGripShape.Straight -> builder
            .addMirroredPoints(aabb, FULL, START, true)
            .addMirroredPoints(aabb, FULL, END, true)

        SwordGripShape.Waisted -> builder
            .addMirroredPoints(aabb, config.gripThinnerWidth, START, true)
            .addMirroredPoints(aabb, FULL, CENTER, true)
            .addMirroredPoints(aabb, config.gripThinnerWidth, END, true)
    }

    return builder.build()
}
