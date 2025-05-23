package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
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
            EyeShape.Almond -> Size2d(diameter, diameter * almondHeight)
            EyeShape.Circle -> square(diameter)
            EyeShape.Ellipse -> Size2d(diameter, diameter * ellipseHeight)
        }
    }

    fun getDistanceBetweenEyes(size: Size = Size.Medium) = distanceBetweenEyes.convert(size)

    fun getOneEyeCenter(aabb: AABB, size: Size) = aabb.getPoint(
        CENTER,
        if (size == Size.Small) {
            twoEyesY
        } else {
            oneEyeY
        }
    )

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
            val center = config.head.eyes.getOneEyeCenter(aabb, head.eyes.size)
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

fun visualizeEye(state: CharacterRenderState, center: Point2d, eye: Eye, layer: Int) {
    val size = state.config.head.eyes.getEyeSize(state.aabb, eye.getShape(), Size.Small)
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(state.copy(aabb = eyeAabb), eye, layer)
}

private fun visualizeEye(state: CharacterRenderState, center: Point2d, size: Size2d, eye: Eye, layer: Int = 0) {
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(state.copy(aabb = eyeAabb), eye, layer)
}

private fun visualizeEye(state: CharacterRenderState, eye: Eye, layer: Int) {
    when (eye) {
        is NormalEye -> {
            visualizeEyeShape(state, eye.eyeShape, eye.scleraColor, layer)
            visualizePupil(state, eye.pupilShape, eye.pupilColor, layer)
        }

        is SimpleEye -> visualizeEyeShape(state, eye.eyeShape, eye.color, layer)
    }
}

private fun visualizeEyeShape(state: CharacterRenderState, eyeShape: EyeShape, color: Color, layer: Int) {
    val options = NoBorder(color.toRender())
    val renderer = state.renderer.getLayer(layer)

    when (eyeShape) {
        EyeShape.Almond -> renderer.renderPointedOval(state.aabb, options)
        EyeShape.Circle -> renderer.renderCircle(state.aabb, options)
        EyeShape.Ellipse -> renderer.renderEllipse(state.aabb, options)
    }
}

private fun visualizePupil(state: CharacterRenderState, pupilShape: PupilShape, color: Color, layer: Int) {
    val options = NoBorder(color.toRender())
    val aabb = state.aabb
    val slitWidth = aabb.size.width * state.config.head.eyes.slitFactor
    val renderer = state.renderer.getLayer(layer)

    when (pupilShape) {
        PupilShape.Circle -> renderer.renderCircle(aabb.shrink(state.config.head.eyes.pupilFactor), options)
        PupilShape.HorizontalSlit -> {
            val slitAABB = AABB.fromCenter(aabb.getCenter(), Size2d(aabb.size.width, slitWidth))
            renderer.renderPointedOval(slitAABB, options)
        }

        PupilShape.VerticalSlit -> {
            val slitAABB = AABB.fromCenter(aabb.getCenter(), Size2d(slitWidth, aabb.size.height))
            renderer.renderPointedOval(slitAABB, options)
        }
    }
}