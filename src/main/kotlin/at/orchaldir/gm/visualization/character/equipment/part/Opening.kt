package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.common.SewingPattern
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.utils.SewingPatternConfig
import at.orchaldir.gm.visualization.utils.visualizeSewingPattern

data class OpeningConfig(
    val buttonRadius: SizeConfig<Factor>,
    val laceUp: SewingPatternConfig,
    val spaceBetweenColumns: SizeConfig<Factor>,
    val zipperWidth: Factor,
) {
    fun getWidthFactor(size: Size) = spaceBetweenColumns.convert(size)
    fun getWidth(aabb: AABB, size: Size) = aabb.convertWidth(getWidthFactor(size))
}

fun visualizeOpening(
    state: CharacterRenderState<Body>,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    opening: Opening,
    layer: Int,
) {
    val config = state.config.equipment.opening

    when (opening) {
        NoOpening -> doNothing()
        is DoubleBreasted -> {
            val width = config.getWidthFactor(opening.width)
            val half = width * 0.5f

            visualizeButtons(state, aabb, x - half, topY, bottomY, opening.buttons, layer)
            visualizeButtons(state, aabb, x + half, topY, bottomY, opening.buttons, layer)
        }

        is SingleBreasted -> visualizeButtons(state, aabb, x, topY, bottomY, opening.buttons, layer)
        is LaceUp -> visualizeLacePattern(state, config, aabb, x, topY, bottomY, layer, opening.pattern)
        is Shoelace -> visualizeLacePattern(state, config, aabb, x, topY, bottomY, layer, opening.pattern)
        is Zipper -> visualizeZipper(state, aabb, x, topY, bottomY, opening, layer)
    }
}

private fun visualizeLacePattern(
    state: CharacterRenderState<Body>,
    config: OpeningConfig,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    layer: Int,
    pattern: SewingPattern,
) = visualizeSewingPattern(
    state,
    config.laceUp,
    aabb.getPoint(x, topY),
    aabb.getPoint(x, bottomY),
    config.getWidth(aabb, Size.Medium),
    pattern,
    layer,
)

fun visualizeButtons(
    state: CharacterRenderState<Body>,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    buttons: ButtonColumn,
    layer: Int,
) {
    val options = state.getNoBorder(buttons.button.main)
    val distance = bottomY - topY
    val step = distance / buttons.count.toFloat()
    var y = topY + step * HALF
    val sizeFactor = state.config.equipment.opening.buttonRadius.convert(buttons.button.size)
    val radius = state.fullAABB.convertHeight(sizeFactor)
    val renderer = state.renderer.getLayer(layer)

    for (i in 0..<buttons.count.toInt()) {
        val center = aabb.getPoint(x, y)
        renderer.renderCircle(center, radius, options)
        y += step
    }
}

fun visualizeZipper(
    state: CharacterRenderState<Body>,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    zipper: Zipper,
    layer: Int,
) {
    val width = aabb.convertHeight(state.config.equipment.opening.zipperWidth)
    val color = zipper.main.getColor(state.state, state.colors)
    val options = LineOptions(color.toRender(), width)
    val top = aabb.getPoint(x, topY)
    val bottom = aabb.getPoint(x, bottomY)

    state.renderer.getLayer(layer).renderLine(listOf(top, bottom), options)
}