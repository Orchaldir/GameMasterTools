package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.renderRoundedPolygon

fun visualizeShortHair(state: CharacterRenderState, hair: NormalHair, shortHair: ShortHairCut) {
    val config = state.config
    val options = config.getLineOptions(hair.color)

    if (!state.renderFront) {
        when (shortHair.style) {
            Shaved -> return

            else -> doNothing()
        }

        state.renderer.getLayer().renderRectangle(state.aabb, options)

        return
    }

    when (shortHair.style) {
        BowlCut -> visualizeRectangleHair(state, options, config.head.hair.width, START)
        BuzzCut -> visualizeRectangleHair(state, options, FULL, START)
        MiddlePart -> visualizeMiddlePart(state, options, CENTER)
        Shaved -> doNothing()
        SidePartLeft -> visualizeMiddlePart(
            state,
            options,
            END - config.head.hair.sidePartX
        )

        SidePartRight -> visualizeMiddlePart(
            state,
            options,
            config.head.hair.sidePartX
        )
    }
}

private fun visualizeMiddlePart(
    state: CharacterRenderState,
    options: FillAndBorder,
    x: Factor,
) {
    val aabb = state.aabb
    val config = state.config
    val (bottomLeft, bottomRight) = aabb.getMirroredPoints(config.head.hair.width, config.head.hairlineY)
    val (topLeft, topRight) = aabb.getMirroredPoints(config.head.hair.width, START)
    val bottomCenter = aabb.getPoint(x, config.head.hairlineY)
    val topCenter = aabb.getPoint(x, START)

    renderRoundedPolygon(
        state.renderer,
        options,
        listOf(
            topLeft,
            topLeft,
            bottomLeft,
            bottomLeft,
            bottomCenter,
            topCenter,
            bottomCenter,
            bottomRight,
            bottomRight,
            topRight,
            topRight,
        )
    )
}

private fun visualizeRectangleHair(
    state: CharacterRenderState,
    options: FillAndBorder,
    width: Factor,
    topY: Factor,
    topWidth: Factor = FULL,
) {
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, width * topWidth, topY, true)
        .addMirroredPoints(state.aabb, width, state.config.head.hairlineY, true)
        .addLeftPoint(state.aabb, CENTER, state.config.head.hairlineY - Factor.fromNumber(0.05f))
        .build()

    renderRoundedPolygon(state.renderer, options, polygon.corners)
}
