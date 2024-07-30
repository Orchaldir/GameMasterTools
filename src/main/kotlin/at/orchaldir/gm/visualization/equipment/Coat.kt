package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.core.model.item.style.DoubleBreasted
import at.orchaldir.gm.core.model.item.style.NoOpening
import at.orchaldir.gm.core.model.item.style.SingleBreasted
import at.orchaldir.gm.core.model.item.style.Zipper
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.LineOptions
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.equipment.part.visualizeTorso

fun visualizeCoat(
    state: RenderState,
    body: Body,
    coat: Coat,
) {
    val options = FillAndBorder(coat.fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, coat.sleeveStyle)
    visualizeTorso(state, options, body, coat.necklineStyle)

    if (state.renderFront) {
        visualizeOpening(state, body, coat)
    }
}

fun visualizeOpening(
    state: RenderState,
    body: Body,
    coat: Coat,
) {
    val necklineHeight = state.config.equipment.neckline.getHeight(coat.necklineStyle)
    val topY = necklineHeight
    val bottomY = FULL
    val torsoAabb = state.config.body.getTorsoAabb(state.aabb, body)

    when (coat.openingStyle) {
        NoOpening -> doNothing()
        is DoubleBreasted -> doNothing()
        is SingleBreasted -> doNothing()
        is Zipper -> visualizeZipper(state, torsoAabb, HALF, topY, bottomY, coat.openingStyle)
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
    val options = LineOptions(zipper.color.toRender(), 0.5f)
    val top = aabb.getPoint(x, topY)
    val bottom = aabb.getPoint(x, bottomY)

    state.renderer.renderLine(listOf(top, bottom), options)
}