package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.RectangularShape
import at.orchaldir.gm.utils.math.unit.Volume
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HIGHER_EQUIPMENT_LAYER

data class BeltConfig(
    val bandHeight: Factor,
    val bandThicknessRelativeToHeight: Factor,
    val buckleHeight: SizeConfig<Factor>,
    val buckleThicknessRelativeToHeight: Factor,
    val holeRadius: SizeConfig<Factor>,
    val y: Factor,
) {
    fun getBandCenter(config: ICharacterConfig<Body>) =
        config.torsoAABB().getPoint(CENTER, y)

    fun getBandSize(config: ICharacterConfig<Body>): Size2d {
        val hipWidth = config.equipment().pants.getHipWidth(config)

        return config.torsoAABB().size.scale(hipWidth, bandHeight)
    }

    fun getBandVolume(config: ICharacterConfig<Body>): Volume {
        val bandSize = getBandSize(config)
        val bandThickness = bandSize.height * bandThicknessRelativeToHeight

        return bandSize.calculateVolumeOfPrism(bandThickness) * config.body().getTorsoCircumferenceFactor()
    }

    fun getBeltHoleRadius(config: ICharacterConfig<Body>, size: Size) =
        config.torsoAABB().convertHeight(holeRadius.convert(size))

    fun getBuckleHeight(config: ICharacterConfig<Body>, size: Size) =
        config.torsoAABB().convertHeight(buckleHeight.convert(size))

    fun getBuckleVolume(config: ICharacterConfig<Body>, shape: BuckleShape, size: Size): Volume {
        val height = getBuckleHeight(config, size)
        val half = height / 2.0f
        val double = height * 2.0f
        val thickness = height * buckleThicknessRelativeToHeight

        return when (shape) {
            BuckleShape.Circle -> CircularShape.Circle.calculateVolumeOfPrism(half, thickness)
            BuckleShape.Frame -> Volume.fromCube(double - half, height - half, thickness)
            BuckleShape.Plate -> RectangularShape.Ellipse.calculateVolumeOfPrism(Size2d(double, height), thickness)
            BuckleShape.Rectangle -> Volume.fromCube(double, height, thickness)
            BuckleShape.Ring -> CircularShape.Circle.calculateVolumeOfPrism(half, thickness) -
                    CircularShape.Circle.calculateVolumeOfPrism(half / 2, thickness)
            BuckleShape.Square -> Volume.fromCube(height, height, thickness)
        }
    }
}

fun visualizeBelt(
    state: CharacterRenderState<Body>,
    belt: Belt,
) {
    visualizeBeltBand(state, belt)
    visualizeBeltHoles(state, belt.holes)
    visualizeBuckle(state, belt.buckle)
}

private fun visualizeBeltBand(
    state: CharacterRenderState<Body>,
    belt: Belt,
) {
    val fill = belt.strap.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val beltConfig = state.config.equipment.belt
    val bandAabb = AABB.fromCenter(
        beltConfig.getBandCenter(state),
        beltConfig.getBandSize(state),
    )
    val polygon = Polygon2dBuilder()
        .addRectangle(bandAabb)
        .build()

    state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)
        .renderPolygon(polygon, options)
}

private fun visualizeBeltHoles(
    state: CharacterRenderState<Body>,
    holes: BeltHoles,
) {
    val beltConfig = state.config.equipment.belt

    when (holes) {
        NoBeltHoles -> doNothing()
        is OneRowOfBeltHoles -> visualizeRowOfBeltHoles(state, beltConfig.y, holes.size, holes.border)
        is TwoRowsOfBeltHoles -> {
            val diffY = beltConfig.bandHeight / 6.0f
            visualizeRowOfBeltHoles(state, beltConfig.y + diffY, Size.Small, holes.border)
            visualizeRowOfBeltHoles(state, beltConfig.y - diffY, Size.Small, holes.border)
        }

        is ThreeRowsOfBeltHoles -> {
            val diffY = beltConfig.bandHeight / 4.0f
            visualizeRowOfBeltHoles(state, beltConfig.y + diffY, Size.Small, holes.border)
            visualizeRowOfBeltHoles(state, beltConfig.y, Size.Small, holes.border)
            visualizeRowOfBeltHoles(state, beltConfig.y - diffY, Size.Small, holes.border)
        }
    }
}

private fun visualizeRowOfBeltHoles(
    state: CharacterRenderState<Body>,
    y: Factor,
    size: Size,
    border: Color?,
) {
    val radius = state.config.equipment.belt.getBeltHoleRadius(state, size)
    val options = if (border != null) {
        FillAndBorder(Color.Black.toRender(), LineOptions(border.toRender(), radius / 2.0f))
    } else {
        NoBorder(Color.Black.toRender())
    }
    val hipWidth = state.config.equipment.pants.getHipWidth(state)
    val splitter = SegmentSplitter.fromStartAndEnd(state.torsoAABB().getMirroredPoints(hipWidth, y), 10)
    val renderer = state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)

    splitter.getCenters().forEach { center ->
        renderer.renderCircle(center, radius, options)
    }
}

private fun visualizeBuckle(
    state: CharacterRenderState<Body>,
    buckle: Buckle,
) {
    if (!state.renderFront) {
        return
    }

    when (buckle) {
        NoBuckle -> doNothing()
        is SimpleBuckle -> visualizeSimpleBuckle(state, buckle)
    }
}

private fun visualizeSimpleBuckle(
    state: CharacterRenderState<Body>,
    buckle: SimpleBuckle,
) {
    val fill = buckle.part.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val beltConfig = state.config.equipment.belt
    val height = beltConfig.getBuckleHeight(state, buckle.size)
    val half = height / 2.0f
    val double = height * 2.0f
    val center = state.torsoAABB().getPoint(CENTER, beltConfig.y)
    val renderer = state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)

    when (buckle.shape) {
        BuckleShape.Circle -> renderer.renderCircle(center, half, options)
        BuckleShape.Frame -> renderer.renderHollowRectangle(center, double, height, half / 2, options)
        BuckleShape.Plate -> renderer.renderEllipse(center, height, half, options)
        BuckleShape.Rectangle -> renderer.renderRectangle(AABB.fromWidthAndHeight(center, double, height), options)
        BuckleShape.Ring -> renderer.renderRing(center, half, half / 2.0f, options)
        BuckleShape.Square -> renderer.renderRectangle(AABB.fromCenter(center, height), options)
    }

}
