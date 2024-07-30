package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.ABOVE_EQUIPMENT_LAYER

fun visualizeOpening(
    state: RenderState,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    openingStyle: OpeningStyle,
) {
    when (openingStyle) {
        NoOpening -> doNothing()
        is DoubleBreasted -> doNothing()
        is SingleBreasted -> visualizeButtons(state, aabb, x, topY, bottomY, openingStyle.buttons)
        is Zipper -> visualizeZipper(state, aabb, x, topY, bottomY, openingStyle)
    }
}

fun visualizeButtons(
    state: RenderState,
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

    for (i in 0..<buttons.count.toInt()) {
        val center = aabb.getPoint(x, y)
        state.renderer.renderCircle(center, Distance(0.01f), options, ABOVE_EQUIPMENT_LAYER)
        y += step
    }
}

fun visualizeZipper(
    state: RenderState,
    aabb: AABB,
    x: Factor,
    topY: Factor,
    bottomY: Factor,
    zipper: Zipper,
) {
    val options = LineOptions(zipper.color.toRender(), 0.002f)
    val top = aabb.getPoint(x, topY)
    val bottom = aabb.getPoint(x, bottomY)

    state.renderer.renderLine(listOf(top, bottom), options, ABOVE_EQUIPMENT_LAYER)
}