package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.ChainmailHood
import at.orchaldir.gm.core.model.item.equipment.style.SkullCap
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HAND_LAYER

data class HelmetConfig(
    val hoodOpeningWidth: Factor,
    val padding: Factor,
)

fun visualizeHelmet(
    state: CharacterRenderState,
    helmet: Helmet,
) {
    val renderer = state.renderer.getLayer(HAND_LAYER)

    when (helmet.style) {
        is ChainmailHood -> visualizeChainmailHood(state, renderer, helmet.style)
        is SkullCap -> doNothing()
    }
}

private fun visualizeChainmailHood(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    hood: ChainmailHood,
) {
    val color = hood.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    if (state.renderFront) {
        val config = state.config.equipment.helmet
        val hoodWidth = FULL + config.padding * 2
        val hoodOpeningWidth = config.hoodOpeningWidth
        val polygon = Polygon2dBuilder()
            .addMirroredPoints(state.aabb, hoodWidth, START, true)
            .addMirroredPoints(state.aabb, hoodWidth, END, true)
            .addMirroredPoints(state.aabb, hoodOpeningWidth, END, true)
            .addMirroredPoints(state.aabb, hoodOpeningWidth, FULL - hoodOpeningWidth)
            .build()

        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderRectangle(state.aabb, options)
    }
}
