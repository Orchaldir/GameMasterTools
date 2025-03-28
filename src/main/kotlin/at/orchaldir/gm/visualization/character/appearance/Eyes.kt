package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class EyesConfig(
    val oneEyeY: Factor,
    val twoEyesY: Factor,
    private val diameter: SizeConfig<Factor>,
    private val distanceBetweenEyes: SizeConfig<Factor>,
    private val almondHeight: Factor,
    private val ellipseHeight: Factor,
    val pupilFactor: Factor,
    val slitFactor: Factor,
) {

    fun getEyeSize(aabb: AABB, shape: EyeShape, size: Size = Size.Small): Size2d {
        val diameter = aabb.convertWidth(diameter.convert(size))

        return when (shape) {
            EyeShape.Almond -> Size2d(diameter, diameter * almondHeight.toNumber())
            EyeShape.Circle -> square(diameter)
            EyeShape.Ellipse -> Size2d(diameter, diameter * ellipseHeight.toNumber())
        }
    }

    fun getDistanceBetweenEyes(size: Size = Size.Medium) = distanceBetweenEyes.convert(size)

    fun getOneEyeCenter(aabb: AABB) = aabb.getPoint(CENTER, oneEyeY)
    fun getTwoEyesCenter(aabb: AABB) = aabb
        .getMirroredPoints(getDistanceBetweenEyes(), twoEyesY)
}

fun visualizeEyes(state: CharacterRenderState, head: Head) {
    if (!state.renderFront) {
        return
    }

    val config = state.config
    val aabb = state.aabb

    when (head.eyes) {
        NoEyes -> doNothing()
        is OneEye -> {
            val center = config.head.eyes.getOneEyeCenter(aabb)
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.eye.getShape(), head.eyes.size)

            visualizeEye(state, center, size, head.eyes.eye)
        }

        is TwoEyes -> {
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.eye.getShape(), Size.Small)
            val (left, right) = config.head.eyes.getTwoEyesCenter(aabb)

            visualizeEye(state, left, size, head.eyes.eye)
            visualizeEye(state, right, size, head.eyes.eye)
        }
    }
}

private fun visualizeEye(state: CharacterRenderState, center: Point2d, size: Size2d, eye: Eye) {
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(state.copy(aabb = eyeAabb), eye)
}

private fun visualizeEye(state: CharacterRenderState, eye: Eye) {
    when (eye) {
        is NormalEye -> {
            visualizeEyeShape(state, eye.eyeShape, eye.scleraColor)
            visualizePupil(state, eye.pupilShape, eye.pupilColor)
        }

        is SimpleEye -> visualizeEyeShape(state, eye.eyeShape, eye.color)
    }
}

private fun visualizeEyeShape(state: CharacterRenderState, eyeShape: EyeShape, color: Color) {
    val options = NoBorder(color.toRender())
    val layer = state.renderer.getLayer()

    when (eyeShape) {
        EyeShape.Almond -> layer.renderPointedOval(state.aabb, options)
        EyeShape.Circle -> layer.renderCircle(state.aabb, options)
        EyeShape.Ellipse -> layer.renderEllipse(state.aabb, options)
    }
}

private fun visualizePupil(state: CharacterRenderState, pupilShape: PupilShape, color: Color) {
    val options = NoBorder(color.toRender())
    val aabb = state.aabb
    val slitWidth = aabb.size.width * state.config.head.eyes.slitFactor.toNumber()
    val layer = state.renderer.getLayer()

    when (pupilShape) {
        PupilShape.Circle -> layer.renderCircle(aabb.shrink(state.config.head.eyes.pupilFactor), options)
        PupilShape.HorizontalSlit -> {
            val slitAABB = AABB.fromCenter(aabb.getCenter(), Size2d(aabb.size.width, slitWidth))
            layer.renderPointedOval(slitAABB, options)
        }

        PupilShape.VerticalSlit -> {
            val slitAABB = AABB.fromCenter(aabb.getCenter(), Size2d(slitWidth, aabb.size.height))
            layer.renderPointedOval(slitAABB, options)
        }
    }
}