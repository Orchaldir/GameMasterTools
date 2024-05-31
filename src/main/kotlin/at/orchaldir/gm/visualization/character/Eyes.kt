package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.SizeConfig

data class EyesConfig(
    private val diameter: SizeConfig,
    private val distanceBetweenEyes: SizeConfig,
) {

    fun getEyeSize(aabb: AABB, size: Size = Size.Small): Size2d {
        val height = aabb.size.height
        return square(height * diameter.convert(size))
    }

    fun getDistanceBetweenEyes(size: Size = Size.Medium): Factor {
        return Factor(distanceBetweenEyes.convert(size))
    }
}

fun visualizeEyes(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.eyes) {
        NoEyes -> doNothing()
        is OneEye -> {
            val center = aabb.getPoint(Factor(0.5f), config.head.eyeY)
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.size)

            visualizeEye(renderer, config, center, size, head.eyes.eye)
        }

        is TwoEyes -> {
            val size = config.head.eyes.getEyeSize(aabb, Size.Small)
            val distance = config.head.eyes.getDistanceBetweenEyes()
            val (left, right) = aabb.getMirroredPoints(distance, config.head.eyeY)

            visualizeEye(renderer, config, left, size, head.eyes.eye)
            visualizeEye(renderer, config, right, size, head.eyes.eye)
        }
    }
}

fun visualizeEye(renderer: Renderer, config: RenderConfig, center: Point2d, size: Size2d, eye: Eye) {
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(renderer, config, eyeAabb, eye)
}

fun visualizeEye(renderer: Renderer, config: RenderConfig, aabb: AABB, eye: Eye) {
    visualizeEyeShape(renderer, config, aabb, eye.eyeShape, eye.scleraColor)
}

fun visualizeEyeShape(renderer: Renderer, config: RenderConfig, aabb: AABB, eyeShape: EyeShape, color: Color) {
    val options = NoBorder(color.toRender())

    when (eyeShape) {
        EyeShape.Almond -> renderer.renderPointedOval(aabb, options)
        EyeShape.Circle -> renderer.renderCircle(aabb, options)
        EyeShape.Ellipse -> renderer.renderEllipse(aabb, options)
    }
}