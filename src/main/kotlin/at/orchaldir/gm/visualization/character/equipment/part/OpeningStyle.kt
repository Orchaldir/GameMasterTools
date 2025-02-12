package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

data class OpeningConfig(
    val buttonRadius: SizeConfig<Factor>,
    val spaceBetweenColumns: SizeConfig<Factor>,
    val zipperWidth: Factor,
)

fun visualizeOpening(
    state: CharacterRenderState,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    openingStyle: OpeningStyle,
) {
    when (openingStyle) {
        NoOpening -> doNothing()
        is DoubleBreasted -> {
            val spaceBetweenColumns = state.config.equipment.opening.spaceBetweenColumns
                .convert(openingStyle.spaceBetweenColumns)
            val half = spaceBetweenColumns * 0.5f

            visualizeButtons(state, aabb, x - half, topY, bottomY, openingStyle.buttons)
            visualizeButtons(state, aabb, x + half, topY, bottomY, openingStyle.buttons)
        }

        is SingleBreasted -> visualizeButtons(state, aabb, x, topY, bottomY, openingStyle.buttons)
        is Zipper -> visualizeZipper(state, aabb, x, topY, bottomY, openingStyle)
    }
}

fun visualizeButtons(
    state: CharacterRenderState,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    buttons: ButtonColumn,
) {
    val options = NoBorder(buttons.button.color.toRender())
    val distance = bottomY - topY
    val step = distance / buttons.count.toFloat()
    var y = topY + step * HALF
    val radius = aabb.convertHeight(state.config.equipment.opening.buttonRadius.convert(buttons.button.size))
    val layer = state.renderer.getLayer(ABOVE_EQUIPMENT_LAYER)

    for (i in 0..<buttons.count.toInt()) {
        val center = aabb.getPoint(x, y)
        layer.renderCircle(center, radius, options)
        y += step
    }
}

fun visualizeZipper(
    state: CharacterRenderState,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    zipper: Zipper,
) {
    val width = aabb.convertHeight(state.config.equipment.opening.zipperWidth)
    val options = LineOptions(zipper.color.toRender(), width)
    val top = aabb.getPoint(x, topY)
    val bottom = aabb.getPoint(x, bottomY)

    state.renderer.getLayer(ABOVE_EQUIPMENT_LAYER).renderLine(listOf(top, bottom), options)
}