package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
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

fun visualizeEyes(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.eyes) {
        NoEyes -> doNothing()
        is OneEye -> {
            val center = aabb.getPoint(CENTER, config.head.eyeY)
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.eye.eyeShape, head.eyes.size)

            visualizeEye(renderer, config, center, size, head.eyes.eye)
        }

        is TwoEyes -> {
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.eye.eyeShape, Size.Small)
            val distance = config.head.eyes.getDistanceBetweenEyes()
            val (left, right) = aabb.getMirroredPoints(distance, config.head.eyeY)

            visualizeEye(renderer, config, left, size, head.eyes.eye)
            visualizeEye(renderer, config, right, size, head.eyes.eye)
        }
    }
}

private fun visualizeEye(renderer: Renderer, config: RenderConfig, center: Point2d, size: Size2d, eye: Eye) {
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(renderer, config, eyeAabb, eye)
}

private fun visualizeEye(renderer: Renderer, config: RenderConfig, aabb: AABB, eye: Eye) {
    visualizeEyeShape(renderer, aabb, eye.eyeShape, eye.scleraColor)
    visualizePupil(renderer, config, aabb, eye.pupilShape, eye.pupilColor)
}

private fun visualizeEyeShape(renderer: Renderer, aabb: AABB, eyeShape: EyeShape, color: Color) {
    val options = NoBorder(color.toRender())

    when (eyeShape) {
        EyeShape.Almond -> renderer.renderPointedOval(aabb, options)
        EyeShape.Circle -> renderer.renderCircle(aabb, options)
        EyeShape.Ellipse -> renderer.renderEllipse(aabb, options)
    }
}

private fun visualizePupil(renderer: Renderer, config: RenderConfig, aabb: AABB, pupilShape: PupilShape, color: Color) {
    val options = NoBorder(color.toRender())
    val slitWidth = aabb.size.width * config.head.eyes.slitFactor.value

    when (pupilShape) {
        PupilShape.Circle -> renderer.renderCircle(aabb.shrink(config.head.eyes.pupilFactor), options)
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