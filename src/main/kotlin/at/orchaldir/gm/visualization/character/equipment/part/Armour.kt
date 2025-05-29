package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso

fun calculateArmourScaleWidth(
    state: CharacterRenderState,
    body: Body,
    torso: AABB,
    columns: Int,
): Distance {
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)

    return hipWidth / columns
}

fun createClippingPolygonForArmourBody(
    state: CharacterRenderState,
    body: Body,
): Polygon2d {
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)
    val half = hipWidth / 2
    val bottom = state.aabb.getPoint(CENTER, END)
    val builder = Polygon2dBuilder()
        .addPoints(bottom.minusWidth(half), bottom.addWidth(half))

    addHip(state.config, builder, state.aabb, body)
    addTorso(state, body, builder)

    return builder.build()
}
