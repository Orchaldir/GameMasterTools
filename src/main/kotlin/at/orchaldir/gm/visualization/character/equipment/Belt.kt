package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HIGHER_EQUIPMENT_LAYER

data class BeltConfig(
    val bandHeight: Factor,
    val buckleHeight: SizeConfig<Factor>,
    val holeRadius: SizeConfig<Factor>,
    val y: Factor,
)

fun visualizeBelt(
    state: CharacterRenderState,
    body: Body,
    belt: Belt,
) {
    val torsoAABB = state.config.body.getTorsoAabb(state.aabb, body)

    visualizeBeltBand(state, body, torsoAABB, belt)
    visualizeBeltHoles(state, body, torsoAABB, belt.holes)
    visualizeBuckle(state, torsoAABB, belt.buckle)
}

private fun visualizeBeltBand(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    belt: Belt,
) {
    val fill = belt.strap.getFill(state.state)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val hipWidth = state.config.equipment.pants.getHipWidth(state.config.body, body)
    val beltConfig = state.config.equipment.belt
    val bandAabb = torsoAABB.createSubAabb(CENTER, beltConfig.y, hipWidth, beltConfig.bandHeight)
    val polygon = Polygon2dBuilder()
        .addRectangle(bandAabb)
        .build()

    state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)
        .renderPolygon(polygon, options)
}

private fun visualizeBeltHoles(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    holes: BeltHoles,
) {
    val beltConfig = state.config.equipment.belt

    when (holes) {
        NoBeltHoles -> doNothing()
        is OneRowOfBeltHoles -> visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y, holes.size, holes.border)
        is TwoRowsOfBeltHoles -> {
            val diffY = beltConfig.bandHeight / 6.0f
            visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y + diffY, Size.Small, holes.border)
            visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y - diffY, Size.Small, holes.border)
        }

        is ThreeRowsOfBeltHoles -> {
            val diffY = beltConfig.bandHeight / 4.0f
            visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y + diffY, Size.Small, holes.border)
            visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y, Size.Small, holes.border)
            visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y - diffY, Size.Small, holes.border)
        }
    }
}

private fun visualizeRowOfBeltHoles(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    y: Factor,
    size: Size,
    border: Color?,
) {
    val factor = state.config.equipment.belt.holeRadius.convert(size)
    val radius = torsoAABB.convertHeight(factor)
    val options = if (border != null) {
        FillAndBorder(Color.Black.toRender(), LineOptions(border.toRender(), radius / 2.0f))
    } else {
        NoBorder(Color.Black.toRender())
    }
    val hipWidth = state.config.equipment.pants.getHipWidth(state.config.body, body)
    val splitter = SegmentSplitter.fromStartAndEnd(torsoAABB.getMirroredPoints(hipWidth, y), 10)
    val renderer = state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)

    splitter.getCenters().forEach { center ->
        renderer.renderCircle(center, radius, options)
    }
}

private fun visualizeBuckle(
    state: CharacterRenderState,
    torsoAABB: AABB,
    buckle: Buckle,
) {
    if (!state.renderFront) {
        return
    }

    when (buckle) {
        NoBuckle -> doNothing()
        is SimpleBuckle -> visualizeSimpleBuckle(state, torsoAABB, buckle)
    }
}

private fun visualizeSimpleBuckle(
    state: CharacterRenderState,
    torsoAABB: AABB,
    buckle: SimpleBuckle,
) {
    val fill = buckle.part.getFill(state.state)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val beltConfig = state.config.equipment.belt
    val distance = torsoAABB.convertHeight(beltConfig.buckleHeight.convert(buckle.size))
    val half = distance / 2.0f
    val double = distance * 2.0f
    val center = torsoAABB.getPoint(CENTER, beltConfig.y)
    val renderer = state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)

    when (buckle.shape) {
        BuckleShape.Circle -> renderer.renderCircle(center, half, options)
        BuckleShape.Frame -> renderer.renderHollowRectangle(center, double, distance, half / 2, options)
        BuckleShape.Plate -> renderer.renderEllipse(center, distance, half, options)
        BuckleShape.Rectangle -> renderer.renderRectangle(AABB.fromWidthAndHeight(center, double, distance), options)
        BuckleShape.Ring -> renderer.renderRing(center, half, half / 2.0f, options)
        BuckleShape.Square -> renderer.renderRectangle(AABB.fromCenter(center, distance), options)
    }

}
