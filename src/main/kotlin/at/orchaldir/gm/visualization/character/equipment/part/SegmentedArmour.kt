package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodyArmour
import at.orchaldir.gm.core.model.item.equipment.style.SegmentedArmour
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.equipment.getOuterwearBottomY

fun visualizeSegmentedArmour(
    state: CharacterRenderState,
    body: Body,
    armour: BodyArmour,
    style: SegmentedArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeSegmentedArmourBody(state, renderer, body, armour, style)
}

private fun visualizeSegmentedArmourBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: BodyArmour,
    style: SegmentedArmour,
) {
    val clipping = createClippingPolygonForArmourBody(state, body)
    val clippingName = state.renderer.createClipping(clipping)
    val color = style.segment.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val maxWidthFactor = state.config.body.getMaxWidth(body.bodyShape)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length, THREE_QUARTER)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)
    val segmentHeight = (bottom - start).y / style.rows.toFloat()
    var position = torso.getPoint(CENTER, START).addHeight(segmentHeight / 2)

    repeat(style.rows) {
        val aabb = AABB.fromWidthAndHeight(position, maxWidth, segmentHeight)

        renderer.renderRectangle(aabb, options)

        position = position.addHeight(segmentHeight)
    }
}
