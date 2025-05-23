package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Gloves
import at.orchaldir.gm.core.model.item.equipment.style.GloveStyle
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HIGHER_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.getArmLayer
import at.orchaldir.gm.visualization.character.appearance.visualizeHands

fun visualizeGloves(
    state: CharacterRenderState,
    body: Body,
    gloves: Gloves,
) {
    val fill = gloves.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    when (gloves.style) {
        GloveStyle.Hand -> doNothing()
        GloveStyle.Half -> visualizeGloveSleeves(state, options, body, HALF)
        GloveStyle.Full -> visualizeGloveSleeves(state, options, body, FULL)
    }

    visualizeHands(state, body, options)
}

private fun visualizeGloveSleeves(
    state: CharacterRenderState,
    options: RenderOptions,
    body: Body,
    length: Factor,
) {
    val (left, right) = state.config.body.getArmStarts(state.aabb, body)
    val armSize = state.config.body.getArmSize(state.aabb, body)
    val gloveSize = Size2d(armSize.width, armSize.height * length)
    val down = Point2d.yAxis(armSize.height * (FULL - length))
    val centerLeft = left + down
    val centerRight = right + down
    val leftAabb = AABB(centerLeft, gloveSize)
    val rightAabb = AABB(centerRight, gloveSize)
    val layer = state.renderer.getLayer(getArmLayer(HIGHER_EQUIPMENT_LAYER, state.renderFront))

    layer.renderRectangle(leftAabb, options)
    layer.renderRectangle(rightAabb, options)
}
