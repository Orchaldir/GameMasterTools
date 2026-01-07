package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.Bow
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Line2dBuilder
import at.orchaldir.gm.utils.math.subdivideLine
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.QUARTER_CIRCLE
import at.orchaldir.gm.utils.renderer.TransformRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.GripConfig
import at.orchaldir.gm.visualization.character.equipment.part.visualizeGrip

data class BowConfig(
    val grip: GripConfig,
    val gripHeight: SizeConfig<Factor>,
    val gripThickness: Factor,
    val heightToWidth: Factor,
    val thicknessCenter: Factor,
) {

    fun calculateBowThickness(height: Distance) = height * thicknessCenter

    fun calculateCenterAabb(grip: BowGrip, height: Distance): AABB {
        val size = when (grip) {
            NoBowGrip -> Size.Small
            is SimpleBowGrip -> grip.size
        }

        return calculateGripAabb(size, height, FULL)
    }

    fun calculateGripAabb(grip: SimpleBowGrip, height: Distance) =
        calculateGripAabb(grip.size, height, gripThickness)


    private fun calculateGripAabb(size: Size, height: Distance, thickness: Factor): AABB {
        val gripHeight = calculateGripHeight(size, height)
        val thickness = calculateBowThickness(height) * thickness

        return AABB.fromWidthAndHeight(Point2d(), thickness, gripHeight)
    }

    fun calculateGripHeight(size: Size, height: Distance) = height * gripHeight.convert(size)

}

fun visualizeBow(
    state: CharacterRenderState<Body>,
    bow: Bow,
    set: Set<BodySlot>,
) {
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)

    state.getLayer(HELD_EQUIPMENT_LAYER)
        .createGroup(hand) { movedRenderer ->
            movedRenderer.createGroup(QUARTER_CIRCLE) { rotatedRender ->
                visualizeBow(state, rotatedRender, bow)
            }
        }
}

private fun visualizeBow(
    state: CharacterRenderState<Body>,
    renderer: TransformRenderer,
    bow: Bow,
) {
    val height = state.fullAABB.convertHeight(bow.height)
    val config = state.config.equipment.bow
    val width = height * config.heightToWidth
    val centerX = -width / 2.0f
    val bowAabb = AABB.fromWidthAndHeight(Point2d.xAxis(centerX), width, height)
    val centerAabb = config.calculateCenterAabb(bow.grip, height)

    visualizeBowShape(state, config, renderer, bowAabb, centerAabb, bow)
    visualizeBowGrip(state, config, renderer, height, bow.grip)
}

private fun visualizeBowGrip(
    state: CharacterRenderState<Body>,
    config: BowConfig,
    renderer: TransformRenderer,
    height: Distance,
    grip: BowGrip,
) {
    when (grip) {
        NoBowGrip -> doNothing()
        is SimpleBowGrip -> visualizeGrip(state, renderer, config.grip, grip.grip, config.calculateGripAabb(grip, height))
    }
}

private fun visualizeBowShape(
    state: CharacterRenderState<Body>,
    config: BowConfig,
    renderer: TransformRenderer,
    bowAabb: AABB,
    centerAabb: AABB,
    bow: Bow,
) {
    val fill = bow.fill.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    when (bow.shape) {
        BowShape.Angular -> {
            val polygon = Polygon2dBuilder()
                .addLeftPoint(bowAabb, START, START)
                .addMirroredPoints(centerAabb, FULL, START)
                .addMirroredPoints(centerAabb, FULL, END)
                .addLeftPoint(bowAabb, START, END)
                .build()

            renderer.renderPolygon(polygon, options)
        }
        BowShape.Straight -> {
            val line = Line2dBuilder()
                .addPoint(centerAabb, CENTER, START)
                .addPoint(bowAabb, END, START)
                .addPoint(bowAabb, START, START)
                .build()
            val curve = subdivideLine(line, 3)

            renderer.renderLine(curve, state.lineOptions())
        }
    }
}


