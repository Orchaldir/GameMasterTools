package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig

data class EyesConfig(
    val smallDiameter: Float,
    val mediumDiameter: Float,
    val largeDiameter: Float,
) {

    fun getEyeSize(aabb: AABB, size: Size = Size.Small): Size2d {
        val height = aabb.size.height
        return when (size) {
            Size.Small -> square(height * smallDiameter)
            Size.Medium -> square(height * mediumDiameter)
            Size.Large -> square(height * largeDiameter)
        }
    }
}

fun visualizeEyes(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.eyes) {
        NoEyes -> doNothing()
        is OneEye -> {
            val center = aabb.getPoint(0.5f, config.head.eyeY)
            val size = config.head.eyes.getEyeSize(aabb, head.eyes.size)
            val eyeAabb = AABB.fromCenter(center, size)

            visualizeEye(renderer, config, eyeAabb, head.eyes.eye)
        }

        is TwoEyes -> doNothing()
    }
}

fun visualizeEye(renderer: Renderer, config: RenderConfig, aabb: AABB, eye: Eye) {
    visualizeEyeShape(renderer, config, aabb, eye.eyeShape, eye.scleraColor)
}

fun visualizeEyeShape(renderer: Renderer, config: RenderConfig, aabb: AABB, eyeShape: EyeShape, color: Color) {
    val options = NoBorder(color.toRender())

    when (eyeShape) {
        EyeShape.Almond -> TODO()
        EyeShape.Circle -> renderer.renderCircle(aabb, options)
        EyeShape.Ellipse -> renderer.renderEllipse(aabb, options)
    }
}