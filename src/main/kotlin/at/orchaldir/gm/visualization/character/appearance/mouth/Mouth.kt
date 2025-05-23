package at.orchaldir.gm.visualization.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.mouth.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.MOUTH_LAYER
import at.orchaldir.gm.visualization.character.appearance.beard.visualizeBeard

data class MouthConfig(
    private val simpleWidth: SizeConfig<Factor>,
    val simpleHeight: Factor,
    val femaleHeight: Factor,
    val y: Factor,
) {
    fun getSimpleWidth(size: Size) = simpleWidth.convert(size)

    fun getWidth(mouth: Mouth): Factor {
        val width = when (mouth) {
            is FemaleMouth -> mouth.width
            NoMouth -> Size.Medium
            is NormalMouth -> mouth.width
            is Beak -> error("Beak is not supported!")
            is Snout -> error("Snout is not supported!")
        }

        return getSimpleWidth(width)
    }

    fun getHeight(mouth: Mouth) = when (mouth) {
        is FemaleMouth -> femaleHeight
        NoMouth -> ZERO
        is NormalMouth -> simpleHeight
        is Beak -> error("Beak is not supported!")
        is Snout -> error("Snout is not supported!")
    }

    fun getBottomY(mouth: Mouth) = y + getHeight(mouth) * 0.5f

    fun getTopY(mouth: Mouth) = y - getHeight(mouth) * 0.5f

}

fun visualizeMouth(state: CharacterRenderState, head: Head) {
    when (head.mouth) {
        NoMouth -> doNothing()
        is NormalMouth -> {
            visualizeMaleMouth(state, head.mouth.width)
            visualizeBeard(state, head, head.mouth.beard)
        }

        is FemaleMouth -> visualizeFemaleMouth(state, head.mouth)
        is Beak -> visualizeBeak(state, head.mouth)
        is Snout -> visualizeSnout(state, head.mouth)
    }
}

fun visualizeMaleMouth(
    state: CharacterRenderState,
    size: Size,
) {
    if (!state.renderFront) {
        return
    }

    val aabb = state.aabb
    val config = state.config
    val center = aabb.getPoint(CENTER, config.head.mouth.y)
    val width = aabb.convertWidth(config.head.mouth.getSimpleWidth(size))
    val height = aabb.convertHeight(config.head.mouth.simpleHeight)
    val mouthAabb = AABB.fromCenter(center, Size2d(width, height))
    val option = NoBorder(Color.Black.toRender())

    state.renderer.getLayer(MOUTH_LAYER).renderRectangle(mouthAabb, option)
}

private fun visualizeFemaleMouth(
    state: CharacterRenderState,
    mouth: FemaleMouth,
) {
    if (!state.renderFront) {
        return
    }

    val aabb = state.aabb
    val config = state.config.head.mouth
    val options = NoBorder(mouth.color.toRender())
    val width = config.getSimpleWidth(mouth.width)
    val halfHeight = config.femaleHeight * 0.5f
    val (left, right) = aabb.getMirroredPoints(width, config.y)
    val (topLeft, topRight) =
        aabb.getMirroredPoints(width * 0.5f, config.y - halfHeight)
    val (bottomLeft, bottomRight) =
        aabb.getMirroredPoints(width * 0.5f, config.y + halfHeight)
    val cupidsBow = aabb.getPoint(CENTER, config.y - halfHeight * 0.5f)
    val polygon = Polygon2d(listOf(left, bottomLeft, bottomRight, right, topRight, cupidsBow, topLeft))

    state.renderer.getLayer(MOUTH_LAYER).renderPolygon(polygon, options)
}

