package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Gloves
import at.orchaldir.gm.core.model.item.style.GloveStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.HIGHER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.LOWER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.visualizeHands

fun visualizeGloves(
    state: RenderState,
    body: Body,
    gloves: Gloves,
) {
    val options = FillAndBorder(gloves.fill.toRender(), state.config.line)

    when (gloves.style) {
        GloveStyle.Hand -> doNothing()
        GloveStyle.Half -> visualizeGloveSleeves(state, options, body, HALF)
        GloveStyle.Full -> visualizeGloveSleeves(state, options, body, FULL)
    }

    visualizeHands(state, body, options)
}

private fun visualizeGloveSleeves(
    state: RenderState,
    options: RenderOptions,
    body: Body,
    length: Factor,
) {
    val (left, right) = state.config.body.getArmStarts(state.aabb, body)
    val armSize = state.config.body.getArmSize(state.aabb, body)
    val gloveSize = Size2d(armSize.width, armSize.height * length.value)
    val down = Point2d(0.0f, armSize.height * (1.0f - length.value))
    val centerLeft = left + down
    val centerRight = right + down
    val leftAabb = AABB(centerLeft, gloveSize)
    val rightAabb = AABB(centerRight, gloveSize)
    val layer = if (state.renderFront) {
        HIGHER_EQUIPMENT_LAYER
    } else {
        LOWER_EQUIPMENT_LAYER
    }

    state.renderer.renderRectangle(leftAabb, options, layer)
    state.renderer.renderRectangle(rightAabb, options, layer)
}
