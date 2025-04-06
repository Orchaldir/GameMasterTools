package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.renderPolygon
import at.orchaldir.gm.visualization.renderRoundedBuilder
import at.orchaldir.gm.visualization.renderRoundedPolygon

data class HairConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val sidePartX: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
    val width: Factor,
)

fun visualizeHair(state: CharacterRenderState, head: Head) {
    when (head.hair) {
        NoHair -> doNothing()
        is NormalHair -> visualizeNormalHair(state, head.hair)
    }
}

private fun visualizeNormalHair(state: CharacterRenderState, hair: NormalHair) {
    val config = state.config
    val options = FillAndBorder(hair.color.toRender(), config.line)
    val hasHeadwear = state.hasEquipped(EquipmentSlot.HeadSlot)

    if (!state.renderFront) {
        when (hair.style) {
            ShavedHair -> return
            Spiked -> if (!hasHeadwear) {
                visualizeSpikedHair(state, options, FULL)
            }

            else -> doNothing()
        }

        state.renderer.getLayer().renderRectangle(state.aabb, options)

        return
    }

    when (hair.style) {
        is BowlCut -> visualizeRectangleHair(state, options, config.head.hair.width, START)
        is BuzzCut -> visualizeRectangleHair(state, options, FULL, START)
        is FlatTop -> visualizeFlatTop(state, options, config)
        is MiddlePart -> visualizeMiddlePart(state, options, CENTER)
        ShavedHair -> doNothing()

        is SidePart -> when (hair.style.side) {
            Side.Left -> visualizeMiddlePart(
                state,
                options,
                END - config.head.hair.sidePartX
            )

            Side.Right -> visualizeMiddlePart(
                state,
                options,
                config.head.hair.sidePartX
            )
        }

        is Spiked -> if (!hasHeadwear) {
            visualizeSpikedHair(state, options, state.config.head.hairlineY)
        }
    }
}

private fun visualizeFlatTop(
    state: CharacterRenderState,
    options: FillAndBorder,
    config: CharacterRenderConfig,
) {
    if (state.hasEquipped(EquipmentSlot.HeadSlot)) {
        return
    }

    visualizeRectangleHair(state, options, FULL, config.head.hair.flatTopY)
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
        state.renderer.getLayer(),
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

    renderRoundedPolygon(state.renderer.getLayer(), options, polygon.corners)
}

private fun visualizeSpikedHair(
    state: CharacterRenderState,
    options: FillAndBorder,
    bottomY: Factor,
) {
    val config = state.config.head.hair
    val (bottomLeft, bottomRight) = state.aabb.getMirroredPoints(config.width, bottomY)
    val (topLeft, topRight) = state.aabb.getMirroredPoints(config.width, config.spikedY)
    val points = mutableListOf<Point2d>()
    val spikes = 6
    val topPoints = SegmentSplitter
        .fromStartAndEnd(topLeft, topRight, spikes)
        .getCorners()
    val down = Point2d(0.0f, state.aabb.convertHeight(config.spikedHeight).toMeters())
    val builder = Polygon2dBuilder()

    for (i in 0..<spikes) {
        val spike = topPoints[i]
        val nextSpike = topPoints[i + 1]
        val middle = (spike + nextSpike) / 2.0f
        val bottomBetweenSpikes = middle + down

        builder
            .addRightPoint(spike, true)
            .addRightPoint(bottomBetweenSpikes, true)
    }

    builder
        .addRightPoint(topRight, true)
        .addRightPoint(bottomRight, true)
        .addRightPoint(state.aabb, CENTER, state.config.head.hairlineY - Factor.fromNumber(0.05f))
        .addLeftPoint(bottomLeft, true)

    renderRoundedBuilder(state.renderer, builder, options, 0)
}
