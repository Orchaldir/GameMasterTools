package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.SizeConfig

data class EyesConfig(
    private val diameter: SizeConfig,
    private val distanceBetweenEyes: SizeConfig,
    private val almondHeight: Factor,
    private val ellipseHeight: Factor,
    val pupilFactor: Factor,
    val slitFactor: Factor,
) {

    fun getEyeSize(aabb: AABB, shape: EyeShape, size: Size = Size.Small): Size2d {
        val diameter = aabb.size.height * diameter.convert(size)

        return when (shape) {
            EyeShape.Almond -> Size2d(diameter, diameter * almondHeight.value)
            EyeShape.Circle -> square(diameter)
            EyeShape.Ellipse -> Size2d(diameter, diameter * ellipseHeight.value)
        }
    }

    fun getDistanceBetweenEyes(size: Size = Size.Medium): Factor {
        return Factor(distanceBetweenEyes.convert(size))
    }
}

fun visualizeEyes(state: RenderState, head: Head) {
    if (!state.renderFront) {
        return
    }

    val config = state.config
    val aabb = state.aabb

    when (head.eyes) {
        NoEyes -> doNothing()
        is OneEye -> {
            val center = aabb.getPoint(CENTER, config.head.eyeY)
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.eye.eyeShape, head.eyes.size)

            visualizeEye(state, center, size, head.eyes.eye)
        }

        is TwoEyes -> {
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.eye.eyeShape, Size.Small)
            val distance = config.head.eyes.getDistanceBetweenEyes()
            val (left, right) = aabb.getMirroredPoints(distance, config.head.eyeY)

            visualizeEye(state, left, size, head.eyes.eye)
            visualizeEye(state, right, size, head.eyes.eye)
        }
    }
}

private fun visualizeEye(state: RenderState, center: Point2d, size: Size2d, eye: Eye) {
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(state.copy(aabb = eyeAabb), eye)
}

private fun visualizeEye(state: RenderState, eye: Eye) {
    visualizeEyeShape(state, eye.eyeShape, eye.scleraColor)
    visualizePupil(state, eye.pupilShape, eye.pupilColor)
}

private fun visualizeEyeShape(state: RenderState, eyeShape: EyeShape, color: Color) {
    val options = NoBorder(color.toRender())

    when (eyeShape) {
        EyeShape.Almond -> state.renderer.renderPointedOval(state.aabb, options)
        EyeShape.Circle -> state.renderer.renderCircle(state.aabb, options)
        EyeShape.Ellipse -> state.renderer.renderEllipse(state.aabb, options)
    }
}

private fun visualizePupil(state: RenderState, pupilShape: PupilShape, color: Color) {
    val options = NoBorder(color.toRender())
    val aabb = state.aabb
    val slitWidth = aabb.size.width * state.config.head.eyes.slitFactor.value

    when (pupilShape) {
        PupilShape.Circle -> state.renderer.renderCircle(aabb.shrink(state.config.head.eyes.pupilFactor), options)
        PupilShape.HorizontalSlit -> {
            val slitAABB = AABB.fromCenter(aabb.getCenter(), Size2d(aabb.size.width, slitWidth))
            state.renderer.renderPointedOval(slitAABB, options)
        }

        PupilShape.VerticalSlit -> {
            val slitAABB = AABB.fromCenter(aabb.getCenter(), Size2d(slitWidth, aabb.size.height))
            state.renderer.renderPointedOval(slitAABB, options)
        }
    }
}