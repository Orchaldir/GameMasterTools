package at.orchaldir.gm.visualization.equipment.part

import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.LineOptions
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
        is SingleBreasted -> doNothing()
        is Zipper -> visualizeZipper(state, aabb, x, topY, bottomY, openingStyle)
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