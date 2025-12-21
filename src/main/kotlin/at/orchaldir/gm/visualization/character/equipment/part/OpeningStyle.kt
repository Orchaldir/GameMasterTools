package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class OpeningConfig(
    val buttonRadius: SizeConfig<Factor>,
    val spaceBetweenColumns: SizeConfig<Factor>,
    val zipperWidth: Factor,
)

fun visualizeOpening(
    state: CharacterRenderState<Body>,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    openingStyle: OpeningStyle,
    layer: Int,
) {
    when (openingStyle) {
        NoOpening -> doNothing()
        is DoubleBreasted -> {
            val spaceBetweenColumns = state.config.equipment.opening.spaceBetweenColumns
                .convert(openingStyle.spaceBetweenColumns)
            val half = spaceBetweenColumns * 0.5f

            visualizeButtons(state, aabb, x - half, topY, bottomY, openingStyle.buttons, layer)
            visualizeButtons(state, aabb, x + half, topY, bottomY, openingStyle.buttons, layer)
        }

        is SingleBreasted -> visualizeButtons(state, aabb, x, topY, bottomY, openingStyle.buttons, layer)
        is Zipper -> visualizeZipper(state, aabb, x, topY, bottomY, openingStyle, layer)
    }
}

fun visualizeButtons(
    state: CharacterRenderState<Body>,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    buttons: ButtonColumn,
    layer: Int,
) {
    val color = buttons.button.part.getColor(state.state, state.colors)
    val options = NoBorder(color.toRender())
    val distance = bottomY - topY
    val step = distance / buttons.count.toFloat()
    var y = topY + step * HALF
    val radius = aabb.convertHeight(state.config.equipment.opening.buttonRadius.convert(buttons.button.size))
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
    val color = zipper.part.getColor(state.state, state.colors)
    val options = LineOptions(color.toRender(), width)
    val top = aabb.getPoint(x, topY)
    val bottom = aabb.getPoint(x, bottomY)

    state.renderer.getLayer(layer).renderLine(listOf(top, bottom), options)
}