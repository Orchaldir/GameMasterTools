package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.eye.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig

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

    fun getEyeSize(config: ICharacterConfig<Head>, shape: EyeShape, size: Size = Size.Small): Size2d {
        val diameter = config.headAABB().convertWidth(diameter.convert(size))

        return when (shape) {
            EyeShape.Almond -> Size2d(diameter, diameter * almondHeight)
            EyeShape.Circle -> square(diameter)
            EyeShape.Ellipse -> Size2d(diameter, diameter * ellipseHeight)
        }
    }

    fun getDistanceBetweenEyes(size: Size = Size.Medium) = distanceBetweenEyes.convert(size)

    fun getOneEyeCenter(config: ICharacterConfig<Head>, size: Size) = config.headAABB().getPoint(
        CENTER,
        if (size == Size.Small) {
            twoEyesY
        } else {
            oneEyeY
        }
    )

    fun getTwoEyesCenter(config: ICharacterConfig<Head>) = config
        .headAABB()
        .getMirroredPoints(getDistanceBetweenEyes(), twoEyesY)
}

fun visualizeEyes(state: CharacterRenderState<Head>) {
    if (!state.renderFront) {
        return
    }

    val config = state.config

    when (val eyes = state.get().eyes) {
        NoEyes -> doNothing()
        is OneEye -> {
            val center = config.head.eyes.getOneEyeCenter(state, eyes.size)
            val size = config.head.eyes.getEyeSize(state, eyes.eye.getShape(), eyes.size)

            visualizeEye(state, center, size, eyes.eye)
        }

        is TwoEyes -> {
            val size = config.head.eyes.getEyeSize(state, eyes.eye.getShape(), Size.Small)
            val (left, right) = config.head.eyes.getTwoEyesCenter(state)

            visualizeEye(state, left, size, eyes.eye)
            visualizeEye(state, right, size, eyes.eye)
        }
    }
}

fun visualizeEye(state: CharacterRenderState<Head>, center: Point2d, eye: Eye, layer: Int) {
    val size = state.config.head.eyes.getEyeSize(state, eye.getShape(), Size.Small)
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(state, eyeAabb, eye, layer)
}

private fun visualizeEye(state: CharacterRenderState<Head>, center: Point2d, size: Size2d, eye: Eye, layer: Int = 0) {
    val eyeAabb = AABB.fromCenter(center, size)

    visualizeEye(state, eyeAabb, eye, layer)
}

private fun visualizeEye(state: CharacterRenderState<Head>, aabb: AABB, eye: Eye, layer: Int) {
    when (eye) {
        is NormalEye -> {
            visualizeEyeShape(state, aabb, eye.eyeShape, eye.scleraColor, layer)
            visualizePupil(state, aabb, eye.pupilShape, eye.pupilColor, layer)
        }

        is SimpleEye -> visualizeEyeShape(state, aabb, eye.eyeShape, eye.color, layer)
    }
}

private fun visualizeEyeShape(
    state: CharacterRenderState<Head>,
    aabb: AABB,
    eyeShape: EyeShape,
    color: Color,
    layer: Int,
) {
    val options = NoBorder(color.toRender())
    val renderer = state.renderer.getLayer(layer)

    when (eyeShape) {
        EyeShape.Almond -> renderer.renderPointedOval(aabb, options)
        EyeShape.Circle -> renderer.renderCircle(aabb, options)
        EyeShape.Ellipse -> renderer.renderEllipse(aabb, options)
    }
}

private fun visualizePupil(
    state: CharacterRenderState<Head>,
    aabb: AABB,
    pupilShape: PupilShape,
    color: Color,
    layer: Int,
) {
    val options = NoBorder(color.toRender())
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